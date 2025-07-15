package atlantique.cnut.ne.atlantique.controller;

import atlantique.cnut.ne.atlantique.dto.Oauth2DTO;
import atlantique.cnut.ne.atlantique.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/oauth2")
public class OAUthController {

    private final AuthService authService;

    public OAUthController(AuthService authService) {

        this.authService = authService;
    }

    @PostMapping( "/token")
    public ResponseEntity<?> jwtToken(@RequestBody @Valid Oauth2DTO oauth2DTO){
        return ResponseEntity.ok(this.authService.genToken(oauth2DTO));
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logOut(Authentication authentication,
                                    @RequestHeader(name = "Authorization") String authorizationHeader){
        String accessToken = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7);
        }
        return ResponseEntity.ok(this.authService.logOut(authentication.getName(), accessToken));
    }

}