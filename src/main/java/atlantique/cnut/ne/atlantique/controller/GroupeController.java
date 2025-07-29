package atlantique.cnut.ne.atlantique.controller;

import atlantique.cnut.ne.atlantique.dto.GroupeDto;
import atlantique.cnut.ne.atlantique.entity.Groupe;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.exceptions.StatusCode;
import atlantique.cnut.ne.atlantique.service.GroupeService;
import atlantique.cnut.ne.atlantique.util.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/groupes")
@RequiredArgsConstructor
@Slf4j
public class GroupeController {

    private final GroupeService groupeService;
    private final UtilService utilService;

    @PostMapping(consumes = { "multipart/form-data" })
    @PreAuthorize("hasAuthority('SCOPE_SUPERUTILISATEUR')")
    public ResponseEntity<Map<String, Object>> createGroupe(
            @RequestPart("groupe") @Valid GroupeDto groupeDto,
            @RequestPart(value = "logoFile", required = false) MultipartFile logoFile,
            @RequestPart(value = "signatureFile", required = false) MultipartFile signatureFile) {
        try {
            Groupe newGroupe = groupeService.createGroupe(groupeDto, logoFile, signatureFile);
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_OK.getStatus_code(),
                            true,
                            "Groupe créé avec succès.",
                            newGroupe
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
                            "Erreur lors de la création du groupe: " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPERUTILISATEUR', 'SCOPE_ADMINISTRATEUR_GROUPE', 'SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getGroupeById(@PathVariable String id) {
        return groupeService.findGroupeById(id)
                .map(groupe -> ResponseEntity.ok(
                        utilService.response(
                                StatusCode.HTTP_OK.getStatus_code(),
                                true,
                                "Groupe récupéré avec succès.",
                                groupe
                        )
                ))
                .orElseThrow(() -> new ResourceNotFoundException("Groupe non trouvé avec l'ID: " + id));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPERUTILISATEUR', 'SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllGroupes() {
        List<Groupe> groupes = groupeService.findAllGroupes();
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_OK.getStatus_code(),
                        true,
                        "Liste des groupes récupérée avec succès.",
                        groupes
                )
        );
    }

    @GetMapping("/paginated")
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPERUTILISATEUR', 'SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllGroupesPaginated(Pageable pageable) {
        Page<Groupe> groupePage = groupeService.findAllGroupesPaginated(pageable);
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_OK.getStatus_code(),
                        true,
                        "Liste des groupes paginée récupérée avec succès.",
                        groupePage
                )
        );
    }

    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    @PreAuthorize("hasAuthority('SCOPE_SUPERUTILISATEUR')")
    public ResponseEntity<Map<String, Object>> updateGroupe(
            @PathVariable String id,
            @RequestPart("groupe") @Valid GroupeDto groupeDto,
            @RequestPart(value = "logoFile", required = false) MultipartFile logoFile,
            @RequestPart(value = "signatureFile", required = false) MultipartFile signatureFile) {
        try {
            Groupe updatedGroupe = groupeService.updateGroupe(id, groupeDto, logoFile, signatureFile);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_OK.getStatus_code(),
                            true,
                            "Groupe mis à jour avec succès.",
                            updatedGroupe
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
        } catch (Exception e) {
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_INTERNAL_SERVER_ERROR.getStatus_code(),
                            false,
                            "Erreur lors de la mise à jour du groupe: " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_SUPERUTILISATEUR')")
    public ResponseEntity<Map<String, Object>> deleteGroupe(@PathVariable String id) {
        try {
            groupeService.deleteGroupe(id);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_OK.getStatus_code(),
                            true,
                            "Groupe supprimé avec succès.",
                            null
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
        } catch (Exception e) {
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_INTERNAL_SERVER_ERROR.getStatus_code(),
                            false,
                            "Erreur lors de la suppression du groupe: " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}