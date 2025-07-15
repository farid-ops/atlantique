package atlantique.cnut.ne.atlantique.service;


public interface TokenBlacklistService {

    void blacklist(String token);

    boolean isTokenBlacklisted(String token);
}
