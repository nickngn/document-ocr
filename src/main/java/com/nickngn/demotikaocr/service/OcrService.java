package com.nickngn.demotikaocr.service;

import com.nickngn.demotikaocr.config.BoundingConfig;
import com.recognition.software.jdeskew.ImageDeskew;
import com.recognition.software.jdeskew.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;
import net.sourceforge.tess4j.util.LoadLibs;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class OcrService {

    public static final String PNG = "png";

    static {
        File tmpFolder = LoadLibs.extractTessResources("win32-x86-64");
        System.setProperty("java.library.path", tmpFolder.getPath());
    }

    private final ITesseract tesseract;
    private final BoundingConfig boundingConfig;

    public Map<String, String> doOcr(BufferedImage bufferedImage, String docType) throws TesseractException {
        Map<String, String> result = new HashMap<>();

        bufferedImage = preprocess(bufferedImage);
        BoundingConfig.DocumentType docTypeConf = boundingConfig.getDocumentTypes().get(docType);
        docTypeConf = docTypeConf.calcScaledConfig(bufferedImage.getWidth(), bufferedImage.getHeight());

        for (var f : docTypeConf.getFields()) {
            String val = "";
            if (f.getType().equals(BoundingConfig.FieldType.TEXT)) {
                val = doOcr(bufferedImage, docTypeConf, f.getName());
            } else if (f.getType().equals(BoundingConfig.FieldType.IMAGE)) {
                BufferedImage image = bufferedImage.getSubimage(f.getX(), f.getY(), f.getWidth(), f.getHeight());
                val = encodeBase64(image, PNG);
            }
            result.put(f.getName(), val.trim());
        }
        return result;
    }

    private String doOcr(BufferedImage bufferedImage, BoundingConfig.DocumentType documentType, String name) throws TesseractException {
        return tesseract.doOCR(bufferedImage, "", List.of(documentType.roi(name)));
    }

    private BufferedImage preprocess(BufferedImage bufferedImage) {
        bufferedImage = ImageHelper.convertImageToGrayscale(bufferedImage);
        return deskewImage(bufferedImage);
    }

    public String encodeBase64(BufferedImage image, String type) {
        String imageString = null;

        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ) {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();
            Base64.Encoder encoder = Base64.getEncoder();
            imageString = encoder.encodeToString(imageBytes);
        } catch (IOException e) {
            log.error("Error occurred while encoding image", e);
        }
        return "data:image/" + type + ";base64," + imageString;
    }

    public static BufferedImage deskewImage(BufferedImage bImg) {
        double minimumDeskewThreshold = 0.05;
        ImageDeskew deskew = new ImageDeskew(bImg);
        double imageSkewAngle = deskew.getSkewAngle();

        if ((imageSkewAngle > minimumDeskewThreshold || imageSkewAngle < -(minimumDeskewThreshold))) {
            bImg = ImageUtil.rotate(bImg, -imageSkewAngle, bImg.getWidth() / 2, bImg.getHeight() / 2);
        }

        return bImg;
    }

}
