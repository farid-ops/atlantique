package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.ConsignataireDto;
import atlantique.cnut.ne.atlantique.entity.Consignataire;

import java.util.List;
import java.util.Optional;

public interface ConsignataireService {

    Consignataire createConsignataire(ConsignataireDto consignataireDto);

    List<Consignataire> findAllConsignataires();

    Optional<Consignataire> findConsignataireById(String id);

    Consignataire updateConsignataire(String id, ConsignataireDto consignataireDto);

    void deleteConsignataire(String id);
}