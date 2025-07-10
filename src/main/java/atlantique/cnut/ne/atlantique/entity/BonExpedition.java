package atlantique.cnut.ne.atlantique.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "bon_expedition")
public class BonExpedition {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column
    private String nombreColis;
    @Column
    private String immatriculation;
    @Column
    private String nom;
    @Column
    private String prenom;
    @Column
    private String destinataire;
    @Column
    private String poids;
    @Column
    private String valeur;
    @Column
    private String idSite;
    @Column
    private String idOperateur;
    @Column
    private String observation;
    @Column
    private boolean valide;
    @CreationTimestamp
    private Date creationDate;
    @UpdateTimestamp
    private Date modificationDate;

    //les relations
    @OneToOne
    private ExtraBE extraBE;
}
