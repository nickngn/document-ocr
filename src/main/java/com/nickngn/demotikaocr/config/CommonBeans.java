package com.nickngn.demotikaocr.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class CommonBeans {

    private final ResourceLoader resourceLoader;

    @Value("${tesseract.tessdata-path}")
    private String path;

    @Bean
    public ITesseract tesseract() {
        ITesseract tesseract = new Tesseract1();
        tesseract.setLanguage("eng");
        tesseract.setOcrEngineMode(1);
        tesseract.setDatapath(path);
        return tesseract;
    }

    @Bean
    public BoundingConfig boundingConfig() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Resource resource = resourceLoader.getResource("classpath:bounding-config.json");
        Map<String, BoundingConfig.DocumentType> documentTypeMap = mapper.readValue(resource.getInputStream(), new TypeReference<Map<String, BoundingConfig.DocumentType>>() {
        });
        BoundingConfig boundingConfig = new BoundingConfig();
        boundingConfig.setDocumentTypes(documentTypeMap);
        return boundingConfig;
    }
}
