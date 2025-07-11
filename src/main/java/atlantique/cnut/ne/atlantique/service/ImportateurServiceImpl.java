package atlantique.cnut.ne.atlantique.service;


import atlantique.cnut.ne.atlantique.repository.ImportateurRepository;
import org.springframework.stereotype.Service;

@Service
public class ImportateurServiceImpl implements ImportateurService {
    private final ImportateurRepository importateurRepository;

    public ImportateurServiceImpl(ImportateurRepository importateurRepository) {
        this.importateurRepository = importateurRepository;
    }
}
