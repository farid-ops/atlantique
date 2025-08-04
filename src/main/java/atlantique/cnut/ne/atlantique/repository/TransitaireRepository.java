package atlantique.cnut.ne.atlantique.repository;

import atlantique.cnut.ne.atlantique.entity.Transitaire;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransitaireRepository extends JpaRepository<Transitaire, String> {
    Optional<Transitaire> findByDesignation(String designation);

    Page<Transitaire> findByIdGroupe(String idGroupe, Pageable pageable);
    List<Transitaire> findByIdGroupe(String idGroupe);
}
