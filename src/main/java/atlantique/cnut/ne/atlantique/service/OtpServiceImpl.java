package atlantique.cnut.ne.atlantique.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.time.Duration;

@Service
public class OtpServiceImpl implements OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpServiceImpl.class);
    private static final String OTP_PREFIX = "otp:";
    private static final int OTP_LENGTH = 6;
    private static final Duration OTP_EXPIRATION = Duration.ofMinutes(5);

    private final StringRedisTemplate redisTemplate;
    private final SecureRandom secureRandom = new SecureRandom();

    public OtpServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String generateOtp(String identifier) {
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(secureRandom.nextInt(10));
        }
        String generatedOtp = otp.toString();

        redisTemplate.opsForValue().set(OTP_PREFIX + identifier, generatedOtp, OTP_EXPIRATION);
        logger.info("OTP généré pour {}: {} (expirera dans {}s)", identifier, generatedOtp, OTP_EXPIRATION.getSeconds());
        return generatedOtp;
    }

    @Override
    public boolean validateOtp(String identifier, String otpCode) {
        String storedOtp = redisTemplate.opsForValue().get(OTP_PREFIX + identifier);

        redisTemplate.delete(OTP_PREFIX + identifier);

        if (storedOtp != null && storedOtp.equals(otpCode)) {
            logger.info("OTP valide pour {}", identifier);
            return true;
        } else {
            logger.warn("OTP invalide ou expiré pour {}. Fourni: {}, Attendu: {}", identifier, otpCode, storedOtp);
            return false;
        }
    }

    @Override
    public void sendOtp(String identifier, String otpCode) {
        logger.info("Simulating sending OTP {} to {}", otpCode, identifier);
    }
}