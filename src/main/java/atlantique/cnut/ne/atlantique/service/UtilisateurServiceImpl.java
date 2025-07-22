package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.UtilisateurDto;
import atlantique.cnut.ne.atlantique.entity.Autorite;
import atlantique.cnut.ne.atlantique.entity.Utilisateur;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.repository.AutoriteRepository;
import atlantique.cnut.ne.atlantique.repository.UtilisateurRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final AutoriteRepository autoriteRepository;

    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository,
                                  PasswordEncoder passwordEncoder,
                                  AutoriteRepository autoriteRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.autoriteRepository = autoriteRepository;
    }

    @Override
    public Optional<Utilisateur> findByPhone(String phone) {
        return this.utilisateurRepository.findByTelephone(phone);
    }

    @Override
    public Utilisateur createUtilisateur(UtilisateurDto utilisateurDto) {
        if (utilisateurRepository.findByTelephone(utilisateurDto.getTelephone()).isPresent()) {
            throw new IllegalArgumentException("Un utilisateur avec ce numéro de téléphone existe déjà.");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(utilisateurDto.getNom());
        utilisateur.setPrenom(utilisateurDto.getPrenom());
        utilisateur.setEmail(utilisateurDto.getEmail());
        utilisateur.setTelephone(utilisateurDto.getTelephone());
        utilisateur.setAdresse(utilisateurDto.getAdresse());
        utilisateur.setIdSite(utilisateurDto.getIdSite());
        utilisateur.setIdPays(utilisateurDto.getIdPays());

        utilisateur.setPassword(passwordEncoder.encode(utilisateurDto.getPassword()));

        Set<Autorite> assignedAuthorities = new HashSet<>();
        if (utilisateurDto.getAutoriteIds() != null && !utilisateurDto.getAutoriteIds().isEmpty()) {
            assignedAuthorities = utilisateurDto.getAutoriteIds().stream()
                    .map(autoriteId -> autoriteRepository.findById(autoriteId)
                            .orElseThrow(() -> new ResourceNotFoundException("Autorité non trouvée avec l'ID: " + autoriteId)))
                    .collect(Collectors.toSet());
        } else {
            autoriteRepository.findByNom("OPERATEUR").ifPresentOrElse(
                    assignedAuthorities::add,
                    () -> {
                        throw new ResourceNotFoundException("Le rôle 'OPERATEUR' par défaut n'a pas été trouvé dans la base de données.");
                    }
            );
        }
        utilisateur.setAuthorites(assignedAuthorities);

        utilisateur.setAccountNonExpired(true);
        utilisateur.setAccountNonLocked(true);
        utilisateur.setCredentialsNonExpired(true);
        utilisateur.setEnabled(utilisateurDto.getEnabled() != null ? utilisateurDto.getEnabled() : true);

        return utilisateurRepository.save(utilisateur);
    }

    @Override
    public List<Utilisateur> findAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    @Override
    public Page<Utilisateur> findAllUtilisateursPaginated(Pageable pageable) {
        return utilisateurRepository.findAll(pageable);
    }

    @Override
    public Optional<Utilisateur> findUtilisateurById(String id) {
        return utilisateurRepository.findById(id);
    }

    @Override
    public Utilisateur updateUtilisateur(String id, UtilisateurDto utilisateurDto) {
        return utilisateurRepository.findById(id)
                .map(existingUtilisateur -> {
                    existingUtilisateur.setNom(utilisateurDto.getNom());
                    existingUtilisateur.setPrenom(utilisateurDto.getPrenom());
                    existingUtilisateur.setEmail(utilisateurDto.getEmail());
                    existingUtilisateur.setTelephone(utilisateurDto.getTelephone());
                    existingUtilisateur.setAdresse(utilisateurDto.getAdresse());
                    existingUtilisateur.setIdSite(utilisateurDto.getIdSite());
                    existingUtilisateur.setIdPays(utilisateurDto.getIdPays());

                    if (utilisateurDto.getPassword() != null && !utilisateurDto.getPassword().isEmpty()) {
                        existingUtilisateur.setPassword(passwordEncoder.encode(utilisateurDto.getPassword()));
                    }

                    if (utilisateurDto.getAutoriteIds() != null) {
                        Set<Autorite> updatedAuthorities = utilisateurDto.getAutoriteIds().stream()
                                .map(autoriteId -> autoriteRepository.findById(autoriteId)
                                        .orElseThrow(() -> new ResourceNotFoundException("Autorité non trouvée avec l'ID: " + autoriteId)))
                                .collect(Collectors.toSet());
                        existingUtilisateur.setAuthorites(updatedAuthorities);
                    }

                    if (utilisateurDto.getEnabled() != null) {
                        existingUtilisateur.setEnabled(utilisateurDto.getEnabled());
                    }

                    return utilisateurRepository.save(existingUtilisateur);
                }).orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));
    }

    @Override
    public void deleteUtilisateur(String id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id);
        }
        utilisateurRepository.deleteById(id);
    }
}