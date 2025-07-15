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
public class ExtraBeDto {
    @NotBlank(message = "L'ID de l'utilisateur ne peut pas être vide.")
    private String idUtilisateur;
    @NotBlank(message = "Le montant ne peut pas être vide.")
    @Pattern(regexp = "\\d+(\\.\\d+)?", message = "Le montant doit être une valeur numérique.")
    private String montant;
    @NotBlank(message = "L'ID du site ne peut pas être vide.")
    private String idSite;
}
