package atlantique.cnut.ne.atlantique.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NatureMarchandiseDto {
    private String designation;
    private String typeMarchandise;
    private boolean active;
}
