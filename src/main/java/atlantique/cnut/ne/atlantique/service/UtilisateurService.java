package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.UtilisateurDto;
import atlantique.cnut.ne.atlantique.entity.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List; // Importez List
import java.util.Optional;

public interface UtilisateurService {

    Optional<Utilisateur> findByPhone(String phone);

    Utilisateur createUtilisateur(UtilisateurDto utilisateurDto);

    List<Utilisateur> findAllUtilisateurs();
    Page<Utilisateur> findAllUtilisateursPaginated(Pageable pageable);

    Optional<Utilisateur> findUtilisateurById(String id);

    Utilisateur updateUtilisateur(String id, UtilisateurDto utilisateurDto);

    void deleteUtilisateur(String id);
}