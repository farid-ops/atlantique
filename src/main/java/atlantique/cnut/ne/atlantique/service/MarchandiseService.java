package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.MarchandiseDto;
import atlantique.cnut.ne.atlantique.entity.Marchandise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MarchandiseService {

    Marchandise createMarchandise(MarchandiseDto marchandiseDto);
    List<Marchandise> findAllMarchandises();

    Page<Marchandise> findAllMarchandisesPaginated(Pageable pageable);

    Optional<Marchandise> findMarchandiseById(String id);
    Marchandise updateMarchandise(String id, MarchandiseDto marchandiseDto);
    void deleteMarchandise(String id);
}