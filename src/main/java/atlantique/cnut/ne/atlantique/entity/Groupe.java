package atlantique.cnut.ne.atlantique.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "groupes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Groupe {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String denomination;

    @Column
    private String telephone;

    @Column
    private String email;

    @Column
    private String siteWeb;

    @Column
    private String nif;

    @Column
    private String bp;

    @Column(nullable = false)
    private String adresse;

    @Column(nullable = false)
    private Double prixBeStandard;
    @Column(nullable = false)
    private Double visaVehiculeMoins5000kg;
    @Column(nullable = false)
    private Double visaVehiculePlus5000kg;

    @Column
    private String signatureImage;
    @Column
    private String logo;

    @Column
    private Double coutBSC;
    @Column
    private Double tonnage;
    @Column
    private Double valeurConteneur10Pieds;
    @Column
    private Double valeurConteneur20Pieds;
    @Column
    private Double valeurConteneur30Pieds;

    @CreationTimestamp
    private Date creationDate;
    @UpdateTimestamp
    private Date modificationDate;

}