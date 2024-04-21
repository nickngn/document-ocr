package com.nickngn.demotikaocr.config;

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
        return mapper.readValue(resource.getInputStream(), BoundingConfig.class);
    }
}
