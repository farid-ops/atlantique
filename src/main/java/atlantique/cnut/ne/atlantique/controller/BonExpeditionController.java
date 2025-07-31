package atlantique.cnut.ne.atlantique.controller;

import atlantique.cnut.ne.atlantique.dto.BonExpeditionDto;
import atlantique.cnut.ne.atlantique.entity.BonExpedition;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.exceptions.StatusCode;
import atlantique.cnut.ne.atlantique.service.BonExpeditionService;
import atlantique.cnut.ne.atlantique.util.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/v1/bon-expeditions")
@RequiredArgsConstructor
@Slf4j
public class BonExpeditionController {

    private final BonExpeditionService bonExpeditionService;
    private final UtilService utilService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR')")
    public ResponseEntity<Map<String, Object>> createBonExpedition(@RequestBody @Valid BonExpeditionDto bonExpeditionDto) {
        try {
            BonExpedition newBonExpedition = bonExpeditionService.createBonExpedition(bonExpeditionDto);
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_BONEXPEDITION_CREATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_BONEXPEDITION_CREATED.getStatus_message(),
                            newBonExpedition
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

    @PostMapping("/from-marchandise/{marchandiseId}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR')")
    public ResponseEntity<Map<String, Object>> createBonExpeditionFromMarchandise(
            @PathVariable String marchandiseId,
            @RequestBody @Valid BonExpeditionDto bonExpeditionDto) {
        try {
            BonExpedition newBonExpedition = bonExpeditionService.createBonExpeditionFromMarchandise(marchandiseId, bonExpeditionDto);
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_BONEXPEDITION_CREATED.getStatus_code(),
                            true,
                            "Bon d'Expédition créé et marchandise mise à jour avec succès.",
                            newBonExpedition
                    ),
                    HttpStatus.CREATED
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
        } catch (IllegalArgumentException e) {
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
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_CAISSIER', 'SCOPE_STATICIEN', 'SCOPE_CSITE')")
    public ResponseEntity<Map<String, Object>> getAllBonExpeditions() {
        List<BonExpedition> bonExpeditions = bonExpeditionService.findAllBonExpeditions();
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_BONEXPEDITION_RETRIEVED.getStatus_code(),
                        true,
                        StatusCode.HTTP_BONEXPEDITION_RETRIEVED.getStatus_message(),
                        bonExpeditions
                )
        );
    }

    @GetMapping("/paginated")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_CAISSIER', 'SCOPE_STATICIEN', 'SCOPE_CSITE')")
    public ResponseEntity<Map<String, Object>> getAllBonExpeditionsPaginated(Pageable pageable) {
        Page<BonExpedition> bonExpeditionPage = bonExpeditionService.findAllBonExpeditionsPaginated(pageable);
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_BONEXPEDITION_RETRIEVED.getStatus_code(),
                        true,
                        StatusCode.HTTP_BONEXPEDITION_RETRIEVED.getStatus_message(),
                        bonExpeditionPage
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_CAISSIER', 'SCOPE_STATICIEN', 'SCOPE_CSITE')")
    public ResponseEntity<Map<String, Object>> getBonExpeditionById(@PathVariable String id) {
        return bonExpeditionService.findBonExpeditionById(id)
                .map(bonExpedition -> ResponseEntity.ok(
                        utilService.response(
                                StatusCode.HTTP_BONEXPEDITION_RETRIEVED.getStatus_code(),
                                true,
                                StatusCode.HTTP_BONEXPEDITION_RETRIEVED.getStatus_message(),
                                bonExpedition
                        )
                ))
                .orElseThrow(() -> new ResourceNotFoundException("Bon d'Expédition non trouvé avec l'ID: " + id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR')")
    public ResponseEntity<Map<String, Object>> updateBonExpedition(@PathVariable String id, @RequestBody @Valid BonExpeditionDto bonExpeditionDto) {
        try {
            BonExpedition updatedBonExpedition = bonExpeditionService.updateBonExpedition(id, bonExpeditionDto);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_BONEXPEDITION_UPDATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_BONEXPEDITION_UPDATED.getStatus_message(),
                            updatedBonExpedition
                    )
            );
        } catch (ResourceNotFoundException e) {
            throw e;
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
        }
        catch (Exception e) {
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
    public ResponseEntity<Map<String, Object>> deleteBonExpedition(@PathVariable String id) {
        try {
            bonExpeditionService.deleteBonExpedition(id);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_BONEXPEDITION_DELETED.getStatus_code(),
                            true,
                            StatusCode.HTTP_BONEXPEDITION_DELETED.getStatus_message(),
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