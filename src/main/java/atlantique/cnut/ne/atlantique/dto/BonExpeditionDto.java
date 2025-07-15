package atlantique.cnut.ne.atlantique.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BonExpeditionDto {
    @NotBlank(message = "Le nombre de colis ne peut pas être vide.")
    @Pattern(regexp = "\\d+", message = "Le nombre de colis doit être un nombre.")
    private String nombreColis;
    @NotBlank(message = "L'immatriculation ne peut pas être vide.")
    private String immatriculation;
    @NotBlank(message = "Le nom ne peut pas être vide.")
    private String nom;
    @NotBlank(message = "Le prénom ne peut pas être vide.")
    private String prenom;
    @NotBlank(message = "Le destinataire ne peut pas être vide.")
    private String destinataire;
    @NotBlank(message = "Le poids ne peut pas être vide.")
    @Pattern(regexp = "\\d+(\\.\\d+)?", message = "Le poids doit être une valeur numérique.")
    private String poids;
    @NotBlank(message = "La valeur ne peut pas être vide.")
    @Pattern(regexp = "\\d+(\\.\\d+)?", message = "La valeur doit être une valeur numérique.")
    private String valeur;
    @NotBlank(message = "L'ID du site ne peut pas être vide.")
    private String idSite;
    @NotBlank(message = "L'ID de l'utilisateur ne peut pas être vide.")
    private String idUtilisateur;
    @NotBlank(message = "L'observation ne peut pas être vide.")
    private String observation;
    private boolean valide;
}
