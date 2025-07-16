package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.PaysDto;
import atlantique.cnut.ne.atlantique.entity.Pays;

import java.util.List;
import java.util.Optional;

public interface PaysService {

    Pays createPays(PaysDto paysDto);
    List<Pays> findAllPays();
    Optional<Pays> findPaysById(String id);
    Pays updatePays(String id, PaysDto paysDto);
    void deletePays(String id);
}