package atlantique.cnut.ne.atlantique.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MarchandiseItemDto {
    private String poids;
    private String nombreColis;
    private String numeroBl;
}