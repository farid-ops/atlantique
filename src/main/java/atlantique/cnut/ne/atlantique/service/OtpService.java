package atlantique.cnut.ne.atlantique.service;

public interface OtpService {

    String generateOtp(String identifier);

    boolean validateOtp(String identifier, String otpCode);

    void sendOtp(String identifier, String otpCode);
}
