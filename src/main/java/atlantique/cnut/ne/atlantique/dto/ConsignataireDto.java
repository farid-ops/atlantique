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
public class ConsignataireDto {
    @NotBlank(message = "La lettre de manifeste ne peut pas être vide.")
    private String lettreManifeste;
    @NotBlank(message = "La désignation ne peut pas être vide.")
    private String designation;
}
