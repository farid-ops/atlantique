package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.entity.Utilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {

    private final Utilisateur utilisateur;

    public UserDetailsImpl(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities;

        authorities=this.utilisateur.getAuthorites().stream().map(
                authority -> new SimpleGrantedAuthority(
                        authority.getNom().toUpperCase()
                )
        ).collect(Collectors.toList());

        return authorities;
    }

    @Override
    public String getPassword() {
        return this.utilisateur.getPassword();
    }

    @Override
    public String getUsername() {
        return this.utilisateur.getTelephone();
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.utilisateur.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.utilisateur.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.utilisateur.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return this.utilisateur.isEnabled();
    }
}
