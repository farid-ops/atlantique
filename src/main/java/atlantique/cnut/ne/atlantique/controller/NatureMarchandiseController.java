package atlantique.cnut.ne.atlantique.controller;

import atlantique.cnut.ne.atlantique.dto.NatureMarchandiseDto;
import atlantique.cnut.ne.atlantique.entity.NatureMarchandise;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.exceptions.StatusCode;
import atlantique.cnut.ne.atlantique.service.NatureMarchandiseService;
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
@RequestMapping("/api/v1/nature-marchandises")
public class NatureMarchandiseController {

    private final NatureMarchandiseService natureMarchandiseService;
    private final UtilService utilService;

    public NatureMarchandiseController(NatureMarchandiseService natureMarchandiseService, UtilService utilService) {
        this.natureMarchandiseService = natureMarchandiseService;
        this.utilService = utilService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> createNatureMarchandise(@RequestBody @Valid NatureMarchandiseDto natureMarchandiseDto) {
        try {
            NatureMarchandise newNatureMarchandise = natureMarchandiseService.createNatureMarchandise(natureMarchandiseDto);
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_NATUREMARCHANDISE_CREATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_NATUREMARCHANDISE_CREATED.getStatus_message(),
                            newNatureMarchandise
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
    public ResponseEntity<Map<String, Object>> findAllNatureMarchandises() {
        List<NatureMarchandise> natureMarchandisePage = natureMarchandiseService.findAllNatureMarchandises();
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_NATUREMARCHANDISE_RETRIEVED.getStatus_code(),
                        true,
                        StatusCode.HTTP_NATUREMARCHANDISE_RETRIEVED.getStatus_message(),
                        natureMarchandisePage
                )
        );
    }

    @GetMapping("/paginated")
//    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN')")
    public ResponseEntity<Map<String, Object>> findAllNatureMarchandisesPaginated(Pageable pageable) {
        Page<NatureMarchandise> natureMarchandisePage = natureMarchandiseService.findAllNatureMarchandisesPaginated(pageable);
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_NATUREMARCHANDISE_RETRIEVED.getStatus_code(),
                        true,
                        StatusCode.HTTP_NATUREMARCHANDISE_RETRIEVED.getStatus_message(),
                        natureMarchandisePage
                )
        );
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN')")
    public ResponseEntity<Map<String, Object>> getNatureMarchandiseById(@PathVariable String id) {
        return natureMarchandiseService.findNatureMarchandiseById(id)
                .map(natureMarchandise -> ResponseEntity.ok(
                        utilService.response(
                                StatusCode.HTTP_NATUREMARCHANDISE_RETRIEVED.getStatus_code(),
                                true,
                                StatusCode.HTTP_NATUREMARCHANDISE_RETRIEVED.getStatus_message(),
                                natureMarchandise
                        )
                ))
                .orElseThrow(() -> new ResourceNotFoundException("NatureMarchandise non trouvée avec l'ID: " + id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> updateNatureMarchandise(@PathVariable String id, @RequestBody @Valid NatureMarchandiseDto natureMarchandiseDto) {
        try {
            NatureMarchandise updatedNatureMarchandise = natureMarchandiseService.updateNatureMarchandise(id, natureMarchandiseDto);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_NATUREMARCHANDISE_UPDATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_NATUREMARCHANDISE_UPDATED.getStatus_message(),
                            updatedNatureMarchandise
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
    public ResponseEntity<Map<String, Object>> deleteNatureMarchandise(@PathVariable String id) {
        try {
            natureMarchandiseService.deleteNatureMarchandise(id);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_NATUREMARCHANDISE_DELETED.getStatus_code(),
                            true,
                            StatusCode.HTTP_NATUREMARCHANDISE_DELETED.getStatus_message(),
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