package com.nickngn.ocr.controller;

import com.nickngn.ocr.service.OcrService;
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

    @PostMapping({"", "/", "/{docType}"})
    public ResponseEntity<Map<String, String>> ocr(@RequestParam MultipartFile file,
                                                   @PathVariable(value = "docType", required = false) String docType) throws TesseractException, IOException {
        @Cleanup
        InputStream inputStream = file.getInputStream();
        Map<String, String> result = ocrService.readText(ImageIO.read(inputStream), docType);
        return ResponseEntity.ok(result);
    }
}
