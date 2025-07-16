package atlantique.cnut.ne.atlantique.controller;

import atlantique.cnut.ne.atlantique.dto.PortDto;
import atlantique.cnut.ne.atlantique.entity.Port;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.exceptions.StatusCode;
import atlantique.cnut.ne.atlantique.service.PortService;
import atlantique.cnut.ne.atlantique.service.PaysService;
import atlantique.cnut.ne.atlantique.util.UtilService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ports")
public class PortController {

    private final PortService portService;
    private final UtilService utilService;
    private final PaysService paysService;

    public PortController(PortService portService, UtilService utilService, PaysService paysService) {
        this.portService = portService;
        this.utilService = utilService;
        this.paysService = paysService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> createPort(@RequestBody @Valid PortDto portDto) {
        try {
            Port newPort = portService.createPort(portDto);
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_PORT_CREATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_PORT_CREATED.getStatus_message(),
                            newPort
                    ),
                    HttpStatus.CREATED
            );
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_BAD_REQUEST.getStatus_code(),
                            false,
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_INTERNAL_SERVER_ERROR.getStatus_code(),
                            false,
                            StatusCode.HTTP_INTERNAL_SERVER_ERROR.getStatus_message() + ": " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN', 'SCOPE_CSITE', 'SCOPE_CAISSIER')")
    public ResponseEntity<Map<String, Object>> getAllPorts() {
        List<Port> ports = portService.findAllPorts();
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_PORT_RETRIEVED.getStatus_code(),
                        true,
                        StatusCode.HTTP_PORT_RETRIEVED.getStatus_message(),
                        ports
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN', 'SCOPE_CSITE', 'SCOPE_CAISSIER')")
    public ResponseEntity<Map<String, Object>> getPortById(@PathVariable String id) {
        return portService.findPortById(id)
                .map(port -> ResponseEntity.ok(
                        utilService.response(
                                StatusCode.HTTP_PORT_RETRIEVED.getStatus_code(),
                                true,
                                StatusCode.HTTP_PORT_RETRIEVED.getStatus_message(),
                                port
                        )
                ))
                .orElseThrow(() -> new ResourceNotFoundException("Port non trouvé avec l'ID: " + id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> updatePort(@PathVariable String id, @RequestBody @Valid PortDto portDto) {
        try {
            Port updatedPort = portService.updatePort(id, portDto);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_PORT_UPDATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_PORT_UPDATED.getStatus_message(),
                            updatedPort
                    )
            );
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_BAD_REQUEST.getStatus_code(),
                            false,
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_INTERNAL_SERVER_ERROR.getStatus_code(),
                            false,
                            StatusCode.HTTP_INTERNAL_SERVER_ERROR.getStatus_message() + ": " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> deletePort(@PathVariable String id) {
        try {
            portService.deletePort(id);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_PORT_DELETED.getStatus_code(),
                            true,
                            StatusCode.HTTP_PORT_DELETED.getStatus_message(),
                            null
                    )
            );
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_INTERNAL_SERVER_ERROR.getStatus_code(),
                            false,
                            StatusCode.HTTP_INTERNAL_SERVER_ERROR.getStatus_message() + ": " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/by-pays/{idPays}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN', 'SCOPE_CSITE', 'SCOPE_CAISSIER')")
    public ResponseEntity<Map<String, Object>> getPortsByPaysId(@PathVariable String idPays) {
        try {
            List<Port> ports = portService.findPortsByPaysId(idPays);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_PORT_RETRIEVED.getStatus_code(),
                            true,
                            StatusCode.HTTP_PORT_RETRIEVED.getStatus_message(),
                            ports
                    )
            );
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_NOT_FOUND.getStatus_code(),
                            false,
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_INTERNAL_SERVER_ERROR.getStatus_code(),
                            false,
                            StatusCode.HTTP_INTERNAL_SERVER_ERROR.getStatus_message() + ": " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}