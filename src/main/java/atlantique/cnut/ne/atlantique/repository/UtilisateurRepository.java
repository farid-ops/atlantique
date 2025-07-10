package atlantique.cnut.ne.atlantique.repository;

import atlantique.cnut.ne.atlantique.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, String> {
}
