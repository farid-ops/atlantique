package atlantique.cnut.ne.atlantique.repository;

import atlantique.cnut.ne.atlantique.entity.Importateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImportateurRepository extends JpaRepository<Importateur, String> {

    Optional<Importateur> findByNif(String nif);
}
