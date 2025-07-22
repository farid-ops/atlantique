package atlantique.cnut.ne.atlantique.config;

import atlantique.cnut.ne.atlantique.entity.Autorite;
import atlantique.cnut.ne.atlantique.entity.Utilisateur;
import atlantique.cnut.ne.atlantique.repository.AutoriteRepository;
import atlantique.cnut.ne.atlantique.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final AutoriteRepository autoriteRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(AutoriteRepository autoriteRepository, UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.autoriteRepository = autoriteRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        String[] roles = {"USER", "ADMIN", "OPERATEUR", "CAISSIER", "CSITE", "STATICIEN"};
        Set<Autorite> allAuthorities = new HashSet<>();

        for (String roleName : roles) {
            autoriteRepository.findByNom(roleName)
                    .ifPresentOrElse(
                            allAuthorities::add,
                            () -> {
                                Autorite newRole = new Autorite();
                                newRole.setNom(roleName);
                                Autorite savedRole = autoriteRepository.save(newRole);
                                allAuthorities.add(savedRole);
                                logger.info("Rôle créé: {}", roleName);
                            }
                    );
        }

        int phoneSuffix = 0;

        for (Autorite role : allAuthorities) {
            String roleName = role.getNom();
            String usernameBase = roleName.toLowerCase();
            String phone = "22799" + String.format("%07d", phoneSuffix++);
            String email = usernameBase + "@atlantique.cnut.ne";

            if (utilisateurRepository.findByEmail(email).isEmpty()) {
                Utilisateur newUser = new Utilisateur();
                newUser.setNom("John");
                newUser.setPrenom("Doe");
                newUser.setAdresse("Niamey");
                newUser.setEmail(email);
                newUser.setTelephone(phone);
                newUser.setPassword(passwordEncoder.encode("password"));

                Set<Autorite> userRoles = new HashSet<>();
                userRoles.add(role);
                newUser.setAuthorites(userRoles);

                newUser.setAccountNonExpired(true);
                newUser.setAccountNonLocked(true);
                newUser.setCredentialsNonExpired(true);
                newUser.setEnabled(true);

                utilisateurRepository.save(newUser);
                logger.info("Utilisateur '{}' créé: {}", roleName, phone);
            } else {
                logger.info("Utilisateur '{}' existe déjà: {}", roleName, phone);
                phoneSuffix++;
            }
        }
    }
}