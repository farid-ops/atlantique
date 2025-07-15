package atlantique.cnut.ne.atlantique.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CargaisonDto {
    @NotBlank(message = "L'ID de la marchandise ne peut pas être vide.")
    private String idMarchandise;
    @NotBlank(message = "L'ID du BL ne peut pas être vide.")
    private String idBl;
    @NotBlank(message = "Le manifeste ne peut pas être vide.")
    private String manifeste;
    @NotBlank(message = "L'ID du site ne peut pas être vide.")
    private String idSite;
    @NotBlank(message = "L'ID du consignataire ne peut pas être vide.")
    private String idConsignataire;
    @NotBlank(message = "Le transporteur ne peut pas être vide.")
    private String transporteur;
    @NotBlank(message = "Le lieu d'émission ne peut pas être vide.")
    private String lieuEmission;
    @NotBlank(message = "La date d'émission ne peut pas être vide.")
    private String DateEmission;
    @NotBlank(message = "L'ID du navire ne peut pas être vide.")
    private String idNavire;
    @NotBlank(message = "La date de départ du navire ne peut pas être vide.")
    private String dateDepartureNavire;
    @NotBlank(message = "La date d'arrivée du navire ne peut pas être vide.")
    private String dateArriveNavire;
    @NotBlank(message = "L'ID du port d'embarquement ne peut pas être vide.")
    private String idPortEmbarquement;
    @NotBlank(message = "L'ID du port de débarquement ne peut pas être vide.")
    private String idPortDebarquement;
}
