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
public class SiteDto {
    @NotBlank(message = "L'ID du pays ne peut pas être vide.")
    private String idPays;
    @NotBlank(message = "La désignation ne peut pas être vide.")
    private String designation;
}
