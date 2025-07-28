package atlantique.cnut.ne.atlantique.controller;

import atlantique.cnut.ne.atlantique.dto.BlDto;
import atlantique.cnut.ne.atlantique.entity.BL;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.exceptions.StatusCode;
import atlantique.cnut.ne.atlantique.service.BlService;
import atlantique.cnut.ne.atlantique.util.UtilService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bls")
public class BillOfLoadingController {

    private final BlService blService;
    private final UtilService utilService;

    private static final String BL_STORAGE_DIRECTORY = "./uploads/bl_documents/";


    public BillOfLoadingController(BlService blService, UtilService utilService) {
        this.blService = blService;
        this.utilService = utilService;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> uploadBl(@RequestParam("file") MultipartFile file,
                                                        @RequestParam("designation") String designation) {
        try {
            BL uploadedBl = blService.uploadBlDocument(file, designation);
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_BL_CREATED.getStatus_code(),
                            true,
                            "Fichier BL uploadé et BL créé avec succès.",
                            uploadedBl
                    ),
                    HttpStatus.CREATED
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
                            "Échec de l'upload du fichier BL: " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/download/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN', 'SCOPE_CSITE', 'SCOPE_CAISSIER')")
    public ResponseEntity<?> downloadBlDocument(@PathVariable String id) {
        return blService.findBlById(id)
                .map(bl -> {
                    Path filePath = Paths.get(bl.getPath());
                    try {
                        byte[] data = Files.readAllBytes(filePath);
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.parseMediaType(bl.getMimeType()));
                        headers.setContentDispositionFormData("attachment", bl.getFilename());
                        headers.setContentLength(data.length);
                        return new ResponseEntity<>(data, headers, HttpStatus.OK);
                    } catch (IOException e) {
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                })
                .orElseThrow(() -> new ResourceNotFoundException("Document BL non trouvé avec l'ID: " + id));
    }


    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> createBl(@RequestBody @Valid BlDto blDto) {
        try {
            BL newBl = blService.createBl(blDto);
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_BL_CREATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_BL_CREATED.getStatus_message(),
                            newBl
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
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN', 'SCOPE_CSITE', 'SCOPE_CAISSIER')")
    public ResponseEntity<Map<String, Object>> getAllBls(Pageable pageable) {
        Page<BL> blPage = blService.findAllBlsPaginated(pageable);
        return ResponseEntity.ok(
                utilService.response(
                        StatusCode.HTTP_BL_RETRIEVED.getStatus_code(),
                        true,
                        StatusCode.HTTP_BL_RETRIEVED.getStatus_message(),
                        blPage
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERATEUR', 'SCOPE_STATICIEN', 'SCOPE_CSITE', 'SCOPE_CAISSIER')")
    public ResponseEntity<Map<String, Object>> getBlById(@PathVariable String id) {
        return blService.findBlById(id)
                .map(bl -> ResponseEntity.ok(
                        utilService.response(
                                StatusCode.HTTP_BL_RETRIEVED.getStatus_code(),
                                true,
                                StatusCode.HTTP_BL_RETRIEVED.getStatus_message(),
                                bl
                        )
                ))
                .orElseThrow(() -> new ResourceNotFoundException("BL non trouvé avec l'ID: " + id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Map<String, Object>> updateBl(@PathVariable String id, @RequestBody @Valid BlDto blDto) {
        try {
            BL updatedBl = blService.updateBl(id, blDto);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_BL_UPDATED.getStatus_code(),
                            true,
                            StatusCode.HTTP_BL_UPDATED.getStatus_message(),
                            updatedBl
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
    public ResponseEntity<Map<String, Object>> deleteBl(@PathVariable String id) {
        try {
            blService.deleteBl(id);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_BL_DELETED.getStatus_code(),
                            true,
                            StatusCode.HTTP_BL_DELETED.getStatus_message(),
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