package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.entity.Utilisateur;
import atlantique.cnut.ne.atlantique.repository.UtilisateurRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public UserDetailsServiceImpl(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String msisdn) throws UsernameNotFoundException {

        Optional<Utilisateur> account = this.utilisateurRepository.findByTelephone(msisdn);

        if (account.isEmpty()) throw new UsernameNotFoundException("Utilisateur non trouvé: ".concat(msisdn));

        return new UserDetailsImpl(account.get());
    }
}
