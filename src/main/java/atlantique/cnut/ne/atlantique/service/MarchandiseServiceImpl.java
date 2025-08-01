package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.MarchandiseDto;
import atlantique.cnut.ne.atlantique.dto.MarchandiseItemDto;
import atlantique.cnut.ne.atlantique.dto.VehiculeItemDto;
import atlantique.cnut.ne.atlantique.entity.*;
import atlantique.cnut.ne.atlantique.enums.MarchandiseStatus;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.repository.MarchandiseRepository;
import atlantique.cnut.ne.atlantique.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarchandiseServiceImpl implements MarchandiseService {

    private final MarchandiseRepository marchandiseRepository;
    private final UtilisateurService utilisateurService;
    private final UtilisateurRepository utilisateurRepository;
    private final DailyCashRegisterService dailyCashRegisterService;
    private final GroupeService groupeService;
    private final FileStorageService fileStorageService;

    @Override
    public MarchandiseDto createMarchandise(MarchandiseDto marchandiseDto, MultipartFile blFile, MultipartFile declarationDouaneFile, MultipartFile factureCommercialeFile) {
        log.info("Creating new marchandise");
        Marchandise marchandise = convertToEntity(marchandiseDto);
        performCalculations(marchandise);

        if (blFile != null && !blFile.isEmpty()) {
            String filename = fileStorageService.save(blFile);
            marchandise.setBlFile(filename);
        }
        if (declarationDouaneFile != null && !declarationDouaneFile.isEmpty()) {
            String filename = fileStorageService.save(declarationDouaneFile);
            marchandise.setDeclarationDouaneFile(filename);
        }
        if (factureCommercialeFile != null && !factureCommercialeFile.isEmpty()) {
            String filename = fileStorageService.save(factureCommercialeFile);
            marchandise.setFactureCommercialeFile(filename);
        }

        Marchandise savedMarchandise = marchandiseRepository.save(marchandise);
        return convertToDto(savedMarchandise);
    }

    @Override
    public MarchandiseDto updateMarchandise(String id, MarchandiseDto marchandiseDto, MultipartFile blFile, MultipartFile declarationDouaneFile, MultipartFile factureCommercialeFile) {
        log.info("Updating marchandise with id: {}", id);
        return marchandiseRepository.findById(id).map(existingMarchandise -> {

            if (!getCurrentUserRoles().contains("ADMIN") || !getCurrentUserRoles().contains("OPERATEUR") && (existingMarchandise.getStatus() != MarchandiseStatus.BROUILLON && existingMarchandise.getStatus() != MarchandiseStatus.REJETE)) {
                throw new IllegalArgumentException("La marchandise avec le statut '" + existingMarchandise.getStatus().getDisplayName() + "' ne peut pas être modifiée par un non-ADMIN.");
            }

            if (marchandiseDto.getStatus() != null && marchandiseDto.getStatus() != existingMarchandise.getStatus()) {
                // Votre logique existante pour changer le statut...
                if (marchandiseDto.getStatus() == MarchandiseStatus.SOUMIS_POUR_VALIDATION) {
                    if (existingMarchandise.getStatus() != MarchandiseStatus.BROUILLON && existingMarchandise.getStatus() != MarchandiseStatus.REJETE) {
                        throw new IllegalArgumentException("Seules les marchandises en statut BROUILLON ou REJETE peuvent être soumises à validation.");
                    }
                    existingMarchandise.setStatus(MarchandiseStatus.SOUMIS_POUR_VALIDATION);
                    existingMarchandise.setSubmittedByUserId(getCurrentUserId());
                    existingMarchandise.setSubmissionDate(new Date());
                } else if (marchandiseDto.getStatus() == MarchandiseStatus.VALIDE || marchandiseDto.getStatus() == MarchandiseStatus.REJETE) {
                    if (existingMarchandise.getStatus() != MarchandiseStatus.SOUMIS_POUR_VALIDATION) {
                        throw new IllegalArgumentException("Seules les marchandises en statut SOUMIS_POUR_VALIDATION peuvent être validées ou rejetées.");
                    }
                    String caissierId = getCurrentUserId();
                    if (!dailyCashRegisterService.isCashRegisterOpen(caissierId, LocalDate.now())) {
                        throw new IllegalArgumentException("La caisse de l'utilisateur est clôturée. Impossible de valider la marchandise.");
                    }

                    existingMarchandise.setStatus(marchandiseDto.getStatus());
                    existingMarchandise.setValidatedByUserId(caissierId);
                    existingMarchandise.setValidationDate(new Date());

                    if (marchandiseDto.getStatus() == MarchandiseStatus.VALIDE) {
                        Utilisateur caissier = utilisateurRepository.findById(caissierId)
                                .orElseThrow(() -> new ResourceNotFoundException("Caissier authentifié non trouvé."));
                        try {
                            double totalQuittance = Double.parseDouble(existingMarchandise.getTotalQuittance());
                            caissier.setCashBalance(caissier.getCashBalance() + totalQuittance);
                            utilisateurRepository.save(caissier);
                            log.info("Balance du caissier {} créditée de {} pour la marchandise {}", caissierId, totalQuittance, id);
                            dailyCashRegisterService.recordDeposit(caissierId, totalQuittance);
                        } catch (NumberFormatException e) {
                            log.error("Erreur de formatage du totalQuittance pour la marchandise {}: {}", id, existingMarchandise.getTotalQuittance(), e);
                            throw new IllegalArgumentException("Le champ totalQuittance de la marchandise n'est pas un nombre valide.");
                        }
                    }
                }
            }

            Marchandise updatedMarchandise = updateEntityFromDto(existingMarchandise, marchandiseDto);
            this.validateCargaisonDates(updatedMarchandise);
            performCalculations(updatedMarchandise);

            if (blFile != null && !blFile.isEmpty()) {
                if (existingMarchandise.getBlFile() != null) { fileStorageService.delete(existingMarchandise.getBlFile()); }
                String filename = fileStorageService.save(blFile);
                updatedMarchandise.setBlFile(filename);
            } else if (marchandiseDto.getBlFile() == null) {
                if (existingMarchandise.getBlFile() != null) { fileStorageService.delete(existingMarchandise.getBlFile()); }
                updatedMarchandise.setBlFile(null);
            }

            if (declarationDouaneFile != null && !declarationDouaneFile.isEmpty()) {
                if (existingMarchandise.getDeclarationDouaneFile() != null) { fileStorageService.delete(existingMarchandise.getDeclarationDouaneFile()); }
                String filename = fileStorageService.save(declarationDouaneFile);
                updatedMarchandise.setDeclarationDouaneFile(filename);
            } else if (marchandiseDto.getDeclarationDouaneFile() == null) {
                if (existingMarchandise.getDeclarationDouaneFile() != null) { fileStorageService.delete(existingMarchandise.getDeclarationDouaneFile()); }
                updatedMarchandise.setDeclarationDouaneFile(null);
            }

            if (factureCommercialeFile != null && !factureCommercialeFile.isEmpty()) {
                if (existingMarchandise.getFactureCommercialeFile() != null) { fileStorageService.delete(existingMarchandise.getFactureCommercialeFile()); }
                String filename = fileStorageService.save(factureCommercialeFile);
                updatedMarchandise.setFactureCommercialeFile(filename);
            } else if (marchandiseDto.getFactureCommercialeFile() == null) {
                if (existingMarchandise.getFactureCommercialeFile() != null) { fileStorageService.delete(existingMarchandise.getFactureCommercialeFile()); }
                updatedMarchandise.setFactureCommercialeFile(null);
            }


            Marchandise savedMarchandise = marchandiseRepository.save(updatedMarchandise);
            return convertToDto(savedMarchandise);
        }).orElseThrow(() -> new ResourceNotFoundException("Marchandise non trouvée avec l'ID: " + id));
    }

    private void validateCargaisonDates(Marchandise marchandise) {
        if (marchandise.getDateDepartureNavireCargaison() == null || marchandise.getDateArriveNavireCargaison() == null) {
            throw new IllegalArgumentException("Les dates de départ et d'arrivée du navire sont obligatoires.");
        }

        if (marchandise.getDateDepartureNavireCargaison().after(marchandise.getDateArriveNavireCargaison()) ||
                marchandise.getDateDepartureNavireCargaison().equals(marchandise.getDateArriveNavireCargaison())) {
            throw new IllegalArgumentException("La date de départ du navire doit être antérieure à la date d'arrivée.");
        }
    }

    private void performCalculations(Marchandise marchandise) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(marchandise.getIdUtilisateur());
        Groupe groupeConfig = null;
        if (utilisateurOpt.isPresent() && utilisateurOpt.get().getIdGroupe() != null) {
            groupeConfig = groupeService.findGroupeById(utilisateurOpt.get().getIdGroupe())
                    .orElse(null);
        }

        double prixBeStandard = (groupeConfig != null && groupeConfig.getPrixBeStandard() != null) ? groupeConfig.getPrixBeStandard() : 50000.0;
        double visaVehiculeMoins5000kg = (groupeConfig != null && groupeConfig.getVisaVehiculeMoins5000kg() != null) ? groupeConfig.getVisaVehiculeMoins5000kg() : 15000.0;
        double visaVehiculePlus5000kg = (groupeConfig != null && groupeConfig.getVisaVehiculePlus5000kg() != null) ? groupeConfig.getVisaVehiculePlus5000kg() : 20000.0;


        if (marchandise.isExoneration() || marchandise.isRegularisation()) {
            marchandise.setBe("0");
            marchandise.setCoutBsc("0");
            marchandise.setTotalQuittance("0");
            marchandise.setVisa("0");
            marchandise.setTotalBePrice("0");
            return;
        }

        // Réinitialisation des totaux
        double coutBsc = 0.0;
        double visa = 0.0;
        double totalQuittance = 0.0;
        double be = 0.0;
        double totalBePriceCalculated = 0.0;


        String conteneurType = marchandise.getConteneur();
        String typeMarchandise = marchandise.getTypeMarchandiseSelect();

        if ("vehicule".equalsIgnoreCase(typeMarchandise)) {
            List<VehiculeItem> vehiculeItems = marchandise.getVehiculesGroupage();
            if (vehiculeItems != null && !vehiculeItems.isEmpty()) {
                double totalPoids = 0.0;
                for (VehiculeItem item : vehiculeItems) {
                    double itemPoids = 0.0;
                    try {
                        itemPoids = Double.parseDouble(item.getPoids());
                    } catch (NumberFormatException e) {
                        log.warn("Poids invalide pour un véhicule de groupage.");
                    }

                    double itemVisa = 0.0;
                    if (itemPoids < 5000) {
                        itemVisa = visaVehiculeMoins5000kg;
                    } else {
                        itemVisa = visaVehiculePlus5000kg;
                    }

                    double itemCoutBsc = 50000.0;
                    double itemTotal = itemCoutBsc + itemVisa;

                    item.setVisa(String.valueOf(itemVisa));
                    item.setCoutBsc(String.valueOf(itemCoutBsc));
                    item.setTotal(String.valueOf(itemTotal));

                    coutBsc += itemCoutBsc;
                    visa += itemVisa;
                    totalQuittance += itemTotal;
                }
            } else {
                double poids = 0.0;
                try {
                    poids = Double.parseDouble(marchandise.getPoids());
                } catch (NumberFormatException e) {
                    log.warn("Poids invalide pour un véhicule unique.");
                }

                if (poids < 5000) {
                    visa = visaVehiculeMoins5000kg;
                } else {
                    visa = visaVehiculePlus5000kg;
                }
                coutBsc = 50000.0;
                totalQuittance = coutBsc + visa;
            }

            marchandise.setBe("0");
            marchandise.setTotalBePrice("0");
        }
        else {
            if ("simple".equalsIgnoreCase(conteneurType)) {
                be = 0;
                coutBsc = prixBeStandard;
            } else if ("groupage".equalsIgnoreCase(conteneurType)) {
                be = 0;
                if (marchandise.getMarchandisesGroupage() != null) {
                    coutBsc = prixBeStandard * marchandise.getMarchandisesGroupage().size();
                } else {
                    coutBsc = 0.0;
                }
            } else if ("vrac".equalsIgnoreCase(conteneurType)) {
                try {
                    double poids = Double.parseDouble(marchandise.getPoids());
                    be = Math.floor(poids / 30000);
                    if (be < 1 && poids > 0) {
                        be = 1;
                    } else if (poids == 0) {
                        be = 0;
                    }
                    coutBsc = (poids / 1000) * 3000;
                    totalBePriceCalculated = be * prixBeStandard;
                } catch (NumberFormatException e) {
                    be = 0;
                    coutBsc = 0;
                    totalBePriceCalculated = 0;
                }
            }

            visa = 0;
            totalQuittance = coutBsc + totalBePriceCalculated + visa;
            marchandise.setBe(String.valueOf(be));
            marchandise.setTotalBePrice(String.valueOf(totalBePriceCalculated));
        }

        marchandise.setCoutBsc(String.valueOf(coutBsc));
        marchandise.setVisa(String.valueOf(visa));
        marchandise.setTotalQuittance(String.valueOf(totalQuittance));
    }



    @Override
    public MarchandiseDto getMarchandiseById(String id) {
        return marchandiseRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Marchandise non trouvée avec l'ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MarchandiseDto> findAllMarchandisesPaginated(Pageable pageable) {
        String currentUserId = this.getCurrentUserId();
        Set<String> currentUserRoles = this.getCurrentUserRoles();

        Page<Marchandise> page;

        if (currentUserRoles.contains("ADMIN")) {
            page = marchandiseRepository.findAll(pageable);
        } else if (currentUserRoles.contains("OPERATEUR")) {
            page = marchandiseRepository.findByIdUtilisateur(currentUserId, pageable);
        } else if (currentUserRoles.contains("CAISSIER")) {
            page = marchandiseRepository.findByLieuEmissionCargaison(this.getCurrentUserSiteId(), pageable);
        } else if (currentUserRoles.contains("CSITE")) {
            String currentUserSiteId = getCurrentUserSiteId();
            page = marchandiseRepository.findByLieuEmissionCargaison(currentUserSiteId, pageable);
        } else {
            page = Page.empty(pageable);
        }

        return page.map(this::convertToDto);
    }

    @Override
    public List<MarchandiseDto> getMarchandises(){
        String currentUserId = getCurrentUserId();
        Set<String> currentUserRoles = getCurrentUserRoles();

        List<Marchandise> list;

        if (currentUserRoles.contains("ADMIN")) {
            list = marchandiseRepository.findAll();
        } else if (currentUserRoles.contains("OPERATEUR")) {
            list = marchandiseRepository.findByIdUtilisateur(currentUserId);
        } else if (currentUserRoles.contains("CAISSIER")) {
            list = marchandiseRepository.findForCaissierCombinedList(currentUserId);
        } else if (currentUserRoles.contains("CSITE")) {
            String currentUserSiteId = getCurrentUserSiteId();
            list = marchandiseRepository.findByLieuEmissionCargaison(currentUserSiteId);
        } else {
            list = List.of();
        }

        return list.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteMarchandise(String id) {
        if (!marchandiseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Marchandise non trouvée avec l'ID : " + id);
        }
        marchandiseRepository.deleteById(id);
    }

    @Override
    @Transactional
    public MarchandiseDto submitMarchandiseForValidation(String id) {
        return marchandiseRepository.findById(id)
                .map(marchandise -> {
                    if (marchandise.getStatus() != MarchandiseStatus.BROUILLON &&
                            marchandise.getStatus() != MarchandiseStatus.REJETE) {
                        throw new IllegalArgumentException("Seules les marchandises en statut BROUILLON ou REJETE peuvent être soumises à validation.");
                    }
                    marchandise.setStatus(MarchandiseStatus.SOUMIS_POUR_VALIDATION);
                    marchandise.setSubmittedByUserId(getCurrentUserId());
                    marchandise.setSubmissionDate(new Date());
                    Marchandise updatedMarchandise = marchandiseRepository.save(marchandise);
                    return convertToDto(updatedMarchandise);
                }).orElseThrow(() -> new ResourceNotFoundException("Marchandise non trouvée avec l'ID: " + id));
    }

    @Override
    @Transactional
    public MarchandiseDto validateMarchandise(String id, boolean isValid) {
        return marchandiseRepository.findById(id)
                .map(marchandise -> {
                    if (marchandise.getStatus() != MarchandiseStatus.SOUMIS_POUR_VALIDATION) {
                        throw new IllegalArgumentException("Seules les marchandises en statut SOUMIS_POUR_VALIDATION peuvent être validées ou rejetées.");
                    }

                    String caissierId = getCurrentUserId();
                    if (!dailyCashRegisterService.isCashRegisterOpen(caissierId, LocalDate.now())) {
                        throw new IllegalArgumentException("La caisse de l'utilisateur est clôturée. Impossible de valider la marchandise.");
                    }

                    if (isValid) {
                        marchandise.setStatus(MarchandiseStatus.VALIDE);
                        marchandise.setValidatedByUserId(caissierId);
                        marchandise.setValidationDate(new Date());

                        Utilisateur caissier = utilisateurRepository.findById(caissierId)
                                .orElseThrow(() -> new ResourceNotFoundException("Caissier authentifié non trouvé."));

                        try {
                            double totalQuittance = Double.parseDouble(marchandise.getTotalQuittance());
                            caissier.setCashBalance(caissier.getCashBalance() + totalQuittance);
                            utilisateurRepository.save(caissier);
                            log.info("Balance du caissier {} créditée de {} pour la marchandise {}", caissierId, totalQuittance, id);

                            dailyCashRegisterService.recordDeposit(caissierId, totalQuittance);

                        } catch (NumberFormatException e) {
                            log.error("Erreur de formatage du totalQuittance pour la marchandise {}: {}", id, marchandise.getTotalQuittance(), e);
                            throw new IllegalArgumentException("Le champ totalQuittance de la marchandise n'est pas un nombre valide.");
                        }

                    } else {
                        marchandise.setStatus(MarchandiseStatus.REJETE);
                        marchandise.setValidatedByUserId(caissierId);
                        marchandise.setValidationDate(new Date());
                    }

                    Marchandise updatedMarchandise = marchandiseRepository.save(marchandise);
                    return convertToDto(updatedMarchandise);
                }).orElseThrow(() -> new ResourceNotFoundException("Marchandise non trouvée avec l'ID: " + id));
    }

    private Marchandise convertToEntity(MarchandiseDto marchandiseDto) {
        Marchandise marchandise = new Marchandise();
        if (marchandiseDto.getId() != null) {
            marchandise.setId(marchandiseDto.getId());
        }
        Optional<Utilisateur> utilisateur= this.utilisateurRepository.findById(marchandiseDto.getIdUtilisateur());

        marchandise.setTypeMarchandiseSelect(marchandiseDto.getTypeMarchandiseSelect());
        marchandise.setCaf(marchandiseDto.getCaf());
        marchandise.setPoids(marchandiseDto.getPoids());
        marchandise.setType(marchandiseDto.getType());
        marchandise.setNombreColis(marchandiseDto.getNombreColis());
        marchandise.setNumeroChassis(marchandiseDto.getNumeroChassis());
        marchandise.setNumeroDouane(marchandiseDto.getNumeroDouane());
        marchandise.setNombreConteneur(marchandiseDto.getNombreConteneur());
        marchandise.setRegularisation(marchandiseDto.isRegularisation());
        marchandise.setExoneration(marchandiseDto.isExoneration());
        marchandise.setConteneur(marchandiseDto.getConteneur());
        marchandise.setTypeConteneur(marchandiseDto.getTypeConteneur());
        marchandise.setVolume(marchandiseDto.getVolume());
        marchandise.setObservation(marchandiseDto.getObservation());
        marchandise.setNumVoyage(marchandiseDto.getNumVoyage());
        marchandise.setTotalQuittance(marchandiseDto.getTotalQuittance());
        marchandise.setBe(marchandiseDto.getBe());
        marchandise.setTotalBePrice(marchandiseDto.getTotalBePrice());
        marchandise.setVisa(marchandiseDto.getVisa());
        marchandise.setCoutBsc(marchandiseDto.getCoutBsc()); // Mappage pour coutBsc
        marchandise.setTotalBePrice(marchandiseDto.getTotalBePrice());

        marchandise.setIdNatureMarchandise(marchandiseDto.getIdNatureMarchandise());
        marchandise.setIdArmateur(marchandiseDto.getIdArmateur());
        marchandise.setIdTransitaire(marchandiseDto.getIdTransitaire());
        marchandise.setIdImportateur(marchandiseDto.getIdImportateur());
        marchandise.setIdUtilisateur(marchandiseDto.getIdUtilisateur());

        marchandise.setStatus(marchandiseDto.getStatus());
        marchandise.setSubmittedByUserId(utilisateur.get().getId());
        marchandise.setValidatedByUserId(marchandiseDto.getValidatedByUserId());
        marchandise.setSubmissionDate(new Date());
        marchandise.setValidationDate(marchandiseDto.getValidationDate());

        marchandise.setIdBl(marchandiseDto.getIdBl());
        marchandise.setManifesteCargaison(marchandiseDto.getManifesteCargaison());
        marchandise.setIdSiteCargaison(utilisateur.get().getIdPays());
        marchandise.setIdConsignataireCargaison(marchandiseDto.getIdConsignataireCargaison());
        marchandise.setTransporteurCargaison(marchandiseDto.getTransporteurCargaison());
        marchandise.setLieuEmissionCargaison(utilisateur.get().getIdSite());
        marchandise.setIdNavireCargaison(marchandiseDto.getIdNavireCargaison());
        marchandise.setDateArriveNavireCargaison(marchandiseDto.getDateArriveNavireCargaison());
        marchandise.setDateDepartureNavireCargaison(marchandise.getDateDepartureNavireCargaison());
        marchandise.setIdPortEmbarquementCargaison(marchandiseDto.getIdPortEmbarquementCargaison());
        marchandise.setIdPortDebarquementCargaison(utilisateur.get().getIdSite());

        if (marchandiseDto.getMarchandisesGroupage() != null) {
            marchandise.setMarchandisesGroupage(marchandiseDto.getMarchandisesGroupage().stream()
                    .map(itemDto -> new MarchandiseItem(itemDto.getPoids(), itemDto.getNombreColis(), itemDto.getNumeroBl()))
                    .collect(Collectors.toList()));
        }

        return marchandise;
    }

    private Marchandise updateEntityFromDto(Marchandise existingMarchandise, MarchandiseDto marchandiseDto) {
        existingMarchandise.setTypeMarchandiseSelect(marchandiseDto.getTypeMarchandiseSelect());
        existingMarchandise.setCaf(marchandiseDto.getCaf());
        existingMarchandise.setPoids(marchandiseDto.getPoids());
        existingMarchandise.setType(marchandiseDto.getType());
        existingMarchandise.setNombreColis(marchandiseDto.getNombreColis());
        existingMarchandise.setNumeroChassis(marchandiseDto.getNumeroChassis());
        existingMarchandise.setNumeroDouane(marchandiseDto.getNumeroDouane());
        existingMarchandise.setNombreConteneur(marchandiseDto.getNombreConteneur());
        existingMarchandise.setRegularisation(marchandiseDto.isRegularisation());
        existingMarchandise.setExoneration(marchandiseDto.isExoneration());
        existingMarchandise.setConteneur(marchandiseDto.getConteneur());
        existingMarchandise.setTypeConteneur(marchandiseDto.getTypeConteneur());
        existingMarchandise.setVolume(marchandiseDto.getVolume());
        existingMarchandise.setObservation(marchandiseDto.getObservation());
        existingMarchandise.setNumVoyage(marchandiseDto.getNumVoyage());
        existingMarchandise.setCoutBsc(marchandiseDto.getCoutBsc());
        existingMarchandise.setTotalBePrice(marchandiseDto.getTotalBePrice());
        // existingMarchandise.setTotalQuittance(marchandiseDto.getTotalQuittance()); // Managed by calculations
        // existingMarchandise.setBe(marchandiseDto.getBe()); // Managed by calculations
        // existingMarchandise.setVisa(marchandiseDto.getVisa()); // Managed by calculations
        // existingMarchandise.setValidation(marchandiseDto.getValidation()); // Removed, replaced by status

        existingMarchandise.setStatus(marchandiseDto.getStatus());

        existingMarchandise.setIdNatureMarchandise(marchandiseDto.getIdNatureMarchandise());
        existingMarchandise.setIdArmateur(marchandiseDto.getIdArmateur());
        existingMarchandise.setIdTransitaire(marchandiseDto.getIdTransitaire());
        existingMarchandise.setIdImportateur(marchandiseDto.getIdImportateur());
        existingMarchandise.setIdUtilisateur(marchandiseDto.getIdUtilisateur());

        existingMarchandise.setIdBl(marchandiseDto.getIdBl());
        existingMarchandise.setManifesteCargaison(marchandiseDto.getManifesteCargaison());
        existingMarchandise.setIdConsignataireCargaison(marchandiseDto.getIdConsignataireCargaison());
        existingMarchandise.setTransporteurCargaison(marchandiseDto.getTransporteurCargaison());
        existingMarchandise.setIdNavireCargaison(marchandiseDto.getIdNavireCargaison());
        existingMarchandise.setDateArriveNavireCargaison(marchandiseDto.getDateArriveNavireCargaison());
        existingMarchandise.setDateDepartureNavireCargaison(marchandiseDto.getDateDepartureNavireCargaison());
        existingMarchandise.setIdPortEmbarquementCargaison(marchandiseDto.getIdPortEmbarquementCargaison());

        if (marchandiseDto.getMarchandisesGroupage() != null) {
            existingMarchandise.setMarchandisesGroupage(marchandiseDto.getMarchandisesGroupage().stream()
                    .map(itemDto -> new MarchandiseItem(itemDto.getPoids(), itemDto.getNombreColis(), itemDto.getNumeroBl()))
                    .collect(Collectors.toList()));
        }

        return existingMarchandise;
    }

    private MarchandiseDto convertToDto(Marchandise marchandise) {
        MarchandiseDto dto = new MarchandiseDto();
        dto.setId(marchandise.getId());
        dto.setTypeMarchandiseSelect(marchandise.getTypeMarchandiseSelect());
        dto.setCaf(marchandise.getCaf());
        dto.setPoids(marchandise.getPoids());
        dto.setType(marchandise.getType());
        dto.setNombreColis(marchandise.getNombreColis());
        dto.setNumeroChassis(marchandise.getNumeroChassis());
        dto.setNumeroDouane(marchandise.getNumeroDouane());
        dto.setNombreConteneur(marchandise.getNombreConteneur());
        dto.setRegularisation(marchandise.isRegularisation());
        dto.setExoneration(marchandise.isExoneration());
        dto.setConteneur(marchandise.getConteneur());
        dto.setTypeConteneur(marchandise.getTypeConteneur());
        dto.setVolume(marchandise.getVolume());
        dto.setObservation(marchandise.getObservation());
        dto.setNumVoyage(marchandise.getNumVoyage());
        dto.setTotalQuittance(marchandise.getTotalQuittance());
        dto.setBe(marchandise.getBe());
        dto.setVisa(marchandise.getVisa());
        dto.setTotalBePrice(marchandise.getCoutBsc()); // Assuming coutBsc is mapped to totalBePrice in DTO
        dto.setTotalBePrice(marchandise.getTotalBePrice());

        // Set new status fields
        dto.setStatus(marchandise.getStatus());
        dto.setSubmittedByUserId(marchandise.getSubmittedByUserId());
        dto.setValidatedByUserId(marchandise.getValidatedByUserId());
        dto.setSubmissionDate(marchandise.getSubmissionDate());
        dto.setValidationDate(marchandise.getValidationDate());

        // Existing fields
        dto.setIdNatureMarchandise(marchandise.getIdNatureMarchandise());
        dto.setIdArmateur(marchandise.getIdArmateur());
        dto.setIdTransitaire(marchandise.getIdTransitaire());
        dto.setIdImportateur(marchandise.getIdImportateur());
        dto.setIdUtilisateur(marchandise.getIdUtilisateur());

        dto.setIdBl(marchandise.getIdBl());
        dto.setManifesteCargaison(marchandise.getManifesteCargaison());
        dto.setIdConsignataireCargaison(marchandise.getIdConsignataireCargaison());
        dto.setTransporteurCargaison(marchandise.getTransporteurCargaison());
        dto.setIdNavireCargaison(marchandise.getIdNavireCargaison());
        dto.setDateArriveNavireCargaison(marchandise.getDateArriveNavireCargaison());
        dto.setDateDepartureNavireCargaison(marchandise.getDateDepartureNavireCargaison());
        dto.setIdPortEmbarquementCargaison(marchandise.getIdPortEmbarquementCargaison());

        dto.setCreationDate(marchandise.getCreationDate());
        dto.setModificationDate(marchandise.getModificationDate());

        dto.setBlFile(marchandise.getBlFile());
        dto.setDeclarationDouaneFile(marchandise.getDeclarationDouaneFile());
        dto.setFactureCommercialeFile(marchandise.getFactureCommercialeFile());

        dto.setNombreVehicule(marchandise.getNombreVehicule());
        if (marchandise.getVehiculesGroupage() != null) {
            dto.setVehiculesGroupage(marchandise.getVehiculesGroupage().stream()
                    .map(vehicule -> new VehiculeItemDto(vehicule.getPoids(), vehicule.getCaf(), vehicule.getNumeroChassis(), vehicule.getVisa(), vehicule.getNumeroDouane(), vehicule.getCoutBsc(), vehicule.getTotal()))
                    .collect(Collectors.toList()));
        } else {
            dto.setVehiculesGroupage(null);
        }

        if (marchandise.getMarchandisesGroupage() != null) {
            dto.setMarchandisesGroupage(marchandise.getMarchandisesGroupage().stream()
                    .map(item -> new MarchandiseItemDto(item.getPoids(), item.getNombreColis(), item.getNumeroBl()))
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private Optional<Utilisateur> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }
        String username = authentication.getName();
        return this.utilisateurRepository.findById(username);
    }

    private String getCurrentUserId() {
        return getAuthenticatedUser()
                .map(Utilisateur::getId)
                .orElseThrow(() -> new IllegalStateException("Aucun utilisateur authentifié."));
    }

    private Set<String> getCurrentUserRoles() {
        return getAuthenticatedUser()
                .map(user -> user.getAuthorites().stream()
                        .map(autorite -> autorite.getNom().toUpperCase())
                        .collect(Collectors.toSet()))
                .orElseThrow(() -> new IllegalStateException("Aucun rôle trouvé pour l'utilisateur authentifié."));
    }

    private String getCurrentUserSiteId() {
        return getAuthenticatedUser()
                .map(Utilisateur::getIdSite)
                .orElseThrow(() -> new IllegalStateException("Utilisateur authentifié non trouvé ou n'a pas d'ID de site."));
    }
}