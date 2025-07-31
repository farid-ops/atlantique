package atlantique.cnut.ne.atlantique.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
public class MultipartConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();

        factory.setMaxFileSize(DataSize.ofMegabytes(5));

        factory.setMaxRequestSize(DataSize.ofMegabytes(6));

        factory.setMaxRequestSize(DataSize.parse("6MB"));
        factory.setMaxFileSize(DataSize.parse("5MB"));

//        factory.setMaxParts(100000); // Set a very high limit for number of parts

        return factory.createMultipartConfig();
    }
}