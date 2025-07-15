package atlantique.cnut.ne.atlantique.repository;

import atlantique.cnut.ne.atlantique.entity.Autorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AutoriteRepository extends JpaRepository<Autorite, String> {
    Optional<Autorite> findByNom(String nom);
}
