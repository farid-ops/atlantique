package atlantique.cnut.ne.atlantique.controller;

import atlantique.cnut.ne.atlantique.dto.ImportateurDto;
import atlantique.cnut.ne.atlantique.entity.Importateur;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.exceptions.StatusCode;
import atlantique.cnut.ne.atlantique.service.AuthService;
import atlantique.cnut.ne.atlantique.service.ImportateurService;
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
@RequestMapping("/api/v1/importateurs")
public class ImportateurController {

    private final ImportateurService importateurService;
    private final UtilService utilService;
    private final AuthService authService;

    public ImportateurController(ImportateurService importateurService, UtilService utilService, AuthService authService) {
        this.importateurService = importateurService;
        this.utilService = utilService;
        this.authService = authService;
    }


    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> createImportateur(@RequestBody @Valid ImportateurDto importateurDto) {
        try {
            Importateur newImportateur = importateurService.createImportateur(importateurDto);
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_IMPORTATEUR_CREATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_IMPORTATEUR_CREATED.getStatus_message(),
                            newImportateur
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
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ADMIN_GROUPE', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN', 'SCOPE_CAISSIER', 'SCOPE_CSITE')")
    public ResponseEntity<Map<String, Object>> getAllImportateurs() {
        List<Importateur> importateurs;
        if (authService.getLoggedInUserRoles().contains("ADMIN_GROUPE")) {
            String idGroupe = authService.getLoggedInUserGroupId();
            importateurs = importateurService.findByIdGroupe(idGroupe);
        } else {
            importateurs = importateurService.findAllImportateurs();
        }

        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_IMPORTATEUR_RETRIEVED.getStatus_code(),
                        true,
                        StatusCode.HTTP_IMPORTATEUR_RETRIEVED.getStatus_message(),
                        importateurs
                )
        );
    }

    @GetMapping("/paginated")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ADMIN_GROUPE', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN', 'SCOPE_CAISSIER', 'SCOPE_CSITE')")
    public ResponseEntity<Map<String, Object>> findAllImportateursPaginated(Pageable pageable) {
        Page<Importateur> importateurPage;
        if (authService.getLoggedInUserRoles().contains("ADMIN_GROUPE")) {
            String idGroupe = authService.getLoggedInUserGroupId();
            importateurPage = importateurService.findAllImportateursPaginated(pageable, idGroupe);
        } else {
            importateurPage = importateurService.findAllImportateursPaginated(pageable, null);
        }

        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_IMPORTATEUR_RETRIEVED.getStatus_code(),
                        true,
                        StatusCode.HTTP_IMPORTATEUR_RETRIEVED.getStatus_message(),
                        importateurPage
                )
        );
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN')")
    public ResponseEntity<Map<String, Object>> getImportateurById(@PathVariable String id) {
        return importateurService.findImportateurById(id)
                .map(importateur -> ResponseEntity.ok(
                        utilService.response(
                                StatusCode.HTTP_IMPORTATEUR_RETRIEVED.getStatus_code(),
                                true,
                                StatusCode.HTTP_IMPORTATEUR_RETRIEVED.getStatus_message(),
                                importateur
                        )
                ))
                .orElseThrow(() -> new ResourceNotFoundException("Importateur non trouvé avec l'ID: " + id));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ADMIN_GROUPE')")
    public ResponseEntity<Map<String, Object>> updateImportateur(@PathVariable String id, @RequestBody @Valid ImportateurDto importateurDto) {
        try {
            Importateur updatedImportateur = importateurService.updateImportateur(id, importateurDto);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_IMPORTATEUR_UPDATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_IMPORTATEUR_UPDATED.getStatus_message(),
                            updatedImportateur
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
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ADMIN_GROUPE')")
    public ResponseEntity<Map<String, Object>> deleteImportateur(@PathVariable String id) {
        try {
            importateurService.deleteImportateur(id);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_IMPORTATEUR_DELETED.getStatus_code(),
                            true,
                            StatusCode.HTTP_IMPORTATEUR_DELETED.getStatus_message(),
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