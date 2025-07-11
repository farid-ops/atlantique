package atlantique.cnut.ne.atlantique.service;


import atlantique.cnut.ne.atlantique.repository.MarchandiseRepository;
import org.springframework.stereotype.Service;

@Service
public class MarchandiseServiceImpl implements MarchandiseService {
    private final MarchandiseRepository marchandiseRepository;

    public MarchandiseServiceImpl(MarchandiseRepository marchandiseRepository) {
        this.marchandiseRepository = marchandiseRepository;
    }
}
