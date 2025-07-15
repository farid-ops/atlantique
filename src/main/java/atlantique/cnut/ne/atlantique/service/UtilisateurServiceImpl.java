package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.entity.Utilisateur;
import atlantique.cnut.ne.atlantique.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public Optional<Utilisateur> findByPhone(String phone) {
        return this.utilisateurRepository.findByPhone(phone);
    }
}
