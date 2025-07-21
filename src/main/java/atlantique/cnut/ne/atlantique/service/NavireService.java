package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.NavireDto; // Importez le DTO
import atlantique.cnut.ne.atlantique.entity.Navire; // Importez l'entité
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface NavireService {
    Navire createNavire(NavireDto navireDto);
    List<Navire> findAllNavires();
    Page<Navire> findAllNaviresPaginated(Pageable pageable);
    Optional<Navire> findNavireById(String id);
    Navire updateNavire(String id, NavireDto navireDto);
    void deleteNavire(String id);
}