package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.MarchandiseDto;
import atlantique.cnut.ne.atlantique.dto.MarchandiseItemDto;
import atlantique.cnut.ne.atlantique.entity.Marchandise;
import atlantique.cnut.ne.atlantique.entity.MarchandiseItem;
import atlantique.cnut.ne.atlantique.entity.Utilisateur;
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

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarchandiseServiceImpl implements MarchandiseService {

    private final MarchandiseRepository marchandiseRepository;
    private final UtilisateurService utilisateurService;
    private final UtilisateurRepository utilisateurRepository;
    private final DailyCashRegisterService dailyCashRegisterService;

    @Override
    public MarchandiseDto createMarchandise(MarchandiseDto marchandiseDto) {
        log.info("Creating new marchandise");
        Marchandise marchandise = convertToEntity(marchandiseDto);
        performCalculations(marchandise);
        Marchandise savedMarchandise = marchandiseRepository.save(marchandise);
        return convertToDto(savedMarchandise);
    }

    @Override
    public MarchandiseDto updateMarchandise(String id, MarchandiseDto marchandiseDto) {
        log.info("Updating marchandise with id: {}", id);
        return marchandiseRepository.findById(id).map(existingMarchandise -> {
            Marchandise updatedMarchandise = updateEntityFromDto(existingMarchandise, marchandiseDto);
            performCalculations(updatedMarchandise);
            Marchandise savedMarchandise = marchandiseRepository.save(updatedMarchandise);
            return convertToDto(savedMarchandise);
        }).orElseThrow(() -> new ResourceNotFoundException("Marchandise not found with id " + id));
    }

    private void performCalculations(Marchandise marchandise) {
        if (marchandise.isExoneration()) {
            marchandise.setBe("0");
            marchandise.setCoutBsc("0");
            marchandise.setTotalQuittance("0");
            marchandise.setVisa("0");
            marchandise.setTotalBePrice("0");
            return;
        }

        double coutBsc = 0.0;
        double be = 0.0;
        double totalBePriceCalculated = 0.0;

        String conteneurType = marchandise.getConteneur();
        String natureMarchandise = marchandise.getIdNatureMarchandise();

        if ("simple".equalsIgnoreCase(conteneurType)) {
            be = 0;
            coutBsc = 50000;
        } else if ("groupage".equalsIgnoreCase(conteneurType)) {
            be = 0;
            if (marchandise.getMarchandisesGroupage() != null) {
                coutBsc = 50000.0 * marchandise.getMarchandisesGroupage().size();
            } else {
                coutBsc = 0.0;
            }
        } else if ("vrac".equalsIgnoreCase(conteneurType)) {
            try {
                double poids = Double.parseDouble(marchandise.getPoids());
                be = Math.floor(poids / 30000);
                if (be < 1) {
                    be = 1;
                }
                coutBsc = (poids / 1000) * 3000;
                totalBePriceCalculated = be * 10000;
            } catch (NumberFormatException e) {
                be = 0;
                coutBsc = 0;
                totalBePriceCalculated = 0;
            }
        }

        if (marchandise.isRegularisation()) {
            coutBsc = 0;
        }

        double visa = 0.0;
        if ("vehicule".equalsIgnoreCase(natureMarchandise)) {
            try {
                double poids = Double.parseDouble(marchandise.getPoids());
                if (poids < 5000) {
                    visa = 15000;
                } else {
                    visa = 20000;
                }
            } catch (NumberFormatException e) {
                visa = 0;
            }
        }

        double totalQuittance = coutBsc + (be * 10000) + visa;

        marchandise.setBe(String.valueOf(be));
        marchandise.setCoutBsc(String.valueOf(coutBsc));
        marchandise.setVisa(String.valueOf(visa));
        marchandise.setTotalQuittance(String.valueOf(totalQuittance));
        marchandise.setTotalBePrice(String.valueOf(totalBePriceCalculated));
    }


    @Override
    public MarchandiseDto getMarchandiseById(String id) {
        return marchandiseRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Marchandise non trouvée avec l'ID: " + id));
    }

    @Override
    public Page<MarchandiseDto> findAllMarchandisesPaginated(Pageable pageable) {
        Page<Marchandise> page = marchandiseRepository.findAll(pageable);
        return page.map(this::convertToDto);
    }

    @Override
    public List<MarchandiseDto> getMarchandises() {
        return this.marchandiseRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
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

                            // Record deposit in daily cash register
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


    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            return utilisateurService.findByPhone(username)
                    .map(user -> user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur authentifié non trouvé dans la base de données."));
        }
        throw new IllegalStateException("Aucun utilisateur authentifié.");
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

        if (marchandise.getMarchandisesGroupage() != null) {
            dto.setMarchandisesGroupage(marchandise.getMarchandisesGroupage().stream()
                    .map(item -> new MarchandiseItemDto(item.getPoids(), item.getNombreColis(), item.getNumeroBl()))
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}