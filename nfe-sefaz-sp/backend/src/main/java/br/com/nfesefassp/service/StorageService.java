package br.com.nfesefassp.service;

import br.com.nfesefassp.controller.AuthController;
import br.com.nfesefassp.model.*;
import br.com.nfesefassp.repository.*;
import br.com.nfesefassp.security.*;
import br.com.nfesefassp.util.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StorageService {
    private final Path basePath;

    public StorageService(@Value("${app.storage-base-path}") String basePath) {
        this.basePath = Path.of(basePath);
    }

    public String saveText(String relativePath, String content) {
        try {
            Path target = basePath.resolve(relativePath).normalize();
            Files.createDirectories(target.getParent());
            Files.writeString(target, content, StandardCharsets.UTF_8);
            return target.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao armazenar arquivo fiscal.", e);
        }
    }

    public String saveBytes(String relativePath, byte[] content) {
        try {
            Path target = basePath.resolve(relativePath).normalize();
            Files.createDirectories(target.getParent());
            Files.write(target, content);
            return target.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao armazenar arquivo.", e);
        }
    }
}
