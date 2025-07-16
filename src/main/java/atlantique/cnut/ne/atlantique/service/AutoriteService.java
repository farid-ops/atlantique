package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.AutoriteDto;
import atlantique.cnut.ne.atlantique.entity.Autorite;

import java.util.List;
import java.util.Optional;

public interface AutoriteService {

    Autorite createAutorite(AutoriteDto autoriteDto);
    List<Autorite> findAllAutorites();
    Optional<Autorite> findAutoriteById(String id);
    Autorite updateAutorite(String id, AutoriteDto autoriteDto);
    void deleteAutorite(String id);
}