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
@Table(name = "marchandise")
public class Marchandise {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column
    private String caf;
    @Column
    private String poids;
    @Column
    private String type;
    @Column
    private String nombreColis;
    @Column
    private String numeroChassis;
    @Column
    private String numeroDouane;
    @Column
    private String nombreConteneur;
    @Column
    private boolean regularisation;
    @Column
    private boolean exoneration;
    @Column
    private boolean conteneur;
    @Column
    private String typeConteneur;
    @Column
    private String volume;
    @Column
    private String observation;
    @Column
    private String numVoyage;
    @Column
    private String atb;
    @Column
    private String idUtilisateur;
    @Column
    private String encours;
    @Column
    private String rcnut1;
    @Column
    private String rcnut2;
    @Column
    private String totalQuitance;
    @Column
    private String abic;
    @Column
    private String tcps;
    @Column
    private String be;
    @Column
    private String visa;
    @Column
    private String validation;
    @Column
    private String idNatureMarchandise;
    @Column
    private String idArmateur;
    @Column
    private Date dateValidation;
    @CreationTimestamp
    private Date creationDate;
    @UpdateTimestamp
    private Date modificationDate;
    @Column
    private String idTransitaire;
    @Column
    private String idImportateur;
}
