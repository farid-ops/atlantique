package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.NatureMarchandiseDto;
import atlantique.cnut.ne.atlantique.entity.NatureMarchandise;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.repository.NatureMarchandiseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NatureMarchandiseServiceImpl implements NatureMarchandiseService {

    private final NatureMarchandiseRepository natureMarchandiseRepository;

    public NatureMarchandiseServiceImpl(NatureMarchandiseRepository natureMarchandiseRepository) {
        this.natureMarchandiseRepository = natureMarchandiseRepository;
    }

    @Override
    public NatureMarchandise createNatureMarchandise(NatureMarchandiseDto natureMarchandiseDto) {
         if (natureMarchandiseRepository.findByCodeNatureMarchandise(natureMarchandiseDto.getCodeNatureMarchandise()).isPresent()) {
             throw new IllegalArgumentException("Une nature de marchandise avec cette désignation existe déjà.");
         }

        NatureMarchandise natureMarchandise = new NatureMarchandise();
        natureMarchandise.setDesignation(natureMarchandiseDto.getDesignation());
        natureMarchandise.setCodeNatureMarchandise(natureMarchandiseDto.getCodeNatureMarchandise());

        return natureMarchandiseRepository.save(natureMarchandise);
    }

    @Override
    public List<NatureMarchandise> findAllNatureMarchandises() {
        return natureMarchandiseRepository.findAll();
    }

    @Override
    public Page<NatureMarchandise> findAllNatureMarchandisesPaginated(Pageable pageable) {
        return natureMarchandiseRepository.findAll(pageable);
    }

    @Override
    public Optional<NatureMarchandise> findNatureMarchandiseById(String id) {
        return natureMarchandiseRepository.findById(id);
    }

    @Override
    public NatureMarchandise updateNatureMarchandise(String id, NatureMarchandiseDto natureMarchandiseDto) {
        return natureMarchandiseRepository.findById(id)
                .map(existingNatureMarchandise -> {
                    existingNatureMarchandise.setDesignation(natureMarchandiseDto.getDesignation());
                    existingNatureMarchandise.setCodeNatureMarchandise(natureMarchandiseDto.getCodeNatureMarchandise());

                    return natureMarchandiseRepository.save(existingNatureMarchandise);
                }).orElseThrow(() -> new ResourceNotFoundException("NatureMarchandise non trouvée avec l'ID: " + id));
    }

    @Override
    public void deleteNatureMarchandise(String id) {
        if (!natureMarchandiseRepository.existsById(id)) {
            throw new ResourceNotFoundException("NatureMarchandise non trouvée avec l'ID: " + id);
        }
        natureMarchandiseRepository.deleteById(id);
    }
}