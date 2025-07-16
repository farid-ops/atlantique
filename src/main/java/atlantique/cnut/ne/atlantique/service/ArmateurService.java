package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.ArmateurDto;
import atlantique.cnut.ne.atlantique.entity.Armateur;

import java.util.List;
import java.util.Optional;

public interface ArmateurService {

    Armateur createArmateur(ArmateurDto armateurDto);

    List<Armateur> findAllArmateurs();

    Optional<Armateur> findArmateurById(String id);

    Armateur updateArmateur(String id, ArmateurDto armateurDto);

    void deleteArmateur(String id);
}