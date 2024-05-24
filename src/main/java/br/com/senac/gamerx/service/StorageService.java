package br.com.senac.gamerx.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class StorageService {
    //Sempre trocar o path pro computador local
    private final Path rootLocation = Paths.get("/Users/brianzavala/Projetos/GamerX-V2/src/main/resources/static/assets");

    public void store(MultipartFile file, String filename) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Falha ao armazenar arquivo vazio " + filename);
            }
            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Falha ao armazenar arquivo " + filename, e);
        }
    }
}
