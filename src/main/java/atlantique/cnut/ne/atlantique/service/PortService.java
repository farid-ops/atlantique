package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.PortDto;
import atlantique.cnut.ne.atlantique.entity.Port;

import java.util.List;
import java.util.Optional;

public interface PortService {

    Port createPort(PortDto portDto);
    List<Port> findAllPorts();
    Optional<Port> findPortById(String id);
    Port updatePort(String id, PortDto portDto);
    void deletePort(String id);
    List<Port> findPortsByPaysId(String idPays);
}