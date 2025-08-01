package atlantique.cnut.ne.atlantique.controller;

import atlantique.cnut.ne.atlantique.dto.ArmateurDto;
import atlantique.cnut.ne.atlantique.entity.Armateur;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.exceptions.StatusCode;
import atlantique.cnut.ne.atlantique.service.ArmateurService;
import atlantique.cnut.ne.atlantique.util.UtilService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/armateurs")
public class ArmateurController {

    private final ArmateurService armateurService;
    private final UtilService utilService;

    public ArmateurController(ArmateurService armateurService, UtilService utilService) {
        this.armateurService = armateurService;
        this.utilService = utilService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> createArmateur(@RequestBody @Valid ArmateurDto armateurDto) {
        try {
            Armateur newArmateur = armateurService.createArmateur(armateurDto);
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_ARMATEUR_CREATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_ARMATEUR_CREATED.getStatus_message(),
                            newArmateur
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
    public ResponseEntity<Map<String, Object>> getAllArmateurs() {
        List<Armateur> armateurs = armateurService.findAllArmateurs();
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_ARMATEUR_RETRIEVED.getStatus_code(),
                        true,
                        StatusCode.HTTP_ARMATEUR_RETRIEVED.getStatus_message(),
                        armateurs
                )
        );
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN')")
    public ResponseEntity<Map<String, Object>> getArmateurById(@PathVariable String id) {
        return armateurService.findArmateurById(id)
                .map(armateur -> ResponseEntity.ok(
                        utilService.response(
                                StatusCode.HTTP_ARMATEUR_RETRIEVED.getStatus_code(),
                                true,
                                StatusCode.HTTP_ARMATEUR_RETRIEVED.getStatus_message(),
                                armateur
                        )
                ))
                .orElseThrow(() -> new ResourceNotFoundException("Armateur non trouvé avec l'ID: " + id));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> updateArmateur(@PathVariable String id, @RequestBody @Valid ArmateurDto armateurDto) {
        try {
            Armateur updatedArmateur = armateurService.updateArmateur(id, armateurDto);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_ARMATEUR_UPDATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_ARMATEUR_UPDATED.getStatus_message(),
                            updatedArmateur
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
    public ResponseEntity<Map<String, Object>> deleteArmateur(@PathVariable String id) {
        try {
            armateurService.deleteArmateur(id);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_ARMATEUR_DELETED.getStatus_code(),
                            true,
                            StatusCode.HTTP_ARMATEUR_DELETED.getStatus_message(),
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