package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.repository.BlRepository;
import org.springframework.stereotype.Service;

@Service
public class BlServiceImpl implements BlService {

    private final BlRepository blRepository;

    public BlServiceImpl(BlRepository blRepository) {
        this.blRepository = blRepository;
    }
}
