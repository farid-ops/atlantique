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
public class BlDto {
    @NotBlank(message = "La désignation ne peut pas être vide.")
    private String designation;
    @NotBlank(message = "Le nom du fichier ne peut pas être vide.")
    private String filename;
    @NotBlank(message = "Le type MIME ne peut pas être vide.")
    private String mimetype;
    @NotBlank(message = "Le chemin ne peut pas être vide.")
    private String path;
}
