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
@Table(name = "cargaison")
public class Cargaison {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column
    private String idBl;
    @Column
    private String manifeste;
    @Column
    private String idSite;
    @Column
    private String idConsignataire;
    @Column
    private String transporteur;
    @Column
    private String lieuEmission;
    @Column
    private Date emission;
    @Column
    private String idNavire;
    @Column
    private Date dateDepartureNavire;
    @Column
    private Date dateArriveNavire;
    @Column
    private String idPortEmbarquement;
    @Column
    private String idPortDemarquement;
    @CreationTimestamp
    private Date creationDate;
    @UpdateTimestamp
    private Date modificationDate;
}