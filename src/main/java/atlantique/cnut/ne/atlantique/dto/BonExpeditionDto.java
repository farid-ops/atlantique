package atlantique.cnut.ne.atlantique.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class BonExpeditionDto {
    private String id;

    @NotBlank(message = "Le nombre de colis ne peut pas être vide.")
    private String nombreColis;

    @NotBlank(message = "L'immatriculation ne peut pas être vide.")
    private String immatriculation;

    @NotBlank(message = "Le nom du destinataire ne peut pas être vide.")
    private String nom;

    @NotBlank(message = "Le prénom du destinataire ne peut pas être vide.")
    private String prenom;

    @NotBlank(message = "Le destinataire ne peut pas être vide.")
    private String destinataire;

    @NotBlank(message = "Le poids ne peut pas être vide.")
    @Pattern(regexp = "\\d+(\\.\\d+)?", message = "Le poids doit être une valeur numérique.")
    private String poids;

    @NotBlank(message = "La valeur ne peut pas être vide.")
    @Pattern(regexp = "\\d+(\\.\\d+)?", message = "La valeur doit être une valeur numérique.")
    private String valeur;

    private String idSite;
    private String idUtilisateur;

    private String observation;

    private boolean valide;

    @NotBlank(message = "L'identifiant de la marchandise est obligatoire.")
    private String idMarchandise;
}