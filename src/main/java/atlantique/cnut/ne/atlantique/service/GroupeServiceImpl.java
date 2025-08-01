package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.GroupeDto;
import atlantique.cnut.ne.atlantique.entity.Groupe;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.repository.GroupeRepository;
import atlantique.cnut.ne.atlantique.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupeServiceImpl implements GroupeService {

    private final GroupeRepository groupeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public Groupe createGroupe(GroupeDto groupeDto, MultipartFile logoFile, MultipartFile signatureFile) {
        if (groupeRepository.findByDenomination(groupeDto.getDenomination()).isPresent()) {
            throw new IllegalArgumentException("Un groupe avec cette dénomination existe déjà : " + groupeDto.getDenomination());
        }

        Groupe groupe = new Groupe();
        groupe.setDenomination(groupeDto.getDenomination());
        groupe.setTelephone(groupeDto.getTelephone());
        groupe.setEmail(groupeDto.getEmail());
        groupe.setSiteWeb(groupeDto.getSiteWeb());
        groupe.setNif(groupeDto.getNif());
        groupe.setBp(groupeDto.getBp());
        groupe.setAdresse(groupeDto.getAdresse());
        groupe.setPrixBeStandard(groupeDto.getPrixBeStandard());
        groupe.setVisaVehiculeMoins5000kg(groupeDto.getVisaVehiculeMoins5000kg());
        groupe.setVisaVehiculePlus5000kg(groupeDto.getVisaVehiculePlus5000kg());

        groupe.setCoutBSC(groupeDto.getCoutBSC());
        groupe.setTonnage(groupeDto.getTonnage());
        groupe.setValeurConteneur10Pieds(groupeDto.getValeurConteneur10Pieds());
        groupe.setValeurConteneur20Pieds(groupeDto.getValeurConteneur20Pieds());
        groupe.setValeurConteneur30Pieds(groupeDto.getValeurConteneur30Pieds());

        if (logoFile != null && !logoFile.isEmpty()) {
            String uniqueFileName = fileStorageService.save(logoFile);
            groupe.setLogo(uniqueFileName);
        }
        if (signatureFile != null && !signatureFile.isEmpty()) {
            String uniqueFileName = fileStorageService.save(signatureFile);
            groupe.setSignatureImage(uniqueFileName);
        }

        log.info("Création d'un nouveau groupe: {}", groupe.getDenomination());
        return groupeRepository.save(groupe);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Groupe> findGroupeById(String id) {
        return groupeRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Groupe> findAllGroupes() {
        return groupeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Groupe> findAllGroupesPaginated(Pageable pageable) {
        return groupeRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Groupe updateGroupe(String id, GroupeDto groupeDto, MultipartFile logoFile, MultipartFile signatureFile) {
        return groupeRepository.findById(id)
                .map(existingGroupe -> {
                    if (!existingGroupe.getDenomination().equals(groupeDto.getDenomination())) {
                        if (groupeRepository.findByDenomination(groupeDto.getDenomination()).isPresent()) {
                            throw new IllegalArgumentException("Un autre groupe avec cette dénomination existe déjà : " + groupeDto.getDenomination());
                        }
                    }

                    existingGroupe.setDenomination(groupeDto.getDenomination());
                    existingGroupe.setTelephone(groupeDto.getTelephone());
                    existingGroupe.setEmail(groupeDto.getEmail());
                    existingGroupe.setSiteWeb(groupeDto.getSiteWeb());
                    existingGroupe.setNif(groupeDto.getNif());
                    existingGroupe.setBp(groupeDto.getBp());
                    existingGroupe.setAdresse(groupeDto.getAdresse());
                    existingGroupe.setPrixBeStandard(groupeDto.getPrixBeStandard());
                    existingGroupe.setVisaVehiculeMoins5000kg(groupeDto.getVisaVehiculeMoins5000kg());
                    existingGroupe.setVisaVehiculePlus5000kg(groupeDto.getVisaVehiculePlus5000kg());

                    existingGroupe.setCoutBSC(groupeDto.getCoutBSC());
                    existingGroupe.setTonnage(groupeDto.getTonnage());
                    existingGroupe.setValeurConteneur10Pieds(groupeDto.getValeurConteneur10Pieds());
                    existingGroupe.setValeurConteneur20Pieds(groupeDto.getValeurConteneur20Pieds());
                    existingGroupe.setValeurConteneur30Pieds(groupeDto.getValeurConteneur30Pieds());

                    if (logoFile != null && !logoFile.isEmpty()) {
                        if (existingGroupe.getLogo() != null && !existingGroupe.getLogo().isEmpty()) {
                            fileStorageService.delete(existingGroupe.getLogo());
                        }
                        String uniqueFileName = fileStorageService.save(logoFile);
                        existingGroupe.setLogo(uniqueFileName);
                    } else if (groupeDto.getLogo() == null) {
                        if (existingGroupe.getLogo() != null && !existingGroupe.getLogo().isEmpty()) {
                            fileStorageService.delete(existingGroupe.getLogo());
                        }
                        existingGroupe.setLogo(null);
                    }
                    if (signatureFile != null && !signatureFile.isEmpty()) {
                        if (existingGroupe.getSignatureImage() != null && !existingGroupe.getSignatureImage().isEmpty()) {
                            fileStorageService.delete(existingGroupe.getSignatureImage());
                        }
                        String uniqueFileName = fileStorageService.save(signatureFile);
                        existingGroupe.setSignatureImage(uniqueFileName);
                    } else if (groupeDto.getSignatureImage() == null) {
                        if (existingGroupe.getSignatureImage() != null && !existingGroupe.getSignatureImage().isEmpty()) {
                            fileStorageService.delete(existingGroupe.getSignatureImage());
                        }
                        existingGroupe.setSignatureImage(null);
                    }

                    log.info("Mise à jour du groupe avec l'ID: {}", id);
                    return groupeRepository.save(existingGroupe);
                }).orElseThrow(() -> new ResourceNotFoundException("Groupe non trouvé avec l'ID: " + id));
    }

    @Override
    @Transactional
    public void deleteGroupe(String id) {
        Groupe groupeToDelete = groupeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Groupe non trouvé avec l'ID: " + id));

        long userCount = utilisateurRepository.countByIdGroupe(id);

        if (userCount > 0) {
            throw new IllegalArgumentException("Impossible de supprimer le groupe avec l'ID " + id + " car " + userCount + " utilisateur(s) lui sont encore associé(s). Veuillez d'abord réaffecter ou désactiver ces utilisateurs.");
        }

        if (groupeToDelete.getLogo() != null && !groupeToDelete.getLogo().isEmpty()) {
            fileStorageService.delete(groupeToDelete.getLogo());
        }
        if (groupeToDelete.getSignatureImage() != null && !groupeToDelete.getSignatureImage().isEmpty()) {
            fileStorageService.delete(groupeToDelete.getSignatureImage());
        }

        log.info("Suppression du groupe avec l'ID: {}", id);
        groupeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Groupe> findByDenomination(String denomination) {
        return groupeRepository.findByDenomination(denomination);
    }
}