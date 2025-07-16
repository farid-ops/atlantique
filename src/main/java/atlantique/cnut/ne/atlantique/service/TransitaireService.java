package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.TransitaireDto;
import atlantique.cnut.ne.atlantique.entity.Transitaire;

import java.util.List;
import java.util.Optional;

public interface TransitaireService {

    Transitaire createTransitaire(TransitaireDto transitaireDto);

    List<Transitaire> findAllTransitaires();

    Optional<Transitaire> findTransitaireById(String id);

    Transitaire updateTransitaire(String id, TransitaireDto transitaireDto);

    void deleteTransitaire(String id);
}