package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.ConsignataireDto;
import atlantique.cnut.ne.atlantique.entity.Consignataire;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.repository.ConsignataireRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ConsignataireServiceImpl implements ConsignataireService {

    private final ConsignataireRepository consignataireRepository;

    public ConsignataireServiceImpl(ConsignataireRepository consignataireRepository) {
        this.consignataireRepository = consignataireRepository;
    }

    @Override
    public Consignataire createConsignataire(ConsignataireDto consignataireDto) {
         if (consignataireRepository.findByDesignation(consignataireDto.getLettreManifeste()).isPresent()) {
             throw new IllegalArgumentException("Un consignataire avec cette lettre de manifeste existe déjà.");
         }

        Consignataire consignataire = new Consignataire();
        consignataire.setLettreManifeste(consignataireDto.getLettreManifeste());
        consignataire.setDesignation(consignataireDto.getDesignation());

        return consignataireRepository.save(consignataire);
    }

    @Override
    public List<Consignataire> findAllConsignataires() {
        return consignataireRepository.findAll();
    }

    @Override
    public Optional<Consignataire> findConsignataireById(String id) {
        return consignataireRepository.findById(id);
    }

    @Override
    public Consignataire updateConsignataire(String id, ConsignataireDto consignataireDto) {
        return consignataireRepository.findById(id)
                .map(existingConsignataire -> {
                    existingConsignataire.setLettreManifeste(consignataireDto.getLettreManifeste());
                    existingConsignataire.setDesignation(consignataireDto.getDesignation());

                    return consignataireRepository.save(existingConsignataire);
                }).orElseThrow(() -> new ResourceNotFoundException("Consignataire non trouvé avec l'ID: " + id));
    }

    @Override
    public void deleteConsignataire(String id) {
        if (!consignataireRepository.existsById(id)) {
            throw new ResourceNotFoundException("Consignataire non trouvé avec l'ID: " + id);
        }
        consignataireRepository.deleteById(id);
    }
}