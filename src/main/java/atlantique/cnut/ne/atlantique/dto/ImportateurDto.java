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
public class ImportateurDto {
    @NotBlank(message = "Le nom ne peut pas être vide.")
    private String nom;
    @NotBlank(message = "Le prénom ne peut pas être vide.")
    private String prenom;
    @NotBlank(message = "Le numéro de téléphone ne peut pas être vide.")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Le format du numéro de téléphone est invalide.")
    private String phone;
    @NotBlank(message = "Le NIF ne peut pas être vide.")
    private String nif;
    private String idGroupe;
}
