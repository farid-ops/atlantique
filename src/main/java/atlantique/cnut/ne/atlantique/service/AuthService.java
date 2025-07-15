package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.Oauth2DTO;

import java.util.Map;

public interface AuthService {

    Map<String, Object> genToken(Oauth2DTO oauth2DTO);

    Map<String, Object> logOut(String msisdn);
}
