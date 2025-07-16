package atlantique.cnut.ne.atlantique.controller;

import atlantique.cnut.ne.atlantique.dto.ConsignataireDto;
import atlantique.cnut.ne.atlantique.entity.Consignataire;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.exceptions.StatusCode;
import atlantique.cnut.ne.atlantique.service.ConsignataireService;
import atlantique.cnut.ne.atlantique.util.UtilService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/consignataires")
public class ConsignataireController {

    private final ConsignataireService consignataireService;
    private final UtilService utilService;

    public ConsignataireController(ConsignataireService consignataireService, UtilService utilService) {
        this.consignataireService = consignataireService;
        this.utilService = utilService;
    }


    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> createConsignataire(@RequestBody @Valid ConsignataireDto consignataireDto) {
        try {
            Consignataire newConsignataire = consignataireService.createConsignataire(consignataireDto);
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_CONSIGNATAIRE_CREATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_CONSIGNATAIRE_CREATED.getStatus_message(),
                            newConsignataire
                    ),
                    HttpStatus.CREATED
            );
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_CONFLICT.getStatus_code(),
                            false,
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.CONFLICT
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
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN')")
    public ResponseEntity<Map<String, Object>> getAllConsignataires() {
        List<Consignataire> consignataires = consignataireService.findAllConsignataires();
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_CONSIGNATAIRE_RETRIEVED.getStatus_code(),
                        true,
                        StatusCode.HTTP_CONSIGNATAIRE_RETRIEVED.getStatus_message(),
                        consignataires
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN')")
    public ResponseEntity<Map<String, Object>> getConsignataireById(@PathVariable String id) {
        return consignataireService.findConsignataireById(id)
                .map(consignataire -> ResponseEntity.ok(
                        utilService.response(
                                StatusCode.HTTP_CONSIGNATAIRE_RETRIEVED.getStatus_code(),
                                true,
                                StatusCode.HTTP_CONSIGNATAIRE_RETRIEVED.getStatus_message(),
                                consignataire
                        )
                ))
                .orElseThrow(() -> new ResourceNotFoundException("Consignataire non trouvé avec l'ID: " + id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> updateConsignataire(@PathVariable String id, @RequestBody @Valid ConsignataireDto consignataireDto) {
        try {
            Consignataire updatedConsignataire = consignataireService.updateConsignataire(id, consignataireDto);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_CONSIGNATAIRE_UPDATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_CONSIGNATAIRE_UPDATED.getStatus_message(),
                            updatedConsignataire
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


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteConsignataire(@PathVariable String id) {
        try {
            consignataireService.deleteConsignataire(id);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_CONSIGNATAIRE_DELETED.getStatus_code(),
                            true,
                            StatusCode.HTTP_CONSIGNATAIRE_DELETED.getStatus_message(),
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
}