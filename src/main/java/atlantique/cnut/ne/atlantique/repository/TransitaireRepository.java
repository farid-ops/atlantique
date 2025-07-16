package atlantique.cnut.ne.atlantique.repository;

import atlantique.cnut.ne.atlantique.entity.Transitaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransitaireRepository extends JpaRepository<Transitaire, String> {
    Optional<Transitaire> findByDesignation(String designation);
}
