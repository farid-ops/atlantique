package atlantique.cnut.ne.atlantique.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;
import java.util.Date;


public class CustomOAuth2AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e)
            throws IOException {
        String message="Authentication failed";
        int code=HttpStatus.FORBIDDEN.value();
        String date= new Date().toString();
        String error=e.getMessage();

        String error_response = String.format("{\"code\": \"%d\",\"message\": \"%s\", \"error\": \"%s\", \"date\": \"%s\"}", code, message, error, date);

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(error_response);
    }
}
