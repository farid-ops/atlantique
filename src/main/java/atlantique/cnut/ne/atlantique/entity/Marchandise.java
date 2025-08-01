package atlantique.cnut.ne.atlantique.entity;

import atlantique.cnut.ne.atlantique.enums.MarchandiseStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "marchandises")
public class Marchandise {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String typeMarchandiseSelect;
    private String caf;
    private String poids;
    private String type;
    private String nombreColis;
    private String numeroChassis;
    private String numeroDouane;
    private String nombreConteneur;
    private boolean regularisation;
    private boolean exoneration;
    private String conteneur;
    private String typeConteneur;
    private String volume;
    private String observation;
    private String numVoyage;
    private String totalQuittance;
    private String be;
    private String visa;
    private String coutBsc;
    private String totalBePrice;

    @Enumerated(EnumType.STRING)
    private MarchandiseStatus status; //BROUILLON, SOUMIS_POUR_VALIDATION, VALIDE, REJETE
    private String submittedByUserId;
    private String validatedByUserId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date submissionDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date validationDate;

    private String idNatureMarchandise;
    private String idArmateur;
    private String idTransitaire;
    private String idImportateur;
    private String idUtilisateur;

    private String idBl;
    private String manifesteCargaison;
    private String idSiteCargaison;
    private String idConsignataireCargaison;
    private String transporteurCargaison;
    private String lieuEmissionCargaison;
    private String idNavireCargaison;
    private Date dateDepartureNavireCargaison;
    private Date dateArriveNavireCargaison;
    private String idPortEmbarquementCargaison;
    private String idPortDebarquementCargaison;

    @Column
    private String blFile;
    @Column
    private String declarationDouaneFile;
    @Column
    private String factureCommercialeFile;

    @CreationTimestamp
    private Date creationDate;
    @UpdateTimestamp
    private Date modificationDate;

    @ElementCollection
    @CollectionTable(name = "marchandise_groupage_items", joinColumns = @JoinColumn(name = "marchandise_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "poids", column = @Column(name = "item_poids")),
            @AttributeOverride(name = "nombreColis", column = @Column(name = "item_nombre_colis")),
            @AttributeOverride(name = "numeroBl", column = @Column(name = "item_numero_bl"))
    })
    private List<MarchandiseItem> marchandisesGroupage;
}