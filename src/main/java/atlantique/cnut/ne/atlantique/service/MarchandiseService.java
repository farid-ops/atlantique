package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.MarchandiseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MarchandiseService {
    MarchandiseDto createMarchandise(MarchandiseDto marchandiseDto);
    MarchandiseDto updateMarchandise(String id, MarchandiseDto marchandiseDto);
    MarchandiseDto getMarchandiseById(String id);
    Page<MarchandiseDto> findAllMarchandisesPaginated(Pageable pageable);
    List<MarchandiseDto> getMarchandises();
    void deleteMarchandise(String id);

    MarchandiseDto submitMarchandiseForValidation(String id);
    MarchandiseDto validateMarchandise(String id, boolean isValid);
}