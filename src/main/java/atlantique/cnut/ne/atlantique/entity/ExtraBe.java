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
@Table(name = "extra_be")
public class ExtraBe {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column
    private String idUtilisateur;
    @Column
    private String idBonExpedition;
    @Column
    private String montant;
    @Column
    private String idSite;
    @CreationTimestamp
    private Date creationDate;
    @UpdateTimestamp
    private Date modificationDate;
}
