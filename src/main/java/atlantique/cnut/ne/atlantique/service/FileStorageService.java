package atlantique.cnut.ne.atlantique.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileStorageService {
    String save(MultipartFile file);
    Resource load(String filename);
    Path loadPath(String filename);
    void delete(String filename);
    void deleteByPath(String filePath);
    Stream<Path> loadAll();
}