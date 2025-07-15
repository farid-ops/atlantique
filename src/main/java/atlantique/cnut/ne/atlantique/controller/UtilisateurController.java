package atlantique.cnut.ne.atlantique.controller;

import atlantique.cnut.ne.atlantique.dto.UtilisateurDto;
import atlantique.cnut.ne.atlantique.entity.Utilisateur;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.exceptions.StatusCode;
import atlantique.cnut.ne.atlantique.service.UtilisateurService;
import atlantique.cnut.ne.atlantique.util.UtilService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final UtilService utilService;

    public UtilisateurController(UtilisateurService utilisateurService, UtilService utilService) {
        this.utilisateurService = utilisateurService;
        this.utilService = utilService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> createUtilisateur(@RequestBody @Valid UtilisateurDto utilisateurDto) {
        try {
            Utilisateur newUser = utilisateurService.createUtilisateur(utilisateurDto);
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_UTILISATEUR_CREATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_UTILISATEUR_CREATED.getStatus_message(),
                            newUser
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
    public ResponseEntity<Map<String, Object>> getAllUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurService.findAllUtilisateurs();
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_UTILISATEUR_RETRIEVED.getStatus_code(),
                        true,
                        StatusCode.HTTP_UTILISATEUR_RETRIEVED.getStatus_message(),
                        utilisateurs
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOOPE_OPERATEUR', 'SCOPE_STATICIEN', 'SCOPE_USER')")
    public ResponseEntity<Map<String, Object>> getUtilisateurById(@PathVariable String id) {
        return utilisateurService.findUtilisateurById(id)
                .map(utilisateur -> ResponseEntity.ok(
                        utilService.response(
                                StatusCode.HTTP_UTILISATEUR_RETRIEVED.getStatus_code(),
                                true,
                                StatusCode.HTTP_UTILISATEUR_RETRIEVED.getStatus_message(),
                                utilisateur
                        )
                ))
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> updateUtilisateur(@PathVariable String id, @RequestBody @Valid UtilisateurDto utilisateurDto) {
        try {
            Utilisateur updatedUser = utilisateurService.updateUtilisateur(id, utilisateurDto);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_UTILISATEUR_UPDATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_UTILISATEUR_UPDATED.getStatus_message(),
                            updatedUser
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
    public ResponseEntity<Map<String, Object>> deleteUtilisateur(@PathVariable String id) {
        try {
            utilisateurService.deleteUtilisateur(id);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_UTILISATEUR_DELETED.getStatus_code(),
                            true,
                            StatusCode.HTTP_UTILISATEUR_DELETED.getStatus_message(),
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