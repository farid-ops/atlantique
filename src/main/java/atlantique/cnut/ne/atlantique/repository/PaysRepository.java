package atlantique.cnut.ne.atlantique.repository;

import atlantique.cnut.ne.atlantique.entity.Pays;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaysRepository extends JpaRepository<Pays, String> {
}
