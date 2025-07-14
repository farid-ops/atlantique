package atlantique.cnut.ne.atlantique.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Oauth2DTO {

    private String msisdn;
    private String password;
    private String grantType;//password|refreshToken

    private boolean withRefreshToken;

    private String refreshToken;
}
