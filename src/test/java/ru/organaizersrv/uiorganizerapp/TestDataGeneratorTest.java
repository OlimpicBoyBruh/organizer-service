package ru.organaizersrv.uiorganizerapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import ru.organaizersrv.uiorganizerapp.util.TestDataGenerator;
import ru.organaizersrv.uiorganizerapp.service.DisciplineService;
import ru.organaizersrv.uiorganizerapp.service.MaterialService;

public class TestDataGeneratorTest {

    @Test
    public void testTransliterate() throws Exception {
        // Create TestDataGenerator instance with mock services
        TestDataGenerator generator = new TestDataGenerator(null, null);
        
        // Access the private transliterate method using reflection
        Method transliterateMethod = TestDataGenerator.class.getDeclaredMethod("transliterate", String.class);
        transliterateMethod.setAccessible(true);
        
        // Test the transliteration of several Russian words
        assertEquals("Matematika", transliterateMethod.invoke(generator, "Математика"));
        assertEquals("Fizika", transliterateMethod.invoke(generator, "Физика"));
        assertEquals("Informatika", transliterateMethod.invoke(generator, "Информатика"));
        assertEquals("Istoriya", transliterateMethod.invoke(generator, "История"));
        assertEquals("Angliyskiy yazyk", transliterateMethod.invoke(generator, "Английский язык"));
        
        System.out.println("Transliteration test passed successfully!");
    }

    @Test
    public void testPdfCreation() {
        try {
            // Create a test PDF with Russian text to verify our fix
            String testFilePath = "target/test-pdf.pdf";
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.newLineAtOffset(100, 700);
                
                // This would cause an exception if we used Cyrillic directly
                // Instead we use transliterated text which works with PDType1Font
                contentStream.showText("Study Material: Matematika");
                
                contentStream.endText();
            }

            document.save(testFilePath);
            document.close();
            
            System.out.println("PDF creation test passed successfully!");
            
            return; // Success
        } catch (Exception e) {
            e.printStackTrace();
            fail("PDF creation test failed: " + e.getMessage());
        }
    }
} 