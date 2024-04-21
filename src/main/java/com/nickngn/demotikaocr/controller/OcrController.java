package com.nickngn.demotikaocr.controller;

import com.nickngn.demotikaocr.model.NRIC;
import com.nickngn.demotikaocr.service.OcrService;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;

@RequiredArgsConstructor
@RequestMapping("/ocr")
@RestController
public class OcrController {

    private final OcrService ocrService;

    @PostMapping("/nric-fin")
    public NRIC ocrNricFin(@RequestParam MultipartFile file) throws TesseractException, IOException {
        return ocrService.detectNric(ImageIO.read(file.getInputStream()));
    }
}
