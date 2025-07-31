// atlantique/src/main/java/atlantique/cnut/ne/atlantique/config/StorageConfig.java
package atlantique.cnut.ne.atlantique.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StorageConfig {

    @Value("${file.upload-dir}")
    private String uploadDir;


    @Bean
    public Path rootLocation() throws IOException {
        Path rootPath = Paths.get(uploadDir).normalize().toAbsolutePath();
        Files.createDirectories(rootPath);
        System.out.println("Root storage directory initialized: " + rootPath);
        return rootPath;
    }
}