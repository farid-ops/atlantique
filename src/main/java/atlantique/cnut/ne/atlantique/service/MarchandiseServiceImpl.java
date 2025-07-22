package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.MarchandiseDto;
import atlantique.cnut.ne.atlantique.entity.*; // Importez toutes les entités nécessaires
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.repository.*; // Importez tous les repositories nécessaires
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MarchandiseServiceImpl implements MarchandiseService {

    private final MarchandiseRepository marchandiseRepository;
    private final NatureMarchandiseRepository natureMarchandiseRepository;
    private final ArmateurRepository armateurRepository;
    private final TransitaireRepository transitaireRepository;
    private final ImportateurRepository importateurRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final BlRepository blRepository;
    private final CargaisonRepository cargaisonRepository;

    public MarchandiseServiceImpl(
            MarchandiseRepository marchandiseRepository,
            NatureMarchandiseRepository natureMarchandiseRepository,
            ArmateurRepository armateurRepository,
            TransitaireRepository transitaireRepository,
            ImportateurRepository importateurRepository,
            UtilisateurRepository utilisateurRepository,
            BlRepository blRepository,
            CargaisonRepository cargaisonRepository) {
        this.marchandiseRepository = marchandiseRepository;
        this.natureMarchandiseRepository = natureMarchandiseRepository;
        this.armateurRepository = armateurRepository;
        this.transitaireRepository = transitaireRepository;
        this.importateurRepository = importateurRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.blRepository = blRepository;
        this.cargaisonRepository = cargaisonRepository;
    }

    @Override
    public Marchandise createMarchandise(MarchandiseDto marchandiseDto) {

        Marchandise marchandise = new Marchandise();
        marchandise.setCaf(marchandiseDto.getCaf());
        marchandise.setPoids(marchandiseDto.getPoids());
        marchandise.setType(marchandiseDto.getType());
        marchandise.setNombreColis(marchandiseDto.getNombreColis());
        marchandise.setNumeroChassis(marchandiseDto.getNumeroChassis());
        marchandise.setNumeroDouane(marchandiseDto.getNumeroDouane());
        marchandise.setNombreConteneur(marchandiseDto.getNombreConteneur());
        marchandise.setRegularisation(marchandiseDto.getRegularisation() != null ? marchandiseDto.getRegularisation() : false);
        marchandise.setExoneration(marchandiseDto.getExoneration() != null ? marchandiseDto.getExoneration() : false);
        marchandise.setConteneur(marchandiseDto.getConteneur() != null ? marchandiseDto.getConteneur() : false);
        marchandise.setTypeConteneur(marchandiseDto.getTypeConteneur());
        marchandise.setVolume(marchandiseDto.getVolume());
        marchandise.setObservation(marchandiseDto.getObservation());
        marchandise.setNumVoyage(marchandiseDto.getNumVoyage());
        marchandise.setAtb(marchandiseDto.getAtb());
        marchandise.setEncours(marchandiseDto.getEncours());
        marchandise.setRcnut1(marchandiseDto.getRcnut1());
        marchandise.setRcnut2(marchandiseDto.getRcnut2());
        marchandise.setTotalQuitance(marchandiseDto.getTotalQuitance());
        marchandise.setAbic(marchandiseDto.getAbic());
        marchandise.setTcps(marchandiseDto.getTcps());
        marchandise.setBe(marchandiseDto.getBe());
        marchandise.setVisa(marchandiseDto.getVisa());
        marchandise.setValidation(marchandiseDto.getValidation());

        marchandise.setIdNatureMarchandise(marchandiseDto.getIdNatureMarchandise());
        marchandise.setIdArmateur(marchandiseDto.getIdArmateur());
        marchandise.setIdTransitaire(marchandiseDto.getIdTransitaire());
        marchandise.setIdImportateur(marchandiseDto.getIdImportateur());
        marchandise.setIdUtilisateur(marchandiseDto.getIdUtilisateur());


        return marchandiseRepository.save(marchandise);
    }

    @Override
    public List<Marchandise> findAllMarchandises() {
        return marchandiseRepository.findAll();
    }

    @Override
    public Page<Marchandise> findAllMarchandisesPaginated(Pageable pageable) {
        return marchandiseRepository.findAll(pageable);
    }

    @Override
    public Optional<Marchandise> findMarchandiseById(String id) {
        return marchandiseRepository.findById(id);
    }

    @Override
    public Marchandise updateMarchandise(String id, MarchandiseDto marchandiseDto) {
        return marchandiseRepository.findById(id)
                .map(existingMarchandise -> {
                    // Mettez à jour les champs simples
                    existingMarchandise.setCaf(marchandiseDto.getCaf());
                    existingMarchandise.setPoids(marchandiseDto.getPoids());
                    existingMarchandise.setType(marchandiseDto.getType());
                    existingMarchandise.setNombreColis(marchandiseDto.getNombreColis());
                    existingMarchandise.setNumeroChassis(marchandiseDto.getNumeroChassis());
                    existingMarchandise.setNumeroDouane(marchandiseDto.getNumeroDouane());
                    existingMarchandise.setNombreConteneur(marchandiseDto.getNombreConteneur());
                    existingMarchandise.setRegularisation(marchandiseDto.getRegularisation() != null ? marchandiseDto.getRegularisation() : false);
                    existingMarchandise.setExoneration(marchandiseDto.getExoneration() != null ? marchandiseDto.getExoneration() : false);
                    existingMarchandise.setConteneur(marchandiseDto.getConteneur() != null ? marchandiseDto.getConteneur() : false);
                    existingMarchandise.setTypeConteneur(marchandiseDto.getTypeConteneur());
                    existingMarchandise.setVolume(marchandiseDto.getVolume());
                    existingMarchandise.setObservation(marchandiseDto.getObservation());
                    existingMarchandise.setNumVoyage(marchandiseDto.getNumVoyage());
                    existingMarchandise.setAtb(marchandiseDto.getAtb());
                    existingMarchandise.setEncours(marchandiseDto.getEncours());
                    existingMarchandise.setRcnut1(marchandiseDto.getRcnut1());
                    existingMarchandise.setRcnut2(marchandiseDto.getRcnut2());
                    existingMarchandise.setTotalQuitance(marchandiseDto.getTotalQuitance());
                    existingMarchandise.setAbic(marchandiseDto.getAbic());
                    existingMarchandise.setTcps(marchandiseDto.getTcps());
                    existingMarchandise.setBe(marchandiseDto.getBe());
                    existingMarchandise.setVisa(marchandiseDto.getVisa());
                    existingMarchandise.setValidation(marchandiseDto.getValidation());

                    existingMarchandise.setIdNatureMarchandise(natureMarchandiseRepository.findById(marchandiseDto.getIdNatureMarchandise()).get().getId());
                    existingMarchandise.setIdArmateur(armateurRepository.findById(marchandiseDto.getIdArmateur()).get().getId());
                    existingMarchandise.setIdUtilisateur((utilisateurRepository.findById(marchandiseDto.getIdUtilisateur()).get().getId()));
                    existingMarchandise.setIdTransitaire((transitaireRepository.findById(marchandiseDto.getIdTransitaire()).get().getId()));
                    existingMarchandise.setIdImportateur((importateurRepository.findById(marchandiseDto.getIdImportateur()).get().getId()));

                    return marchandiseRepository.save(existingMarchandise);
                }).orElseThrow(() -> new ResourceNotFoundException("Marchandise non trouvée avec l'ID: " + id));
    }

    @Override
    public void deleteMarchandise(String id) {
        if (!marchandiseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Marchandise non trouvée avec l'ID: " + id);
        }
        marchandiseRepository.deleteById(id);
    }
}