package atlantique.cnut.ne.atlantique.controller;

import atlantique.cnut.ne.atlantique.dto.MarchandiseDto;
import atlantique.cnut.ne.atlantique.entity.Marchandise;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.exceptions.StatusCode;
import atlantique.cnut.ne.atlantique.service.MarchandiseService;
import atlantique.cnut.ne.atlantique.util.UtilService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/marchandises")
public class MarchandiseController {

    private final MarchandiseService marchandiseService;
    private final UtilService utilService;

    public MarchandiseController(MarchandiseService marchandiseService, UtilService utilService) {
        this.marchandiseService = marchandiseService;
        this.utilService = utilService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> createMarchandise(@RequestBody @Valid MarchandiseDto marchandiseDto) {
        try {
            Marchandise newMarchandise = marchandiseService.createMarchandise(marchandiseDto);
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_MARCHANDISE_CREATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_MARCHANDISE_CREATED.getStatus_message(),
                            newMarchandise
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
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN', 'SCOPE_CSITE', 'SCOPE_CAISSIER')")
    public ResponseEntity<Map<String, Object>> getAllMarchandises(Pageable pageable) {
        Page<Marchandise> marchandisePage = marchandiseService.findAllMarchandisesPaginated(pageable);
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_MARCHANDISE_RETRIEVED.getStatus_code(),
                        true,
                        StatusCode.HTTP_MARCHANDISE_RETRIEVED.getStatus_message(),
                        marchandisePage
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN', 'SCOPE_CSITE', 'SCOPE_CAISSIER')")
    public ResponseEntity<Map<String, Object>> getMarchandiseById(@PathVariable String id) {
        return marchandiseService.findMarchandiseById(id)
                .map(marchandise -> ResponseEntity.ok(
                        utilService.response(
                                StatusCode.HTTP_MARCHANDISE_RETRIEVED.getStatus_code(),
                                true,
                                StatusCode.HTTP_MARCHANDISE_RETRIEVED.getStatus_message(),
                                marchandise
                        )
                ))
                .orElseThrow(() -> new ResourceNotFoundException("Marchandise non trouvée avec l'ID: " + id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> updateMarchandise(@PathVariable String id, @RequestBody @Valid MarchandiseDto marchandiseDto) {
        try {
            Marchandise updatedMarchandise = marchandiseService.updateMarchandise(id, marchandiseDto);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_MARCHANDISE_UPDATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_MARCHANDISE_UPDATED.getStatus_message(),
                            updatedMarchandise
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
    public ResponseEntity<Map<String, Object>> deleteMarchandise(@PathVariable String id) {
        try {
            marchandiseService.deleteMarchandise(id);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_MARCHANDISE_DELETED.getStatus_code(),
                            true,
                            StatusCode.HTTP_MARCHANDISE_DELETED.getStatus_message(),
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