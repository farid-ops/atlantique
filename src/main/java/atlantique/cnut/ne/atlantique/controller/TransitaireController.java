package atlantique.cnut.ne.atlantique.controller;

import atlantique.cnut.ne.atlantique.dto.TransitaireDto;
import atlantique.cnut.ne.atlantique.entity.Transitaire;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.exceptions.StatusCode;
import atlantique.cnut.ne.atlantique.service.TransitaireService;
import atlantique.cnut.ne.atlantique.util.UtilService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transitaires")
public class TransitaireController {

    private final TransitaireService transitaireService;
    private final UtilService utilService;

    public TransitaireController(TransitaireService transitaireService, UtilService utilService) {
        this.transitaireService = transitaireService;
        this.utilService = utilService;
    }


    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> createTransitaire(@RequestBody @Valid TransitaireDto transitaireDto) {
        try {
            Transitaire newTransitaire = transitaireService.createTransitaire(transitaireDto);
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_TRANSITAIRE_CREATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_TRANSITAIRE_CREATED.getStatus_message(),
                            newTransitaire
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
//    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN')")
    public ResponseEntity<Map<String, Object>> getAllTransitaires(Pageable pageable) {
        Page<Transitaire> transitairePage = transitaireService.findAllTransitairesPaginated(pageable);
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_TRANSITAIRE_RETRIEVED.getStatus_code(),
                        true,
                        StatusCode.HTTP_TRANSITAIRE_RETRIEVED.getStatus_message(),
                        transitairePage
                )
        );
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN')")
    public ResponseEntity<Map<String, Object>> getTransitaireById(@PathVariable String id) {
        return transitaireService.findTransitaireById(id)
                .map(transitaire -> ResponseEntity.ok(
                        utilService.response(
                                StatusCode.HTTP_TRANSITAIRE_RETRIEVED.getStatus_code(),
                                true,
                                StatusCode.HTTP_TRANSITAIRE_RETRIEVED.getStatus_message(),
                                transitaire
                        )
                ))
                .orElseThrow(() -> new ResourceNotFoundException("Transitaire non trouvé avec l'ID: " + id));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> updateTransitaire(@PathVariable String id, @RequestBody @Valid TransitaireDto transitaireDto) {
        try {
            Transitaire updatedTransitaire = transitaireService.updateTransitaire(id, transitaireDto);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_TRANSITAIRE_UPDATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_TRANSITAIRE_UPDATED.getStatus_message(),
                            updatedTransitaire
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
    public ResponseEntity<Map<String, Object>> deleteTransitaire(@PathVariable String id) {
        try {
            transitaireService.deleteTransitaire(id);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_TRANSITAIRE_DELETED.getStatus_code(),
                            true,
                            StatusCode.HTTP_TRANSITAIRE_DELETED.getStatus_message(),
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