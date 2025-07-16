package atlantique.cnut.ne.atlantique.controller;

import atlantique.cnut.ne.atlantique.dto.AutoriteDto;
import atlantique.cnut.ne.atlantique.entity.Autorite;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.exceptions.StatusCode;
import atlantique.cnut.ne.atlantique.service.AutoriteService;
import atlantique.cnut.ne.atlantique.util.UtilService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/autorites")
public class AutoriteController {

    private final AutoriteService autoriteService;
    private final UtilService utilService;

    public AutoriteController(AutoriteService autoriteService, UtilService utilService) {
        this.autoriteService = autoriteService;
        this.utilService = utilService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> createAutorite(@RequestBody @Valid AutoriteDto autoriteDto) {
        try {
            Autorite newAutorite = autoriteService.createAutorite(autoriteDto);
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_AUTORITE_CREATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_AUTORITE_CREATED.getStatus_message(),
                            newAutorite
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
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_STATICIEN')")
    public ResponseEntity<Map<String, Object>> getAllAutorites() {
        List<Autorite> autorites = autoriteService.findAllAutorites();
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_AUTORITE_RETRIEVED.getStatus_code(),
                        true,
                        StatusCode.HTTP_AUTORITE_RETRIEVED.getStatus_message(),
                        autorites
                )
        );
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_STATICIEN')")
    public ResponseEntity<Map<String, Object>> getAutoriteById(@PathVariable String id) {
        return autoriteService.findAutoriteById(id)
                .map(autorite -> ResponseEntity.ok(
                        utilService.response(
                                StatusCode.HTTP_AUTORITE_RETRIEVED.getStatus_code(),
                                true,
                                StatusCode.HTTP_AUTORITE_RETRIEVED.getStatus_message(),
                                autorite
                        )
                ))
                .orElseThrow(() -> new ResourceNotFoundException("Autorité non trouvée avec l'ID: " + id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> updateAutorite(@PathVariable String id, @RequestBody @Valid AutoriteDto autoriteDto) {
        try {
            Autorite updatedAutorite = autoriteService.updateAutorite(id, autoriteDto);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_AUTORITE_UPDATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_AUTORITE_UPDATED.getStatus_message(),
                            updatedAutorite
                    )
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
    public ResponseEntity<Map<String, Object>> deleteAutorite(@PathVariable String id) {
        try {
            autoriteService.deleteAutorite(id);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_AUTORITE_DELETED.getStatus_code(),
                            true,
                            StatusCode.HTTP_AUTORITE_DELETED.getStatus_message(),
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