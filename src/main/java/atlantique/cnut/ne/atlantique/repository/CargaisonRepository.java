package atlantique.cnut.ne.atlantique.repository;

import atlantique.cnut.ne.atlantique.entity.Cargaison;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CargaisonRepository extends JpaRepository<Cargaison, String> {
    List<Cargaison> findByIdPortEmbarquementOrIdPortDemarquement(String idPortEmbarquement, String idPortDemarquement);
}
