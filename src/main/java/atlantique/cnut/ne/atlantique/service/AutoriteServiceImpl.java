package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.repository.AutoriteRepository;
import org.springframework.stereotype.Service;

@Service
public class AutoriteServiceImpl implements AutoriteService {

    private final AutoriteRepository autoriteRepository;

    public AutoriteServiceImpl(AutoriteRepository autoriteRepository) {
        this.autoriteRepository = autoriteRepository;
    }
}
