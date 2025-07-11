package atlantique.cnut.ne.atlantique.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CargaisonDto {
    private String idMarchandise;
    private String idBl;
    private String manifeste;
    private String idSite;
    private String idConsignataire;
    private String transporteur;
    private String lieuEmission;
    private String DateEmission;
    private String idNavire;
    private String dateDepartureNavire;
    private String dateArriveNavire;
    private String idPortEmbarquement;
    private String idPortDebarquement;
}
