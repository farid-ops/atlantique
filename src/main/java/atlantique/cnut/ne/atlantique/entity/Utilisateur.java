package atlantique.cnut.ne.atlantique.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "utilisateur")
public class Utilisateur {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column
    private String nom;
    @Column
    private String prenom;
    @Column
    private String adresse;
    @Column
    private String email;
    @Column
    private String telephone;
    @Column
    private String password;
    @Column
    private String idSite;
    @Column
    private String idGroupe;
    @Column
    private String idPays;
    @Column
    private double cashBalance;
    @CreationTimestamp
    private Date dateCreation;
    @UpdateTimestamp
    private Date dateModification;
    @Column
    private boolean isAccountNonExpired;
    @Column
    private boolean isAccountNonLocked;
    @Column
    private boolean isCredentialsNonExpired;
    @Column
    private boolean isEnabled;

    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(
            name = "utilisateur_authority",
            joinColumns = @JoinColumn(name = "utilisateur_id"),
            inverseJoinColumns = @JoinColumn(name = "authorite_id")
    )
    private Set<Autorite> authorites = new HashSet<>();
}