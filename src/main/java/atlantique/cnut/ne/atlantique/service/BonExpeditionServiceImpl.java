package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.repository.BonExpeditionRepository;
import org.springframework.stereotype.Service;

@Service
public class BonExpeditionServiceImpl implements BonExpeditionService {

    private final BonExpeditionRepository bonExpeditionRepository;

    public BonExpeditionServiceImpl(BonExpeditionRepository bonExpeditionRepository) {
        this.bonExpeditionRepository = bonExpeditionRepository;
    }
}
