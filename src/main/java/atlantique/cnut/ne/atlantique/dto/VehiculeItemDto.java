package atlantique.cnut.ne.atlantique.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehiculeItemDto {
    private String poids;
    private String caf;
    private String numeroChassis;
    private String visa;
    private String numeroDouane;
    private String coutBsc;
    private String total;
}