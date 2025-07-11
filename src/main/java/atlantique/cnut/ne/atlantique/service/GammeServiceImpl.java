package atlantique.cnut.ne.atlantique.service;


import atlantique.cnut.ne.atlantique.repository.GammeRepository;
import org.springframework.stereotype.Service;

@Service
public class GammeServiceImpl implements GammeService {
    private final GammeRepository gammeRepository;

    public GammeServiceImpl(GammeRepository gammeRepository) {
        this.gammeRepository = gammeRepository;
    }
}
