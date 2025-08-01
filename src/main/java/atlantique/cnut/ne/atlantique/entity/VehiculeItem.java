package atlantique.cnut.ne.atlantique.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehiculeItem {
    @Column(name = "vehicule_poids")
    private String poids;
    @Column(name = "vehicule_caf")
    private String caf;
    @Column(name = "vehicule_numero_chassis")
    private String numeroChassis;
    @Column(name = "vehicule_visa")
    private String visa;
    @Column(name = "vehicule_numero_douane")
    private String numeroDouane;
    @Column(name = "vehicule_cout_bsc")
    private String coutBsc;
    @Column(name = "vehicule_total")
    private String total;
}