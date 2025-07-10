package atlantique.cnut.ne.atlantique.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "importateur")
public class Importateur {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column
    private String nom;
    @Column
    private String prenom;
    @Column
    private String phone;
    @Column
    private String nif;
    @CreationTimestamp
    private Date creationDate;
    @UpdateTimestamp
    private Date modificationDate;

    //les relations
    @OneToMany
    private Set<Marchandise> marchandises;
}
