package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.entity.Utilisateur;

import java.util.Optional;

public interface UtilisateurService {

    Optional<Utilisateur> findByPhone(String phone);
}
