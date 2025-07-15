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
public class AutoriteDto {
    @NotBlank(message = "Le nom ne peut pas être vide.")
    private String nom;
}
