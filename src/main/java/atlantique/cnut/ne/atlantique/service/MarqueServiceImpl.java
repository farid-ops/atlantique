package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.MarqueDto;
import atlantique.cnut.ne.atlantique.entity.Gamme;
import atlantique.cnut.ne.atlantique.entity.Marque;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.repository.GammeRepository;
import atlantique.cnut.ne.atlantique.repository.MarqueRepository;
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
public class MarqueServiceImpl implements MarqueService {

    private final MarqueRepository marqueRepository;
    private final GammeRepository gammeRepository;

    @Override
    public Marque createMarque(MarqueDto marqueDto) {
        Gamme gamme = gammeRepository.findById(marqueDto.getGammeId())
                .orElseThrow(() -> new ResourceNotFoundException("Gamme non trouvée avec l'ID: " + marqueDto.getGammeId()));

        Marque marque = new Marque();
        marque.setDesignation(marqueDto.getDesignation());
        marque.setGamme(gamme);

        return marqueRepository.save(marque);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Marque> findAllMarques() {
        return marqueRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Marque> findAllMarquesPaginated(Pageable pageable) {
        return marqueRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Marque> findMarqueById(String id) {
        return marqueRepository.findById(id);
    }

    @Override
    public Marque updateMarque(String id, MarqueDto marqueDto) {
        return marqueRepository.findById(id)
                .map(existingMarque -> {
                    Gamme gamme = gammeRepository.findById(marqueDto.getGammeId())
                            .orElseThrow(() -> new ResourceNotFoundException("Gamme non trouvée avec l'ID: " + marqueDto.getGammeId()));
                    existingMarque.setDesignation(marqueDto.getDesignation());
                    existingMarque.setGamme(gamme);
                    return marqueRepository.save(existingMarque);
                }).orElseThrow(() -> new ResourceNotFoundException("Marque non trouvée avec l'ID: " + id));
    }

    @Override
    public void deleteMarque(String id) {
        if (!marqueRepository.existsById(id)) {
            throw new ResourceNotFoundException("Marque non trouvée avec l'ID: " + id);
        }
        marqueRepository.deleteById(id);
    }
}