package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.ArmateurDto;
import atlantique.cnut.ne.atlantique.entity.Armateur;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.repository.ArmateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ArmateurServiceImpl implements ArmateurService {

    private final ArmateurRepository armateurRepository;

    public ArmateurServiceImpl(ArmateurRepository armateurRepository) {
        this.armateurRepository = armateurRepository;
    }

    @Override
    public Armateur createArmateur(ArmateurDto armateurDto) {
         if (armateurRepository.findByDesignation(armateurDto.getDesignation()).isPresent()) {
             throw new IllegalArgumentException("Un armateur avec cette désignation existe déjà.");
         }

        Armateur armateur = new Armateur();
        armateur.setDesignation(armateurDto.getDesignation());

        return armateurRepository.save(armateur);
    }

    @Override
    public List<Armateur> findAllArmateurs() {
        return armateurRepository.findAll();
    }

    @Override
    public Optional<Armateur> findArmateurById(String id) {
        return armateurRepository.findById(id);
    }

    @Override
    public Armateur updateArmateur(String id, ArmateurDto armateurDto) {
        return armateurRepository.findById(id)
                .map(existingArmateur -> {
                    existingArmateur.setDesignation(armateurDto.getDesignation());
                    return armateurRepository.save(existingArmateur);
                }).orElseThrow(() -> new ResourceNotFoundException("Armateur non trouvé avec l'ID: " + id));
    }

    @Override
    public void deleteArmateur(String id) {
        if (!armateurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Armateur non trouvé avec l'ID: " + id);
        }
        armateurRepository.deleteById(id);
    }
}