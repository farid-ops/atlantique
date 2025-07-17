package atlantique.cnut.ne.atlantique.repository;

import atlantique.cnut.ne.atlantique.entity.NatureMarchandise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NatureMarchandiseRepository extends JpaRepository<NatureMarchandise, String> {
    Optional<NatureMarchandise> findByCodeNatureMarchandise(String codeNatureMarchandise);
}
