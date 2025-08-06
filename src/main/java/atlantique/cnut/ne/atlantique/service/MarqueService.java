package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.MarqueDto;
import atlantique.cnut.ne.atlantique.entity.Marque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MarqueService {
    Marque createMarque(MarqueDto marqueDto);
    List<Marque> findAllMarques();
    Page<Marque> findAllMarquesPaginated(Pageable pageable);
    Optional<Marque> findMarqueById(String id);
    Marque updateMarque(String id, MarqueDto marqueDto);
    void deleteMarque(String id);
}