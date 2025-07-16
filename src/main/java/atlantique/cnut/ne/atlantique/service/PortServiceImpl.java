package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.PortDto;
import atlantique.cnut.ne.atlantique.entity.Port;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.repository.PortRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PortServiceImpl implements PortService {

    private final PortRepository portRepository;
    private final PaysService paysService;

    public PortServiceImpl(PortRepository portRepository, PaysService paysService) {
        this.portRepository = portRepository;
        this.paysService = paysService;
    }

    @Override
    public Port createPort(PortDto portDto) {
        if (paysService.findPaysById(portDto.getIdPays()).isEmpty()) {
            throw new ResourceNotFoundException("Pays non trouvé avec l'ID: " + portDto.getIdPays());
        }

        Port port = new Port();
        port.setIdPays(portDto.getIdPays());
        port.setDesignation(portDto.getDesignation());

        return portRepository.save(port);
    }

    @Override
    public List<Port> findAllPorts() {
        return portRepository.findAll();
    }

    @Override
    public Optional<Port> findPortById(String id) {
        return portRepository.findById(id);
    }

    @Override
    public Port updatePort(String id, PortDto portDto) {
        return portRepository.findById(id)
                .map(existingPort -> {
                    if (paysService.findPaysById(portDto.getIdPays()).isEmpty()) {
                        throw new ResourceNotFoundException("Pays non trouvé avec l'ID: " + portDto.getIdPays());
                    }
                    existingPort.setIdPays(portDto.getIdPays());
                    existingPort.setDesignation(portDto.getDesignation());
                    return portRepository.save(existingPort);
                }).orElseThrow(() -> new ResourceNotFoundException("Port non trouvé avec l'ID: " + id));
    }

    @Override
    public void deletePort(String id) {
        if (!portRepository.existsById(id)) {
            throw new ResourceNotFoundException("Port non trouvé avec l'ID: " + id);
        }
        portRepository.deleteById(id);
    }

    @Override
    public List<Port> findPortsByPaysId(String idPays) {
        if (paysService.findPaysById(idPays).isEmpty()) {
            throw new ResourceNotFoundException("Pays non trouvé avec l'ID: " + idPays);
        }
        return portRepository.findByIdPays(idPays);
    }
}