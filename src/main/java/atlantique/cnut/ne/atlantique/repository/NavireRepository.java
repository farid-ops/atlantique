package atlantique.cnut.ne.atlantique.repository;

import atlantique.cnut.ne.atlantique.entity.Navire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NavireRepository extends JpaRepository<Navire, String> {

    Optional<Navire> findByDesignation(String nom);
}
