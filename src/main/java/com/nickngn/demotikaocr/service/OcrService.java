package com.nickngn.demotikaocr.service;

import com.nickngn.demotikaocr.config.BoundingConfig;
import com.nickngn.demotikaocr.model.NRIC;
import com.recognition.software.jdeskew.ImageDeskew;
import com.recognition.software.jdeskew.ImageUtil;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;
import net.sourceforge.tess4j.util.LoadLibs;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Service
public class OcrService {

    static {
        File tmpFolder = LoadLibs.extractTessResources("win32-x86-64");
        System.setProperty("java.library.path", tmpFolder.getPath());
    }

    private final ITesseract tesseract;
    private final BoundingConfig boundingConfig;

    public NRIC detectFin(BufferedImage bufferedImage) throws TesseractException {
        bufferedImage = preprocess(bufferedImage);
        BoundingConfig.DocumentType config = boundingConfig.getFin();
        config = config.calcScaledConfig(bufferedImage.getWidth(), bufferedImage.getHeight());

        String id = tesseract.doOCR(bufferedImage, "", List.of(config.roi("id")));
        BoundingConfig.Field imageField = config.getField("image");
        BufferedImage image = bufferedImage.getSubimage(imageField.getX(), imageField.getY(), imageField.getWidth(), imageField.getHeight());
        String encodedImage = encodeToString(image, "png");
        String name = tesseract.doOCR(bufferedImage, "", List.of(config.roi("name")));
        String race = tesseract.doOCR(bufferedImage, "", List.of(config.roi("race")));
        String dob = tesseract.doOCR(bufferedImage, "", List.of(config.roi("dob")));
        String sex = tesseract.doOCR(bufferedImage, "", List.of(config.roi("sex")));
        String cob = tesseract.doOCR(bufferedImage, "", List.of(config.roi("cob")));

        sex = sex.trim();
        sex = sex.substring(sex.length() - 2);

        return new  NRIC(id.trim(), encodedImage, name.trim(), race.trim(), dob.trim(), sex.trim(), cob.trim());
    }

    public NRIC detectNric(BufferedImage bufferedImage) throws TesseractException {
        bufferedImage = preprocess(bufferedImage);
        BoundingConfig.DocumentType config = boundingConfig.getNric();
        config = config.calcScaledConfig(bufferedImage.getWidth(), bufferedImage.getHeight());

        String id = tesseract.doOCR(bufferedImage, "", List.of(config.roi("id")));
        BoundingConfig.Field imageField = config.getField("image");
        BufferedImage image = bufferedImage.getSubimage(imageField.getX(), imageField.getY(), imageField.getWidth(), imageField.getHeight());
        String encodedImage = encodeToString(image, "png");
        String name = tesseract.doOCR(bufferedImage, "", List.of(config.roi("name")));
        String race = tesseract.doOCR(bufferedImage, "", List.of(config.roi("race")));
        String dob = tesseract.doOCR(bufferedImage, "", List.of(config.roi("dob")));
        String sex = tesseract.doOCR(bufferedImage, "", List.of(config.roi("sex")));
        String cob = tesseract.doOCR(bufferedImage, "", List.of(config.roi("cob")));

        sex = sex.trim();
        sex = sex.substring(sex.length() - 2);

        return new  NRIC(id.trim(), encodedImage, name.trim(), race.trim(), dob.trim(), sex.trim(), cob.trim());
    }

    private BufferedImage preprocess(BufferedImage bufferedImage) {
        bufferedImage = ImageHelper.convertImageToGrayscale(bufferedImage);
        return deskewImage(bufferedImage);
    }

    public String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            Base64.Encoder encoder = Base64.getEncoder();
            imageString = encoder.encodeToString(imageBytes);

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
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
