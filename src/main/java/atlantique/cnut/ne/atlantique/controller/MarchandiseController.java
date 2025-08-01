package atlantique.cnut.ne.atlantique.controller;

import atlantique.cnut.ne.atlantique.dto.MarchandiseDto;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.exceptions.StatusCode;
import atlantique.cnut.ne.atlantique.service.MarchandiseService;
import atlantique.cnut.ne.atlantique.util.UtilService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/marchandises")
public class MarchandiseController {

    private final MarchandiseService marchandiseService;
    private final UtilService utilService;

    public MarchandiseController(MarchandiseService marchandiseService, UtilService utilService) {
        this.marchandiseService = marchandiseService;
        this.utilService = utilService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR')")
    public ResponseEntity<Map<String, Object>> createMarchandise(
            @RequestPart("marchandise") @Valid MarchandiseDto marchandiseDto,
            @RequestPart(value = "blFile", required = false) MultipartFile blFile,
            @RequestPart(value = "declarationDouaneFile", required = false) MultipartFile declarationDouaneFile,
            @RequestPart(value = "factureCommercialeFile", required = false) MultipartFile factureCommercialeFile
    ) {
        try {
            MarchandiseDto newMarchandise = marchandiseService.createMarchandise(marchandiseDto, blFile, declarationDouaneFile, factureCommercialeFile);
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_MARCHANDISE_CREATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_MARCHANDISE_CREATED.getStatus_message(),
                            newMarchandise
                    ),
                    HttpStatus.CREATED
            );
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_BAD_REQUEST.getStatus_code(),
                            false,
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.BAD_REQUEST
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

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR')")
    public ResponseEntity<Map<String, Object>> updateMarchandise(
            @PathVariable String id,
            @RequestPart("marchandise") @Valid MarchandiseDto marchandiseDto,
            @RequestPart(value = "blFile", required = false) MultipartFile blFile,
            @RequestPart(value = "declarationDouaneFile", required = false) MultipartFile declarationDouaneFile,
            @RequestPart(value = "factureCommercialeFile", required = false) MultipartFile factureCommercialeFile
    ) {
        try {
            System.out.println("file name"+blFile.getOriginalFilename());
            MarchandiseDto updatedMarchandise = marchandiseService.updateMarchandise(id, marchandiseDto, blFile, declarationDouaneFile, factureCommercialeFile);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_MARCHANDISE_UPDATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_MARCHANDISE_UPDATED.getStatus_message(),
                            updatedMarchandise
                    )
            );
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_BAD_REQUEST.getStatus_code(),
                            false,
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.BAD_REQUEST
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

    @PostMapping("/{id}/submit-for-validation")
    @PreAuthorize("hasAnyAuthority('SCOPE_OPERATEUR', 'SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> submitMarchandiseForValidation(@PathVariable String id) {
        try {
            MarchandiseDto submittedMarchandise = marchandiseService.submitMarchandiseForValidation(id);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_OK.getStatus_code(),
                            true,
                            "Marchandise soumise pour validation avec succès.",
                            submittedMarchandise
                    )
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

    @PostMapping("/{id}/validate")
    @PreAuthorize("hasAnyAuthority('SCOPE_CAISSIER', 'SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> validateMarchandise(@PathVariable String id, @RequestBody Map<String, Boolean> requestBody) {
        Boolean isValid = requestBody.get("isValid");
        if (isValid == null) {
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_BAD_REQUEST.getStatus_code(),
                            false,
                            "Le paramètre 'isValid' est requis dans le corps de la requête.",
                            null
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        try {
            MarchandiseDto validatedMarchandise = marchandiseService.validateMarchandise(id, isValid);
            String message = isValid ? "Marchandise validée avec succès." : "Marchandise rejetée avec succès.";
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_OK.getStatus_code(),
                            true,
                            message,
                            validatedMarchandise
                    )
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
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN', 'SCOPE_CSITE', 'SCOPE_CAISSIER')")
    public ResponseEntity<Map<String, Object>> findAllMarchandisesPaginated(Pageable pageable) {
        Page<MarchandiseDto> marchandisePage = marchandiseService.findAllMarchandisesPaginated(pageable);
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_MARCHANDISE_RETRIEVED.getStatus_code(),
                        true,
                        StatusCode.HTTP_MARCHANDISE_RETRIEVED.getStatus_message(),
                        marchandisePage
                )
        );
    }

    @GetMapping("/non-pageable")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN', 'SCOPE_CSITE', 'SCOPE_CAISSIER')")
    public ResponseEntity<Map<String, Object>> getMarchandises() {
        List<MarchandiseDto> marchandises = marchandiseService.getMarchandises();
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_MARCHANDISE_RETRIEVED.getStatus_code(),
                        true,
                        StatusCode.HTTP_MARCHANDISE_RETRIEVED.getStatus_message(),
                        marchandises
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN', 'SCOPE_CSITE', 'SCOPE_CAISSIER')")
    public ResponseEntity<Map<String, Object>> getMarchandiseById(@PathVariable String id) {
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_MARCHANDISE_RETRIEVED.getStatus_code(),
                        true,
                        StatusCode.HTTP_MARCHANDISE_RETRIEVED.getStatus_message(),
                        marchandiseService.getMarchandiseById(id)
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')") // Only ADMIN can delete
    public ResponseEntity<Map<String, Object>> deleteMarchandise(@PathVariable String id) {
        try {
            marchandiseService.deleteMarchandise(id);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_MARCHANDISE_DELETED.getStatus_code(),
                            true,
                            StatusCode.HTTP_MARCHANDISE_DELETED.getStatus_message(),
                            null
                    )
            );
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_FORBIDDEN.getStatus_code(),
                            false,
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.FORBIDDEN
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
}