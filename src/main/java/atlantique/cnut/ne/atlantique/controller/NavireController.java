package atlantique.cnut.ne.atlantique.controller;

import atlantique.cnut.ne.atlantique.dto.NavireDto;
import atlantique.cnut.ne.atlantique.entity.Navire;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.exceptions.StatusCode;
import atlantique.cnut.ne.atlantique.service.NavireService;
import atlantique.cnut.ne.atlantique.util.UtilService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/navires")
public class NavireController {

    private final NavireService navireService;
    private final UtilService utilService;

    public NavireController(NavireService navireService, UtilService utilService) {
        this.navireService = navireService;
        this.utilService = utilService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> createNavire(@RequestBody @Valid NavireDto navireDto) {
        try {
            Navire newNavire = navireService.createNavire(navireDto);
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_NAVIRE_CREATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_NAVIRE_CREATED.getStatus_message(),
                            newNavire
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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATEUR', 'STATICIEN')")
    public ResponseEntity<Map<String, Object>> getAllNavires() {
        List<Navire> navires = navireService.findAllNavires();
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_NAVIRE_RETRIEVED.getStatus_code(),
                        true,
                        StatusCode.HTTP_NAVIRE_RETRIEVED.getStatus_message(),
                        navires
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATEUR', 'STATICIEN')")
    public ResponseEntity<Map<String, Object>> getNavireById(@PathVariable String id) {
        return navireService.findNavireById(id)
                .map(navire -> ResponseEntity.ok(
                        utilService.response(
                                StatusCode.HTTP_NAVIRE_RETRIEVED.getStatus_code(),
                                true,
                                StatusCode.HTTP_NAVIRE_RETRIEVED.getStatus_message(),
                                navire
                        )
                ))
                .orElseThrow(() -> new ResourceNotFoundException("Navire non trouvé avec l'ID: " + id));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateNavire(@PathVariable String id, @RequestBody @Valid NavireDto navireDto) {
        try {
            Navire updatedNavire = navireService.updateNavire(id, navireDto);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_NAVIRE_UPDATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_NAVIRE_UPDATED.getStatus_message(),
                            updatedNavire
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
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteNavire(@PathVariable String id) {
        try {
            navireService.deleteNavire(id);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_NAVIRE_DELETED.getStatus_code(),
                            true,
                            StatusCode.HTTP_NAVIRE_DELETED.getStatus_message(),
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