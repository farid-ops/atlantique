package atlantique.cnut.ne.atlantique.repository;

import atlantique.cnut.ne.atlantique.entity.Consignataire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConsignataireRepository extends JpaRepository<Consignataire, String> {
    Optional<Consignataire> findByDesignation(String designation);
}
