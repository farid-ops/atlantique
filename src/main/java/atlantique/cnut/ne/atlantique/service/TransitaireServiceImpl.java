package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.TransitaireDto;
import atlantique.cnut.ne.atlantique.entity.Transitaire;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.repository.TransitaireRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransitaireServiceImpl implements TransitaireService {

    private final TransitaireRepository transitaireRepository;

    public TransitaireServiceImpl(TransitaireRepository transitaireRepository) {
        this.transitaireRepository = transitaireRepository;
    }

    @Override
    public Transitaire createTransitaire(TransitaireDto transitaireDto) {

         if (transitaireRepository.findByDesignation(transitaireDto.getDesignation()).isPresent()) {
             throw new IllegalArgumentException("Un transitaire avec cette désignation existe déjà.");
         }

        Transitaire transitaire = new Transitaire();
        transitaire.setDesignation(transitaireDto.getDesignation());

        return transitaireRepository.save(transitaire);
    }

    @Override
    public List<Transitaire> findAllTransitaires() {
        return transitaireRepository.findAll();
    }

    @Override
    public Optional<Transitaire> findTransitaireById(String id) {
        return transitaireRepository.findById(id);
    }

    @Override
    public Transitaire updateTransitaire(String id, TransitaireDto transitaireDto) {
        return transitaireRepository.findById(id)
                .map(existingTransitaire -> {
                    existingTransitaire.setDesignation(transitaireDto.getDesignation());

                    return transitaireRepository.save(existingTransitaire);
                }).orElseThrow(() -> new ResourceNotFoundException("Transitaire non trouvé avec l'ID: " + id));
    }

    @Override
    public void deleteTransitaire(String id) {
        if (!transitaireRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transitaire non trouvé avec l'ID: " + id);
        }
        transitaireRepository.deleteById(id);
    }
}