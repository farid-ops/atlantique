package atlantique.cnut.ne.atlantique.repository;

import atlantique.cnut.ne.atlantique.entity.Groupe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupeRepository extends JpaRepository<Groupe, String> {

    Optional<Groupe> findByDenomination(String denomination);
}