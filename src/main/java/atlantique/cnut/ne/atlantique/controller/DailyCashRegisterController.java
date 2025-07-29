package atlantique.cnut.ne.atlantique.controller;

import atlantique.cnut.ne.atlantique.dto.DailyCashRegisterDto;
import atlantique.cnut.ne.atlantique.entity.Utilisateur;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.exceptions.StatusCode;
import atlantique.cnut.ne.atlantique.service.DailyCashRegisterService;
import atlantique.cnut.ne.atlantique.service.UtilisateurService;
import atlantique.cnut.ne.atlantique.util.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cash-register")
@RequiredArgsConstructor
@Slf4j
public class DailyCashRegisterController {

    private final DailyCashRegisterService dailyCashRegisterService;
    private final UtilisateurService utilisateurService;
    private final UtilService utilService;

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("Aucun utilisateur authentifié.");
        }
        String username = authentication.getName();
        return utilisateurService.findUtilisateurById(username)
                .map(Utilisateur::getId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur authentifié non trouvé."));
    }

    private String getCurrentUserSiteId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("Aucun utilisateur authentifié.");
        }
        String username = authentication.getName();
        return utilisateurService.findUtilisateurById(username)
                .map(Utilisateur::getIdSite)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur authentifié non trouvé ou n'a pas d'ID de site."));
    }


    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('SCOPE_CAISSIER')")
    public ResponseEntity<Map<String, Object>> getMyDailySummaryToday() {
        try {
            String caissierId = getCurrentUserId();
            DailyCashRegisterDto summary = dailyCashRegisterService.getDailySummary(caissierId, LocalDate.now());
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_OK.getStatus_code(),
                            true,
                            "Résumé de caisse journalier récupéré avec succès.",
                            summary
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

    @GetMapping("/summary-range")
    @PreAuthorize("hasAuthority('SCOPE_CAISSIER')")
    public ResponseEntity<Map<String, Object>> getMyDailySummaryRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            String caissierId = getCurrentUserId();
            List<DailyCashRegisterDto> summaries = dailyCashRegisterService.getDailySummariesForCaissier(caissierId, startDate, endDate);
            if (summaries.isEmpty()) {
                return new ResponseEntity<>(
                        utilService.response(
                                StatusCode.HTTP_NOT_FOUND.getStatus_code(),
                                false,
                                "Aucun résumé de caisse trouvé pour votre compte entre le " + startDate + " et le " + endDate + ".",
                                null
                        ),
                        HttpStatus.NOT_FOUND
                );
            }
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_OK.getStatus_code(),
                            true,
                            "Résumés de caisse pour votre compte entre le " + startDate + " et le " + endDate + " récupérés avec succès.",
                            summaries
                    )
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

    @GetMapping("/summary/{date}")
    @PreAuthorize("hasAuthority('SCOPE_CAISSIER')")
    public ResponseEntity<Map<String, Object>> getMyDailySummaryByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            String caissierId = getCurrentUserId();
            DailyCashRegisterDto summary = dailyCashRegisterService.getDailySummary(caissierId, date);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_OK.getStatus_code(),
                            true,
                            "Résumé de caisse journalier récupéré avec succès pour la date spécifiée.",
                            summary
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

    @PostMapping("/deposit")
    @PreAuthorize("hasAuthority('SCOPE_CAISSIER')")
    public ResponseEntity<Map<String, Object>> recordDeposit(@RequestBody Map<String, Double> requestBody) {
        try {
            String caissierId = getCurrentUserId();
            Double amount = requestBody.get("amount");
            if (amount == null) {
                return new ResponseEntity<>(
                        utilService.response(StatusCode.HTTP_BAD_REQUEST.getStatus_code(), false, "Le montant du dépôt est requis.", null),
                        HttpStatus.BAD_REQUEST
                );
            }
            DailyCashRegisterDto updatedRegister = dailyCashRegisterService.recordDeposit(caissierId, amount);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_OK.getStatus_code(),
                            true,
                            "Dépôt enregistré avec succès.",
                            updatedRegister
                    )
            );
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    utilService.response(StatusCode.HTTP_CONFLICT.getStatus_code(), false, e.getMessage(), null),
                    HttpStatus.CONFLICT
            );
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    utilService.response(StatusCode.HTTP_NOT_FOUND.getStatus_code(), false, e.getMessage(), null),
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    utilService.response(StatusCode.HTTP_INTERNAL_SERVER_ERROR.getStatus_code(), false, StatusCode.HTTP_INTERNAL_SERVER_ERROR.getStatus_message() + ": " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PostMapping("/withdrawal")
    @PreAuthorize("hasAuthority('SCOPE_CAISSIER')")
    public ResponseEntity<Map<String, Object>> recordWithdrawal(@RequestBody Map<String, Double> requestBody) {
        try {
            String caissierId = getCurrentUserId();
            Double amount = requestBody.get("amount");
            if (amount == null) {
                return new ResponseEntity<>(
                        utilService.response(StatusCode.HTTP_BAD_REQUEST.getStatus_code(), false, "Le montant du retrait est requis.", null),
                        HttpStatus.BAD_REQUEST
                );
            }
            DailyCashRegisterDto updatedRegister = dailyCashRegisterService.recordWithdrawal(caissierId, amount);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_OK.getStatus_code(),
                            true,
                            "Retrait enregistré avec succès.",
                            updatedRegister
                    )
            );
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    utilService.response(StatusCode.HTTP_CONFLICT.getStatus_code(), false, e.getMessage(), null),
                    HttpStatus.CONFLICT
            );
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    utilService.response(StatusCode.HTTP_NOT_FOUND.getStatus_code(), false, e.getMessage(), null),
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    utilService.response(StatusCode.HTTP_INTERNAL_SERVER_ERROR.getStatus_code(), false, StatusCode.HTTP_INTERNAL_SERVER_ERROR.getStatus_message() + ": " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    @GetMapping("/site-summaries")
    @PreAuthorize("hasAuthority('SCOPE_CSITE')")
    public ResponseEntity<Map<String, Object>> getSiteCashiersSummariesByDate(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            String siteId = getCurrentUserSiteId();
            List<DailyCashRegisterDto> summaries = dailyCashRegisterService.getDailySummariesForSite(siteId, startDate, endDate);
            if (summaries.isEmpty()) {
                return new ResponseEntity<>(
                        utilService.response(
                                StatusCode.HTTP_NOT_FOUND.getStatus_code(),
                                false,
                                "Aucun résumé de caisse trouvé pour le site " + siteId + " entre le " + startDate + " et le " + endDate + ".",
                                null
                        ),
                        HttpStatus.NOT_FOUND
                );
            }
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_OK.getStatus_code(),
                            true,
                            "Résumés de caisse pour le site " + siteId + " entre le " + startDate + " et le " + endDate + " récupérés avec succès.",
                            summaries
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

    @PostMapping("/close")
    @PreAuthorize("hasAuthority('SCOPE_CAISSIER')")
    public ResponseEntity<Map<String, Object>> closeMyCashRegister() {
        try {
            String caissierId = getCurrentUserId();
            DailyCashRegisterDto closedRegister = dailyCashRegisterService.closeCashRegister(caissierId);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_OK.getStatus_code(),
                            true,
                            "Caisse clôturée avec succès pour la journée.",
                            closedRegister
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
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_NOT_FOUND.getStatus_code(),
                            false,
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.NOT_FOUND
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

    @PostMapping("/open")
    @PreAuthorize("hasAuthority('SCOPE_CAISSIER')")
    public ResponseEntity<Map<String, Object>> openMyCashRegister() {
        try {
            String caissierId = getCurrentUserId();
            DailyCashRegisterDto openedRegister = dailyCashRegisterService.openCashRegister(caissierId);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_OK.getStatus_code(),
                            true,
                            "Caisse ouverte avec succès pour la journée.",
                            openedRegister
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