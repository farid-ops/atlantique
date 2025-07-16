package atlantique.cnut.ne.atlantique.repository;

import atlantique.cnut.ne.atlantique.entity.Port;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortRepository extends JpaRepository<Port, String> {
    List<Port> findByIdPays(String idPays);
}
