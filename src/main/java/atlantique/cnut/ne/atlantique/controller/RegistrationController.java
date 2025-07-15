package atlantique.cnut.ne.atlantique.controller;

import atlantique.cnut.ne.atlantique.dto.UtilisateurDto;
import atlantique.cnut.ne.atlantique.entity.Utilisateur;
import atlantique.cnut.ne.atlantique.exceptions.StatusCode;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.service.OtpService;
import atlantique.cnut.ne.atlantique.service.UtilisateurService;
import atlantique.cnut.ne.atlantique.util.UtilService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

record OtpRequest(@NotBlank(message = "L'identifiant (téléphone/email) ne peut pas être vide.") String identifier, String otpCode) {}


@RestController
@RequestMapping("/api/v1/account")
public class RegistrationController {

    private final UtilisateurService utilisateurService;
    private final UtilService utilService;
    private final OtpService otpService;

    public RegistrationController(UtilisateurService utilisateurService, UtilService utilService, OtpService otpService) {
        this.utilisateurService = utilisateurService;
        this.utilService = utilService;
        this.otpService = otpService;
    }

    @PostMapping("/new")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody @Valid UtilisateurDto utilisateurDto) {
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
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_INTERNAL_SERVER_ERROR.getStatus_code(),
                            false,
                            "Erreur de configuration du rôle utilisateur: " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
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


    @PostMapping("/otp/generate")
    public ResponseEntity<Map<String, Object>> generateOtp(@RequestBody @Valid OtpRequest otpRequest) {
        try {

            String otpCode = otpService.generateOtp(otpRequest.identifier());
            otpService.sendOtp(otpRequest.identifier(), otpCode);
            return ResponseEntity.ok(
                    utilService.response(
                            StatusCode.HTTP_OK.getStatus_code(),
                            true,
                            "OTP généré et envoyé avec succès.",
                            null
                    )
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_INTERNAL_SERVER_ERROR.getStatus_code(),
                            false,
                            "Échec de la génération/envoi de l'OTP: " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PostMapping("/otp/validate")
    public ResponseEntity<Map<String, Object>> validateOtp(@RequestBody @Valid OtpRequest otpRequest) {
        if (otpRequest.otpCode() == null || otpRequest.otpCode().isEmpty()) {
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_BAD_REQUEST.getStatus_code(),
                            false,
                            "Le code OTP ne peut pas être vide.",
                            null
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }
        try {
            boolean isValid = otpService.validateOtp(otpRequest.identifier(), otpRequest.otpCode());
            if (isValid) {
                return ResponseEntity.ok(
                        utilService.response(
                                StatusCode.HTTP_OK.getStatus_code(),
                                true,
                                "OTP validé avec succès.",
                                null
                        )
                );
            } else {
                return new ResponseEntity<>(
                        utilService.response(
                                StatusCode.HTTP_UNAUTHORIZED.getStatus_code(),
                                false,
                                "OTP invalide ou expiré.",
                                null
                        ),
                        HttpStatus.UNAUTHORIZED
                );
            }
        } catch (Exception e) {
            return new ResponseEntity<>(
                    utilService.response(
                            StatusCode.HTTP_INTERNAL_SERVER_ERROR.getStatus_code(),
                            false,
                            "Erreur lors de la validation de l'OTP: " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}