package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.repository.NatureMarchandiseRepository;
import org.springframework.stereotype.Service;

@Service
public class NatureMarchandiseServiceImpl implements NatureMarchandiseService {
    private final NatureMarchandiseRepository natureMarchandiseRepository;

    public NatureMarchandiseServiceImpl(NatureMarchandiseRepository natureMarchandiseRepository) {
        this.natureMarchandiseRepository = natureMarchandiseRepository;
    }
}
