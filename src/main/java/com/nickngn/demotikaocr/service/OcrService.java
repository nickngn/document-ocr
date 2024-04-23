package com.nickngn.demotikaocr.service;

import com.nickngn.demotikaocr.config.BoundingConfig;
import com.nickngn.demotikaocr.config.BoundingConfig.FieldType;
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

    /**
     * Reads text from a given BufferedImage using Optical Character Recognition (OCR) technology.
     *
     * @param bufferedImage The BufferedImage containing the image from which text needs to be extracted.
     * @param docType       The type of document to be read. Pass null to read all text from the image.
     * @return A Map of String pairs where the key represents the location of the extracted text and the value represents the extracted text.
     * @throws TesseractException If an error occurs during the OCR process.
     */
    public Map<String, String> readText(BufferedImage bufferedImage, String docType) throws TesseractException {
        if (bufferedImage == null) {
            return Map.of();
        }

        bufferedImage = preprocess(bufferedImage);

        if (docType == null) {
            return readAllText(bufferedImage);
        }

        return readDocument(bufferedImage, docType);
    }

    private BufferedImage preprocess(BufferedImage bufferedImage) {
        bufferedImage = ImageHelper.convertImageToGrayscale(bufferedImage);
        return deskewImage(bufferedImage);
    }

    /**
     * Reads the contents of a document specified by the provided BufferedImage and predefined document.
     *
     * @param bufferedImage The BufferedImage representing the document.
     * @param docType       The name of the preconfigured document.
     * @return A Map containing the field names and their corresponding values extracted from the document.
     * @throws TesseractException If an error occurs during document processing.
     */
    private Map<String, String> readDocument(BufferedImage bufferedImage, String docType) throws TesseractException {
        BoundingConfig.Document docTypeConf = boundingConfig.getDocuments().get(docType);
        docTypeConf = docTypeConf.calcScaledConfig(bufferedImage.getWidth(), bufferedImage.getHeight());

        Map<String, String> result = new HashMap<>();
        for (var field : docTypeConf.getFields()) {
            String val = "";
            if (field.getType().equals(FieldType.TEXT)) {
                val = readFieldValue(bufferedImage, docTypeConf, field.getName());
            } else if (field.getType().equals(FieldType.IMAGE)) {
                BufferedImage image = bufferedImage.getSubimage(field.getX(), field.getY(), field.getWidth(), field.getHeight());
                val = encodeBase64(image);
            }
            result.put(field.getName(), val.trim());
        }
        return result;
    }

    /**
     * Reads all the text from a BufferedImage using the Tesseract OCR (Optical Character Recognition) engine.
     *
     * @param bufferedImage the BufferedImage containing the text to be read
     * @return a Map with the extracted text as the value associated with the key 'text'
     * @throws TesseractException if an error occurs during the OCR process
     */
    private Map<String, String> readAllText(BufferedImage bufferedImage) throws TesseractException {
        String val = tesseract.doOCR(bufferedImage);
        return Map.of("text", val);
    }

    /**
     * Reads the field value from the provided buffered image using Tesseract OCR.
     *
     * @param bufferedImage the buffered image containing the field to be read
     * @param document      the document configuration
     * @param name          the name of the field
     * @return the field value obtained from the provided buffered image
     * @throws TesseractException if an error occurs while using Tesseract OCR
     */
    private String readFieldValue(BufferedImage bufferedImage,
                                  BoundingConfig.Document document,
                                  String name) throws TesseractException {
        return tesseract.doOCR(bufferedImage, "", List.of(document.roi(name)));
    }

    /**
     * Encodes a BufferedImage object into a Base64-encoded string representation of the image.
     *
     * @param image the BufferedImage object to encode
     * @return the Base64-encoded string representation of the image
     */
    private String encodeBase64(BufferedImage image) {
        String imageString = null;

        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream()
        ) {
            ImageIO.write(image, OcrService.PNG, bos);
            byte[] imageBytes = bos.toByteArray();
            Base64.Encoder encoder = Base64.getEncoder();
            imageString = encoder.encodeToString(imageBytes);
        } catch (IOException e) {
            log.error("Error occurred while encoding image", e);
        }
        return "data:image/" + OcrService.PNG + ";base64," + imageString;
    }

    /**
     * Deskews the given image using the ImageDeskew algorithm.
     *
     * @param bImg the BufferedImage to be deskewed
     * @return the deskewed BufferedImage
     */
    private static BufferedImage deskewImage(BufferedImage bImg) {
        double minimumDeskewThreshold = 0.05;
        ImageDeskew deskew = new ImageDeskew(bImg);
        double imageSkewAngle = deskew.getSkewAngle();

        if ((imageSkewAngle > minimumDeskewThreshold || imageSkewAngle < -(minimumDeskewThreshold))) {
            bImg = ImageUtil.rotate(bImg, -imageSkewAngle, bImg.getWidth() / 2, bImg.getHeight() / 2);
        }

        return bImg;
    }

}
