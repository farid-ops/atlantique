package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.GammeDto;
import atlantique.cnut.ne.atlantique.entity.Gamme;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.repository.GammeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GammeServiceImpl implements GammeService {

    private final GammeRepository gammeRepository;

    @Override
    public Gamme createGamme(GammeDto gammeDto) {
        Gamme gamme = new Gamme();
        gamme.setDesignation(gammeDto.getDesignation());
        gamme.setIdPays(gammeDto.getIdPays());
        return gammeRepository.save(gamme);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Gamme> findAllGammes() {
        return gammeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Gamme> findAllGammesPaginated(Pageable pageable) {
        return gammeRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Gamme> findGammeById(String id) {
        return gammeRepository.findById(id);
    }

    @Override
    public Gamme updateGamme(String id, GammeDto gammeDto) {
        return gammeRepository.findById(id)
                .map(existingGamme -> {
                    existingGamme.setDesignation(gammeDto.getDesignation());
                    existingGamme.setIdPays(gammeDto.getIdPays());
                    return gammeRepository.save(existingGamme);
                }).orElseThrow(() -> new ResourceNotFoundException("Gamme non trouvée avec l'ID: " + id));
    }

    @Override
    public void deleteGamme(String id) {
        if (!gammeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Gamme non trouvée avec l'ID: " + id);
        }
        gammeRepository.deleteById(id);
    }
}