package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.NatureMarchandiseDto;
import atlantique.cnut.ne.atlantique.entity.NatureMarchandise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface NatureMarchandiseService {

    NatureMarchandise createNatureMarchandise(NatureMarchandiseDto natureMarchandiseDto);

    List<NatureMarchandise> findAllNatureMarchandises();

    Page<NatureMarchandise> findAllNatureMarchandisesPaginated(Pageable pageable);

    Optional<NatureMarchandise> findNatureMarchandiseById(String id);

    NatureMarchandise updateNatureMarchandise(String id, NatureMarchandiseDto natureMarchandiseDto);

    void deleteNatureMarchandise(String id);
}