package atlantique.cnut.ne.atlantique.service;


import atlantique.cnut.ne.atlantique.repository.PortRepository;
import org.springframework.stereotype.Service;

@Service
public class PortServiceImpl implements PortService {

    private final PortRepository portRepository;

    public PortServiceImpl(PortRepository portRepository) {
        this.portRepository = portRepository;
    }
}
