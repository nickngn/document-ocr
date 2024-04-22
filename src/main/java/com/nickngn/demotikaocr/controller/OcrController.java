package com.nickngn.demotikaocr.controller;

import com.nickngn.demotikaocr.service.OcrService;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/ocr")
@RestController
public class OcrController {

    private final OcrService ocrService;

    @PostMapping("/{docType}")
    public ResponseEntity<Map<String, String>> ocr(@RequestParam MultipartFile file,
                                                   @PathVariable("docType") String docType) throws TesseractException, IOException {
        @Cleanup
        InputStream inputStream = file.getInputStream();
        Map<String, String> result = ocrService.doOcr(ImageIO.read(inputStream), docType);
        return ResponseEntity.ok(result);
    }
}
