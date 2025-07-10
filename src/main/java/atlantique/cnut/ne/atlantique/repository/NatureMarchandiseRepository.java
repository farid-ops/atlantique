package atlantique.cnut.ne.atlantique.repository;

import atlantique.cnut.ne.atlantique.entity.NatureMarchandise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NatureMarchandiseRepository extends JpaRepository<NatureMarchandise, String> {
}
