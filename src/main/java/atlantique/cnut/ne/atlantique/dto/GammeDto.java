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
public class GammeDto {
    @NotBlank(message = "La désignation ne peut pas être vide.")
    private String designation;
    @NotBlank(message = "L'ID du pays ne peut pas être vide.")
    private String idPays;
}
