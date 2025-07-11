package atlantique.cnut.ne.atlantique.service;


import atlantique.cnut.ne.atlantique.repository.ExtraBeRepository;
import org.springframework.stereotype.Service;

@Service
public class ExtraBeServiceImpl implements ExtraBeService {

    private final ExtraBeRepository extraBeRepository;

    public ExtraBeServiceImpl(ExtraBeRepository extraBeRepository) {
        this.extraBeRepository = extraBeRepository;
    }
}
