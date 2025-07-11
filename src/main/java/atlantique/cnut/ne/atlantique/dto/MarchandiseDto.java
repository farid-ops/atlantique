package atlantique.cnut.ne.atlantique.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MarchandiseDto {
    private String caf;
    private String poids;
    private String type;
    private String nombreColis;
    private String numeroChassis;
    private String numeroDouane;
    private String nombreConteneur;
    private boolean regularisation;
    private boolean conteneur;
    private boolean exoneration;
    private String typeConteneur;
    private String volume;
    private String observation;
    private String numVoyage;
    private String atb;
    private String idUtilisateur;
    private String encours;
    private String rcnut1;
    private String rcnut2;
    private String totalQuitance;
    private String abic;
    private String tcps;
    private String be;
    private String visa;
    private String validation;
    private String idNatureMarchandise;
    private String idArmature;
}
