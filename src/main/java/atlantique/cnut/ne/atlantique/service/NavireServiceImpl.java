package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.NavireDto;
import atlantique.cnut.ne.atlantique.entity.Navire;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.repository.NavireRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NavireServiceImpl implements NavireService {

    private final NavireRepository navireRepository;

    public NavireServiceImpl(NavireRepository navireRepository) {
        this.navireRepository = navireRepository;
    }

    @Override
    public Navire createNavire(NavireDto navireDto) {

         if (navireRepository.findByDesignation(navireDto.getDesignation()).isPresent()) {
             throw new IllegalArgumentException("Un navire avec cette désignation existe déjà.");
         }

        Navire navire = new Navire();
        navire.setDesignation(navireDto.getDesignation());

        return navireRepository.save(navire);
    }

    @Override
    public List<Navire> findAllNavires() {
        return navireRepository.findAll();
    }

    @Override
    public Page<Navire> findAllNaviresPaginated(Pageable pageable) {
        return navireRepository.findAll(pageable);
    }

    @Override
    public Optional<Navire> findNavireById(String id) {
        return navireRepository.findById(id);
    }

    @Override
    public Navire updateNavire(String id, NavireDto navireDto) {
        return navireRepository.findById(id)
                .map(existingNavire -> {
                    existingNavire.setDesignation(navireDto.getDesignation());
                    return navireRepository.save(existingNavire);
                }).orElseThrow(() -> new ResourceNotFoundException("Navire non trouvé avec l'ID: " + id));
    }

    @Override
    public void deleteNavire(String id) {
        if (!navireRepository.existsById(id)) {
            throw new ResourceNotFoundException("Navire non trouvé avec l'ID: " + id);
        }
        navireRepository.deleteById(id);
    }
}