package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.BonExpeditionDto;
import atlantique.cnut.ne.atlantique.entity.BonExpedition;
import atlantique.cnut.ne.atlantique.entity.Marchandise;
import atlantique.cnut.ne.atlantique.entity.Utilisateur;
import atlantique.cnut.ne.atlantique.enums.MarchandiseStatus;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.repository.BonExpeditionRepository;
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

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BonExpeditionServiceImpl implements BonExpeditionService {

    private final BonExpeditionRepository bonExpeditionRepository;
    private final MarchandiseRepository marchandiseRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurService utilisateurService;

    private Optional<Utilisateur> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }
        String username = authentication.getName();
        return utilisateurService.findByPhone(username);
    }

    private String getCurrentUserId() {
        return getAuthenticatedUser()
                .map(Utilisateur::getId)
                .orElseThrow(() -> new IllegalStateException("Aucun utilisateur authentifié."));
    }

    private String getCurrentUserSiteId() {
        return getAuthenticatedUser()
                .map(Utilisateur::getIdSite)
                .orElseThrow(() -> new IllegalStateException("Utilisateur authentifié non trouvé ou n'a pas d'ID de site."));
    }

    @Override
    @Transactional
    public BonExpedition createBonExpeditionFromMarchandise(String marchandiseId, BonExpeditionDto bonExpeditionDto) {
        Marchandise marchandise = marchandiseRepository.findById(marchandiseId)
                .orElseThrow(() -> new ResourceNotFoundException("Marchandise non trouvée avec l'ID: " + marchandiseId));

        if (marchandise.getStatus() != MarchandiseStatus.VALIDE) {
            throw new IllegalArgumentException("Un Bon d'Expédition ne peut être créé que pour une marchandise VALIDÉE. Statut actuel: " + marchandise.getStatus().getDisplayName());
        }

        double beDisponible;
        try {
            beDisponible = Double.parseDouble(marchandise.getBe());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Le nombre de BE de la marchandise n'est pas un nombre valide.");
        }

        if (beDisponible < 1) {
            throw new IllegalArgumentException("Aucun BE disponible pour cette marchandise.");
        }

        marchandise.setBe(String.valueOf(beDisponible - 1));

        double poidsMarchandiseOriginal;
        double poidsBeCree;
        try {
            poidsMarchandiseOriginal = Double.parseDouble(marchandise.getPoids());
            poidsBeCree = Double.parseDouble(bonExpeditionDto.getPoids());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Le poids de la marchandise ou du BE n'est pas un nombre valide.");
        }

        if (poidsMarchandiseOriginal < poidsBeCree) {
            throw new IllegalArgumentException("Le poids du Bon d'Expédition (" + poidsBeCree + ") ne peut pas être supérieur au poids restant de la marchandise (" + poidsMarchandiseOriginal + ").");
        }
        marchandise.setPoids(String.valueOf(poidsMarchandiseOriginal - poidsBeCree));

        marchandiseRepository.save(marchandise);

        BonExpedition bonExpedition = new BonExpedition();
        bonExpedition.setNombreColis(bonExpeditionDto.getNombreColis());
        bonExpedition.setImmatriculation(bonExpeditionDto.getImmatriculation());
        bonExpedition.setNom(bonExpeditionDto.getNom());
        bonExpedition.setPrenom(bonExpeditionDto.getPrenom());
        bonExpedition.setDestinataire(bonExpeditionDto.getDestinataire());
        bonExpedition.setPoids(bonExpeditionDto.getPoids());
        bonExpedition.setValeur(bonExpeditionDto.getValeur());
        bonExpedition.setObservation(bonExpeditionDto.getObservation());
        bonExpedition.setValide(false);

        bonExpedition.setIdSite(getCurrentUserSiteId());
        bonExpedition.setIdUtilisateur(getCurrentUserId());

        bonExpedition.setIdMarchandise(marchandiseId);

        log.info("Création d'un Bon d'Expédition pour la marchandise ID: {}", marchandiseId);
        return bonExpeditionRepository.save(bonExpedition);
    }

    @Override
    @Transactional
    public BonExpedition createBonExpedition(BonExpeditionDto bonExpeditionDto) {
        log.info("Creating new Bon Expedition");
        BonExpedition bonExpedition = new BonExpedition();

        bonExpedition.setNombreColis(bonExpeditionDto.getNombreColis());
        bonExpedition.setImmatriculation(bonExpeditionDto.getImmatriculation());
        bonExpedition.setNom(bonExpeditionDto.getNom());
        bonExpedition.setPrenom(bonExpeditionDto.getPrenom());
        bonExpedition.setDestinataire(bonExpeditionDto.getDestinataire());
        bonExpedition.setPoids(bonExpeditionDto.getPoids());
        bonExpedition.setValeur(bonExpeditionDto.getValeur());
        bonExpedition.setObservation(bonExpeditionDto.getObservation());
        bonExpedition.setValide(false);

        bonExpedition.setIdSite(getCurrentUserSiteId());
        bonExpedition.setIdUtilisateur(getCurrentUserId());

        if (bonExpeditionDto.getIdMarchandise() == null || bonExpeditionDto.getIdMarchandise().isEmpty()) {
            throw new IllegalArgumentException("L'identifiant de la marchandise est obligatoire pour créer un Bon d'Expédition.");
        }
        bonExpedition.setIdMarchandise(bonExpeditionDto.getIdMarchandise());

        return bonExpeditionRepository.save(bonExpedition);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BonExpedition> findAllBonExpeditions() {
        return bonExpeditionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BonExpedition> findAllBonExpeditionsPaginated(Pageable pageable) {
        return bonExpeditionRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BonExpedition> findBonExpeditionById(String id) {
        return bonExpeditionRepository.findById(id);
    }

    @Override
    @Transactional
    public BonExpedition updateBonExpedition(String id, BonExpeditionDto bonExpeditionDto) {
        return bonExpeditionRepository.findById(id)
                .map(existingBonExpedition -> {
                    // Mappage des champs du DTO aux champs existants du BE
                    existingBonExpedition.setNombreColis(bonExpeditionDto.getNombreColis());
                    existingBonExpedition.setImmatriculation(bonExpeditionDto.getImmatriculation());
                    existingBonExpedition.setNom(bonExpeditionDto.getNom());
                    existingBonExpedition.setPrenom(bonExpeditionDto.getPrenom());
                    existingBonExpedition.setDestinataire(bonExpeditionDto.getDestinataire());
                    existingBonExpedition.setPoids(bonExpeditionDto.getPoids());
                    existingBonExpedition.setValeur(bonExpeditionDto.getValeur());
                    existingBonExpedition.setObservation(bonExpeditionDto.getObservation());
                    existingBonExpedition.setValide(bonExpeditionDto.isValide());

                    return bonExpeditionRepository.save(existingBonExpedition);
                }).orElseThrow(() -> new ResourceNotFoundException("BonExpedition non trouvé avec l'ID: " + id));
    }

    @Override
    @Transactional
    public void deleteBonExpedition(String id) {
        if (!bonExpeditionRepository.existsById(id)) {
            throw new ResourceNotFoundException("BonExpedition non trouvé avec l'ID: " + id);
        }
        bonExpeditionRepository.deleteById(id);
    }
}