package atlantique.cnut.ne.atlantique.controller;

import atlantique.cnut.ne.atlantique.dto.MarqueDto;
import atlantique.cnut.ne.atlantique.entity.Marque;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.exceptions.StatusCode;
import atlantique.cnut.ne.atlantique.service.MarqueService;
import atlantique.cnut.ne.atlantique.util.UtilService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1/marques")
@RequiredArgsConstructor
public class MarqueController {

    private final MarqueService marqueService;
    private final UtilService utilService;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> createMarque(@RequestBody @Valid MarqueDto marqueDto) {
        try {
            Marque newMarque = marqueService.createMarque(marqueDto);
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_OK.getStatus_code(),
                            true,
                            "Modèle de véhicule créé avec succès.",
                            newMarque
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
                            "Erreur lors de la création du modèle: " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR')")
    public ResponseEntity<Map<String, Object>> getAllMarques() {
        List<Marque> marques = marqueService.findAllMarques();
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_OK.getStatus_code(),
                        true,
                        "Liste des modèles récupérée avec succès.",
                        marques
                )
        );
    }

    @GetMapping("/paginated")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR')")
    public ResponseEntity<Map<String, Object>> findAllMarquesPaginated(Pageable pageable) {
        Page<Marque> marquePage = marqueService.findAllMarquesPaginated(pageable);
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_OK.getStatus_code(),
                        true,
                        "Liste des modèles paginée récupérée avec succès.",
                        marquePage
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR')")
    public ResponseEntity<Map<String, Object>> getMarqueById(@PathVariable String id) {
        return marqueService.findMarqueById(id)
                .map(marque -> ResponseEntity.ok(
                        utilService.response(
                                StatusCode.HTTP_OK.getStatus_code(),
                                true,
                                "Modèle récupéré avec succès.",
                                marque
                        )
                ))
                .orElseThrow(() -> new ResourceNotFoundException("Modèle non trouvé avec l'ID: " + id));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> updateMarque(@PathVariable String id, @RequestBody @Valid MarqueDto marqueDto) {
        try {
            Marque updatedMarque = marqueService.updateMarque(id, marqueDto);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_OK.getStatus_code(),
                            true,
                            "Modèle mis à jour avec succès.",
                            updatedMarque
                    )
            );
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_INTERNAL_SERVER_ERROR.getStatus_code(),
                            false,
                            "Erreur lors de la mise à jour du modèle: " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteMarque(@PathVariable String id) {
        try {
            marqueService.deleteMarque(id);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_OK.getStatus_code(),
                            true,
                            "Modèle supprimé avec succès.",
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
                            "Erreur lors de la suppression du modèle: " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}