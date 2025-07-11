package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.repository.CargaisonRepository;
import org.springframework.stereotype.Service;

@Service
public class CargaisonServiceImpl implements CargaisonService {

    private final CargaisonRepository cargaisonRepository;

    public CargaisonServiceImpl(CargaisonRepository cargaisonRepository) {
        this.cargaisonRepository = cargaisonRepository;
    }
}
