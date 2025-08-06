package atlantique.cnut.ne.atlantique.controller;

import atlantique.cnut.ne.atlantique.dto.GammeDto;
import atlantique.cnut.ne.atlantique.entity.Gamme;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.exceptions.StatusCode;
import atlantique.cnut.ne.atlantique.service.GammeService;
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
@RequestMapping("/api/v1/gammes")
@RequiredArgsConstructor
public class GammeController {

    private final GammeService gammeService;
    private final UtilService utilService;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> createGamme(@RequestBody @Valid GammeDto gammeDto) {
        try {
            Gamme newGamme = gammeService.createGamme(gammeDto);
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_OK.getStatus_code(),
                            true,
                            "Gamme créée avec succès.",
                            newGamme
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
                            "Erreur lors de la création de la gamme: " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR')")
    public ResponseEntity<Map<String, Object>> getAllGammes() {
        List<Gamme> gammes = gammeService.findAllGammes();
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_OK.getStatus_code(),
                        true,
                        "Liste des gammes récupérée avec succès.",
                        gammes
                )
        );
    }

    @GetMapping("/paginated")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR')")
    public ResponseEntity<Map<String, Object>> findAllGammesPaginated(Pageable pageable) {
        Page<Gamme> gammePage = gammeService.findAllGammesPaginated(pageable);
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_OK.getStatus_code(),
                        true,
                        "Liste des gammes paginée récupérée avec succès.",
                        gammePage
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR')")
    public ResponseEntity<Map<String, Object>> getGammeById(@PathVariable String id) {
        return gammeService.findGammeById(id)
                .map(gamme -> ResponseEntity.ok(
                        utilService.response(
                                StatusCode.HTTP_OK.getStatus_code(),
                                true,
                                "Gamme récupérée avec succès.",
                                gamme
                        )
                ))
                .orElseThrow(() -> new ResourceNotFoundException("Gamme non trouvée avec l'ID: " + id));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> updateGamme(@PathVariable String id, @RequestBody @Valid GammeDto gammeDto) {
        try {
            Gamme updatedGamme = gammeService.updateGamme(id, gammeDto);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_OK.getStatus_code(),
                            true,
                            "Gamme mise à jour avec succès.",
                            updatedGamme
                    )
            );
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_INTERNAL_SERVER_ERROR.getStatus_code(),
                            false,
                            "Erreur lors de la mise à jour de la gamme: " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteGamme(@PathVariable String id) {
        try {
            gammeService.deleteGamme(id);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_OK.getStatus_code(),
                            true,
                            "Gamme supprimée avec succès.",
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
                            "Erreur lors de la suppression de la gamme: " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}