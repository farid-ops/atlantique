package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.GammeDto;
import atlantique.cnut.ne.atlantique.entity.Gamme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GammeService {
    Gamme createGamme(GammeDto gammeDto);
    List<Gamme> findAllGammes();
    Page<Gamme> findAllGammesPaginated(Pageable pageable);
    Optional<Gamme> findGammeById(String id);
    Gamme updateGamme(String id, GammeDto gammeDto);
    void deleteGamme(String id);
}