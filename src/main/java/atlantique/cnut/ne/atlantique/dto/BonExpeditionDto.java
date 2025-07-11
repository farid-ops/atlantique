package atlantique.cnut.ne.atlantique.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BonExpeditionDto {
    private String nombreColis;
    private String immatriculation;
    private String nom;
    private String prenom;
    private String destinataire;
    private String poids;
    private String valeur;
    private String idSite;
    private String idUtilisateur;
    private String observation;
    private boolean valide;
}
