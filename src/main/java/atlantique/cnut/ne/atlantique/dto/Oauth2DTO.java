package atlantique.cnut.ne.atlantique.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Oauth2DTO {

    @NotBlank(message = "Le MSISDN ne peut pas être vide.")
    private String msisdn;
    @NotBlank(message = "Le mot de passe ne peut pas être vide.")
    private String password;
    @NotBlank(message = "Le type d'autorisation (grantType) ne peut pas être vide.")
    private String grantType;
    //password|refreshToken

    private boolean withRefreshToken;

    private String refreshToken;
}
