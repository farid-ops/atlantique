package atlantique.cnut.ne.atlantique.repository;

import atlantique.cnut.ne.atlantique.entity.Armateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArmateurRepository extends JpaRepository<Armateur, String> {

    Optional<Armateur> findByDesignation(String designation);
}
