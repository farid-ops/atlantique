package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.AutoriteDto;
import atlantique.cnut.ne.atlantique.entity.Autorite;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.repository.AutoriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AutoriteServiceImpl implements AutoriteService {

    private final AutoriteRepository autoriteRepository;

    public AutoriteServiceImpl(AutoriteRepository autoriteRepository) {
        this.autoriteRepository = autoriteRepository;
    }

    @Override
    public Autorite createAutorite(AutoriteDto autoriteDto) {
        if (autoriteRepository.findByNom(autoriteDto.getNom()).isPresent()) {
            throw new IllegalArgumentException("Une autorité avec ce nom existe déjà.");
        }

        Autorite autorite = new Autorite();
        autorite.setNom(autoriteDto.getNom());

        return autoriteRepository.save(autorite);
    }

    @Override
    public List<Autorite> findAllAutorites() {
        return autoriteRepository.findAll();
    }

    @Override
    public Optional<Autorite> findAutoriteById(String id) {
        return autoriteRepository.findById(id);
    }

    @Override
    public Autorite updateAutorite(String id, AutoriteDto autoriteDto) {
        return autoriteRepository.findById(id)
                .map(existingAutorite -> {
                    if (autoriteRepository.findByNom(autoriteDto.getNom()).isPresent() && !existingAutorite.getId().equals(autoriteRepository.findByNom(autoriteDto.getNom()).get().getId())) {
                        throw new IllegalArgumentException("Une autre autorité avec ce nom existe déjà.");
                    }
                    existingAutorite.setNom(autoriteDto.getNom());

                    return autoriteRepository.save(existingAutorite);
                }).orElseThrow(() -> new ResourceNotFoundException("Autorité non trouvée avec l'ID: " + id));
    }

    @Override
    public void deleteAutorite(String id) {
        if (!autoriteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Autorité non trouvée avec l'ID: " + id);
        }
        autoriteRepository.deleteById(id);
    }
}