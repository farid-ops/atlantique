package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.BonExpeditionDto;
import atlantique.cnut.ne.atlantique.entity.BonExpedition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BonExpeditionService {
    BonExpedition createBonExpeditionFromMarchandise(String marchandiseId, BonExpeditionDto bonExpeditionDto);

    BonExpedition createBonExpedition(BonExpeditionDto bonExpeditionDto);

    List<BonExpedition> findAllBonExpeditions();
    Page<BonExpedition> findAllBonExpeditionsPaginated(Pageable pageable);

    Optional<BonExpedition> findBonExpeditionById(String id);

    BonExpedition updateBonExpedition(String id, BonExpeditionDto bonExpeditionDto);

    void deleteBonExpedition(String id);
}
