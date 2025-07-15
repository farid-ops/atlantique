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
public class PortDto {
    @NotBlank(message = "L'ID du pays ne peut pas être vide.")
    private String idPays;
    @NotBlank(message = "L'ID du site ne peut pas être vide.")
    private String idSite;
    @NotBlank(message = "La désignation ne peut pas être vide.")
    private String designation;
}
