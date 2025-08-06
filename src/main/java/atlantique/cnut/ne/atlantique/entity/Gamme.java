package atlantique.cnut.ne.atlantique.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "gamme")
public class Gamme {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column
    private String designation;
    @CreationTimestamp
    private Date creationDate;
    @UpdateTimestamp
    private Date modificationDate;
    @OneToMany(mappedBy = "gamme", fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<Marque> marques;
}
