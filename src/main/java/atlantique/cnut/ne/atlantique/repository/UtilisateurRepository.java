package atlantique.cnut.ne.atlantique.repository;

import atlantique.cnut.ne.atlantique.entity.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, String> {

    Optional<Utilisateur> findByTelephone(String phone);
    Optional<Utilisateur> findByEmail(String email);

    long countByIdGroupe(String idGroupe);

    List<Utilisateur> findByIdSite(String idSite);
    Page<Utilisateur> findByIdSite(String idSite, Pageable pageable);

    List<Utilisateur> findByIdGroupe(String idGroupe);
    Page<Utilisateur> findByIdGroupe(String idGroupe, Pageable pageable);
}
