package atlantique.cnut.ne.atlantique.config;

import atlantique.cnut.ne.atlantique.entity.Autorite;
import atlantique.cnut.ne.atlantique.entity.Groupe;
import atlantique.cnut.ne.atlantique.entity.Utilisateur;
import atlantique.cnut.ne.atlantique.repository.AutoriteRepository;
import atlantique.cnut.ne.atlantique.repository.GroupeRepository;
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
    private final GroupeRepository groupeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(AutoriteRepository autoriteRepository, UtilisateurRepository utilisateurRepository,
                      GroupeRepository groupeRepository, PasswordEncoder passwordEncoder) {
        this.autoriteRepository = autoriteRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.groupeRepository = groupeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        String[] roles = {"USER", "ADMIN", "OPERATEUR", "CAISSIER", "CSITE", "STATICIEN", "ADMINISTRATEUR_GROUPE"};
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

        Groupe defaultGroupe = groupeRepository.findByDenomination("GROUPE_PAR_DEFAUT")
                .orElseGet(() -> {
                    Groupe newDefaultGroupe = new Groupe();
                    newDefaultGroupe.setDenomination("GROUPE_PAR_DEFAUT");
                    newDefaultGroupe.setAdresse("Adresse par défaut");
                    newDefaultGroupe.setEmail("default@groupe.com");
                    newDefaultGroupe.setTelephone("0000000000");
                    newDefaultGroupe.setNif("NIF_DEFAUT");
                    newDefaultGroupe.setBp("BP_DEFAUT");
                    newDefaultGroupe.setSiteWeb("www.defaultgroupe.com");
                    newDefaultGroupe.setPrixBeStandard(50000.0);
                    newDefaultGroupe.setVisaVehiculeMoins5000kg(15000.0);
                    newDefaultGroupe.setVisaVehiculePlus5000kg(20000.0);
                    logger.info("Groupe par défaut 'GROUPE_PAR_DEFAUT' créé.");
                    return groupeRepository.save(newDefaultGroupe);
                });

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
                newUser.setCashBalance(0);
                newUser.setIdGroupe(defaultGroupe.getId());
                Set<Autorite> userRoles = new HashSet<>();
                userRoles.add(role);
                newUser.setAuthorites(userRoles);

                newUser.setIdPays("53b2150c-4157-49ee-a05d-4ebbeace860b");
                newUser.setIdSite("4bc349f2-c024-4d9d-8dd8-28b3ad2c681d");

                newUser.setAccountNonExpired(true);
                newUser.setAccountNonLocked(true);
                newUser.setCredentialsNonExpired(true);
                newUser.setEnabled(true);

                utilisateurRepository.save(newUser);
                logger.info("Utilisateur '{}' créé et associé au groupe par défaut: {}", roleName, phone);
            } else {
                logger.info("Utilisateur '{}' existe déjà: {}", roleName, phone);
                phoneSuffix++;
            }
        }
    }
}