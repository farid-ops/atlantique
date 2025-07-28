package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.BlDto;
import atlantique.cnut.ne.atlantique.entity.BL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface BlService {

    BL createBl(BlDto blDto);

    List<BL> findAllBls();

    Page<BL> findAllBlsPaginated(Pageable pageable);

    Optional<BL> findBlById(String id);

    BL updateBl(String id, BlDto blDto);

    void deleteBl(String id);

    List<BL> findBlsByPaysId(String idPays);

    BL uploadBlDocument(MultipartFile file, String designation);
}