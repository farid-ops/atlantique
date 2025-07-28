package atlantique.cnut.ne.atlantique.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UtilisateurDto {
    @NotBlank(message = "Le nom ne peut pas être vide.")
    private String nom;
    @NotBlank(message = "Le prénom ne peut pas être vide.")
    private String prenom;
    @NotBlank(message = "L'email ne peut pas être vide.")
    @Email(message = "Le format de l'email est invalide.")
    private String email;
    @NotBlank(message = "Le numéro de téléphone ne peut pas être vide.")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Le format du numéro de téléphone est invalide.")
    private String telephone;
    @NotBlank(message = "L'adresse ne peut pas être vide.")
    private String adresse;
    @NotBlank(message = "Le mot de passe ne peut pas être vide.")
    private String password;
    @NotBlank(message = "L'ID du site ne peut pas être vide.")
    private String idSite;
    @NotBlank(message = "L'ID du pays ne peut pas être vide.")
    private String idPays;
    private double cashBalance;
    private Set<String> autoriteIds;
    private Boolean enabled;
}
