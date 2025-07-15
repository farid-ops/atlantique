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
public class MarchandiseDto {
    @NotBlank(message = "Le CAF ne peut pas être vide.")
    private String caf;
    @NotBlank(message = "Le poids ne peut pas être vide.")
    @Pattern(regexp = "\\d+(\\.\\d+)?", message = "Le poids doit être une valeur numérique.")
    private String poids;
    @NotBlank(message = "Le type ne peut pas être vide.")
    private String type;
    @NotBlank(message = "Le nombre de colis ne peut pas être vide.")
    @Pattern(regexp = "\\d+", message = "Le nombre de colis doit être un nombre.")
    private String nombreColis;
    @NotBlank(message = "Le numéro de châssis ne peut pas être vide.")
    private String numeroChassis;
    @NotBlank(message = "Le numéro de douane ne peut pas être vide.")
    private String numeroDouane;
    @NotBlank(message = "Le nombre de conteneurs ne peut pas être vide.")
    @Pattern(regexp = "\\d+", message = "Le nombre de conteneurs doit être un nombre.")
    private String nombreConteneur;
    private boolean regularisation;
    private boolean conteneur;
    private boolean exoneration;
    private String typeConteneur;
    @NotBlank(message = "Le volume ne peut pas être vide.")
    private String volume;
    private String observation;
    @NotBlank(message = "Le numéro de voyage ne peut pas être vide.")
    private String numVoyage;
    @NotBlank(message = "L'ATB ne peut pas être vide.")
    private String atb;
    @NotBlank(message = "L'ID de l'utilisateur ne peut pas être vide.")
    private String idUtilisateur;
    @NotBlank(message = "L'encours ne peut pas être vide.")
    private String encours;
    private String rcnut1;
    private String rcnut2;
    @NotBlank(message = "Le total de la quittance ne peut pas être vide.")
    private String totalQuitance;
    private String abic;
    private String tcps;
    private String be;
    private String visa;
    private String validation;
    @NotBlank(message = "L'ID de la nature de la marchandise ne peut pas être vide.")
    private String idNatureMarchandise;
    @NotBlank(message = "L'ID de l'armateur ne peut pas être vide.")
    private String idArmature;
}
