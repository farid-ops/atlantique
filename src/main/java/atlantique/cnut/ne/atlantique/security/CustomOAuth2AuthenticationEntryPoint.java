package fujitora.amiral.accountservice.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.Date;


@SuppressWarnings("ALL")
public class CustomOAuth2AuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2AuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        String message="Authentication failed";
        int code=HttpStatus.UNAUTHORIZED.value();
        String date= new Date().toString();
        String error=authException.getMessage();

        String error_response = String.format("{\"code\": \"%d\",\"message\": \"%s\", \"error\": \"%s\", \"date\": \"%s\"}", code, message, error, date);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(error_response);
    }


}

