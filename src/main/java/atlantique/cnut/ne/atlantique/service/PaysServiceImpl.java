package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.PaysDto;
import atlantique.cnut.ne.atlantique.entity.Pays;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.repository.PaysRepository; // Importez le repository
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaysServiceImpl implements PaysService {

    private final PaysRepository paysRepository;

    public PaysServiceImpl(PaysRepository paysRepository) {
        this.paysRepository = paysRepository;
    }

    @Override
    public Pays createPays(PaysDto paysDto) {
         if (paysRepository.findByDesignation(paysDto.getDesignation()).isPresent()) {
             throw new IllegalArgumentException("Un pays avec cette désignation existe déjà.");
         }

        Pays pays = new Pays();
        pays.setDesignation(paysDto.getDesignation());

        return paysRepository.save(pays);
    }

    @Override
    public List<Pays> findAllPays() {
        return paysRepository.findAll();
    }

    @Override
    public Page<Pays> findAllPaysPaginated(Pageable pageable) {
        return paysRepository.findAll(pageable);
    }

    @Override
    public Optional<Pays> findPaysById(String id) {
        return paysRepository.findById(id);
    }

    @Override
    public Pays updatePays(String id, PaysDto paysDto) {
        return paysRepository.findById(id)
                .map(existingPays -> {
                    existingPays.setDesignation(paysDto.getDesignation());
                    return paysRepository.save(existingPays);
                }).orElseThrow(() -> new ResourceNotFoundException("Pays non trouvé avec l'ID: " + id));
    }

    @Override
    public void deletePays(String id) {
        if (!paysRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pays non trouvé avec l'ID: " + id);
        }
        paysRepository.deleteById(id);
    }
}