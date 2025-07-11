package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.repository.SiteRepository;
import org.springframework.stereotype.Service;

@Service
public class SiteServiceImpl implements SiteService {
    private final SiteRepository siteRepository;

    public SiteServiceImpl(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }
}
