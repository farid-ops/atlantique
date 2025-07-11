package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.repository.ArmateurRepository;
import org.springframework.stereotype.Service;

@Service
public class ArmateurServiceImpl implements ArmateurService {

    private final ArmateurRepository armateurRepository;

    public ArmateurServiceImpl(ArmateurRepository armateurRepository) {
        this.armateurRepository = armateurRepository;
    }
}
