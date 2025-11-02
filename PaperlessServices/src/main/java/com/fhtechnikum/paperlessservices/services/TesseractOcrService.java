package com.fhtechnikum.paperlessservices.services;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.ImageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * Service for performing OCR using Tesseract
 * Implements best practices from SWEN3 course:
 * - High-resolution images (300 DPI)
 * - Proper error handling
 * - Language hints
 * - Correct tessdata path
 */
@Service
public class TesseractOcrService {

    private static final Logger log = LoggerFactory.getLogger(TesseractOcrService.class);

    // Best practice: Use high DPI for better OCR quality
    private static final int OCR_DPI = 300;

    @Value("${tesseract.data-path:/usr/share/tessdata}")
    private String tessDataPath;

    @Value("${tesseract.language:eng}")
    private String language;

    /**
     * Perform OCR on a file based on its MIME type
     *
     * @param fileData File content as byte array
     * @param mimeType MIME type of the file
     * @return Extracted text
     */
    public String performOcr(byte[] fileData, String mimeType) throws Exception {
        log.info("Starting OCR processing for MIME type: {}", mimeType);

        if (mimeType == null) {
            throw new IllegalArgumentException("MIME type cannot be null");
        }

        String extractedText;

        try {
            switch (mimeType.toLowerCase()) {
                case "application/pdf":
                    extractedText = processPdf(fileData);
                    break;
                case "image/png":
                case "image/jpeg":
                case "image/jpg":
                case "image/tiff":
                case "image/bmp":
                    extractedText = processImage(fileData);
                    break;
                case "text/plain":
                    extractedText = new String(fileData);
                    break;
                default:
                    log.warn("Unsupported MIME type for OCR: {}", mimeType);
                    extractedText = "[OCR not supported for file type: " + mimeType + "]";
            }

            log.info("OCR processing completed. Extracted {} characters", extractedText.length());
            return extractedText;

        } catch (Exception e) {
            // Best practice: Proper error handling
            log.error("OCR processing failed for MIME type {}: {}", mimeType, e.getMessage(), e);
            throw new Exception("OCR processing failed: " + e.getMessage(), e);
        }
    }

    /**
     * Process PDF file - convert to images and perform OCR on each page
     * Best practice: Use high-resolution images (300 DPI)
     */
    private String processPdf(byte[] pdfData) throws IOException, TesseractException {
        log.info("Processing PDF document");
        StringBuilder fullText = new StringBuilder();

        // PDFBox 2.0.x API: Use PDDocument.load() instead of Loader.loadPDF()
        try (PDDocument document = PDDocument.load(pdfData)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int pageCount = document.getNumberOfPages();
            log.info("PDF has {} pages", pageCount);

            for (int page = 0; page < pageCount; page++) {
                log.info("Processing PDF page {}/{}", page + 1, pageCount);

                // Best practice: High-resolution image (300 DPI) for better OCR quality
                BufferedImage image = pdfRenderer.renderImageWithDPI(page, OCR_DPI, ImageType.RGB);

                // Perform OCR on the page image
                String pageText = performOcrOnImage(image);
                fullText.append("--- Page ").append(page + 1).append(" ---\n");
                fullText.append(pageText);
                fullText.append("\n\n");
            }
        }

        return fullText.toString().trim();
    }

    /**
     * Process image file directly with OCR
     */
    private String processImage(byte[] imageData) throws IOException, TesseractException {
        log.info("Processing image file");
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));

        if (image == null) {
            throw new IOException("Failed to read image data");
        }

        return performOcrOnImage(image);
    }

    /**
     * Perform OCR on a BufferedImage using Tesseract
     * Implements best practices:
     * - Correct tessdata path
     * - Language hint (-l eng)
     * - Error handling
     */
    private String performOcrOnImage(BufferedImage image) throws TesseractException {
        Tesseract tesseract = new Tesseract();

        // Best practice: Set correct tessdata path
        File tessDataDir = new File(tessDataPath);
        if (tessDataDir.exists()) {
            tesseract.setDatapath(tessDataPath);
            log.debug("Using Tesseract data path: {}", tessDataPath);
        } else {
            log.warn("Tesseract data path not found: {}. Attempting to use default.", tessDataPath);
            // Try without setting datapath - Tesseract will use system default
        }

        // Best practice: Use language hint (-l eng)
        tesseract.setLanguage(language);
        log.debug("Using Tesseract language: {}", language);

        // Perform OCR with error handling
        try {
            String text = tesseract.doOCR(image);
            return text != null ? text.trim() : "";
        } catch (TesseractException e) {
            log.error("Tesseract OCR failed: {}", e.getMessage());
            // Best practice: Provide helpful error messages
            if (e.getMessage().contains("tessdata")) {
                throw new TesseractException("Tesseract language model not found. Path: " + tessDataPath);
            }
            throw e;
        }
    }
}
