package atlantique.cnut.ne.atlantique.repository;

import atlantique.cnut.ne.atlantique.entity.Marchandise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarchandiseRepository extends JpaRepository<Marchandise, String> {
}
