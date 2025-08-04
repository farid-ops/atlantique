package atlantique.cnut.ne.atlantique.controller;

import atlantique.cnut.ne.atlantique.dto.PaysDto;
import atlantique.cnut.ne.atlantique.entity.Pays;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.exceptions.StatusCode;
import atlantique.cnut.ne.atlantique.service.PaysService;
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
@RequestMapping("/api/v1/pays")
public class PaysController {

    private final PaysService paysService;
    private final UtilService utilService;

    public PaysController(PaysService paysService, UtilService utilService) {
        this.paysService = paysService;
        this.utilService = utilService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> createPays(@RequestBody @Valid PaysDto paysDto) {
        try {
            Pays newPays = paysService.createPays(paysDto);
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_PAYS_CREATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_PAYS_CREATED.getStatus_message(),
                            newPays
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
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ADMIN_GROUPE', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN', 'SCOPE_CSITE', 'SCOPE_CAISSIER')")
    public ResponseEntity<Map<String, Object>> getAllPays() {
        List<Pays> paysList = paysService.findAllPays();
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_PAYS_RETRIEVED.getStatus_code(),
                        true,
                        StatusCode.HTTP_PAYS_RETRIEVED.getStatus_message(),
                        paysList
                )
        );
    }

    @GetMapping("/paginated")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN', 'SCOPE_CSITE', 'SCOPE_CAISSIER')")
    public ResponseEntity<Map<String, Object>> getAllPaysPaginated(Pageable pageable) {
        Page<Pays> paysPage = paysService.findAllPaysPaginated(pageable);
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_PAYS_RETRIEVED.getStatus_code(),
                        true,
                        StatusCode.HTTP_PAYS_RETRIEVED.getStatus_message(),
                        paysPage
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN', 'SCOPE_CSITE', 'SCOPE_CAISSIER')")
    public ResponseEntity<Map<String, Object>> getPaysById(@PathVariable String id) {
        return paysService.findPaysById(id)
                .map(pays -> ResponseEntity.ok(
                        utilService.response(
                                StatusCode.HTTP_PAYS_RETRIEVED.getStatus_code(),
                                true,
                                StatusCode.HTTP_PAYS_RETRIEVED.getStatus_message(),
                                pays
                        )
                ))
                .orElseThrow(() -> new ResourceNotFoundException("Pays non trouvé avec l'ID: " + id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> updatePays(@PathVariable String id, @RequestBody @Valid PaysDto paysDto) {
        try {
            Pays updatedPays = paysService.updatePays(id, paysDto);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_PAYS_UPDATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_PAYS_UPDATED.getStatus_message(),
                            updatedPays
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
    public ResponseEntity<Map<String, Object>> deletePays(@PathVariable String id) {
        try {
            paysService.deletePays(id);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_PAYS_DELETED.getStatus_code(),
                            true,
                            StatusCode.HTTP_PAYS_DELETED.getStatus_message(),
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