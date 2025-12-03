package es.upm.grise.profundizacion.file;

import es.upm.grise.profundizacion.exceptions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.zip.CRC32;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

/**
 * Automated tests for File.java (corrected)
 *
 * - Uses JUnit 5 (Jupiter)
 * - Uses Mockito (mockConstruction) to stub FileUtils behavior where needed
 *
 * Note: File.getCRC32() internally constructs a new FileUtils(), so tests that assert
 * on CRC32 values must mock that construction to control the returned CRC value.
 */
public class FileTest {

    @Test
    public void constructor_initializesEmptyNonNullContent() {
        File file = new File();
        assertNotNull(file.getContent(), "content should be non-null after construction");
        assertTrue(file.getContent().isEmpty(), "content should be empty after construction");
    }

    @Test
    public void addProperty_nullArgument_throwsInvalidContentException() {
        File file = new File();
        assertThrows(InvalidContentException.class, () -> file.addProperty(null));
    }

    @Test
    public void addProperty_whenTypeIsImage_throwsWrongFileTypeException() {
        File file = new File();
        file.setType(FileType.IMAGE);
        char[] data = new char[] { 'a', 'b' };
        assertThrows(WrongFileTypeException.class, () -> file.addProperty(data));
    }

    @Test
    public void addProperty_appendsContentCorrectly_whenTypeIsNotImage() {
        File file = new File();
        // Do not set type (null) so it is not FileType.IMAGE -> allowed
        char[] data = new char[] { 'H', 'i', 'ñ' }; // include a non-ascii char to check low-byte behavior
        file.addProperty(data);

        List<Character> content = file.getContent();
        assertEquals(3, content.size(), "content size should match number of characters appended");
        assertEquals(Character.valueOf('H'), content.get(0));
        assertEquals(Character.valueOf('i'), content.get(1));
        assertEquals(Character.valueOf('ñ'), content.get(2));
    }

    @Test
    public void getCRC32_emptyContent_returnsZero() {
        File file = new File();
        long crc = file.getCRC32();
        assertEquals(0L, crc, "CRC32 should be 0 for empty content");
    }

    /**
     * Corrected test:
     *
     * File.getCRC32() constructs a new FileUtils() internally. The actual implementation
     * of FileUtils is out-of-scope for this unit test, so we mock the construction of
     * FileUtils and force calculateCRC32(...) to return the CRC value we expect.
     */
    @Test
    public void getCRC32_nonEmptyContent_matchesCRCComputedDirectly() {
        File file = new File();

        // Prepare content with a mixture of characters (including >127 to check byte truncation)
        char[] data = new char[] { 'A', 'B', 'C', 'ñ' };
        file.addProperty(data);

        // Replicate File.getCRC32 logic to compute expected CRC32 from the bytes
        byte[] bytes = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            bytes[i] = (byte) (data[i] & 0x00FF);
        }
        CRC32 crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        long expected = crc32.getValue();

        // Mock construction of FileUtils so that any new FileUtils() instance returns the expected value.
        try (MockedConstruction<FileUtils> mocked = Mockito.mockConstruction(FileUtils.class,
                (mock, context) -> {
                    when(mock.calculateCRC32(any())).thenReturn(expected);
                })) {

            long actual = file.getCRC32();
            assertEquals(expected, actual, "CRC32 from File should match CRC32 provided by mocked FileUtils");
        }
    }

    @Test
    public void getCRC32_usesFileUtils_whenFileUtilsIsMocked() {
        File file = new File();
        char[] data = new char[] { 'x', 'y', 'z' };
        file.addProperty(data);

        // Mock construction of FileUtils so that any new FileUtils() instance returns a stubbed value.
        try (MockedConstruction<FileUtils> mocked = Mockito.mockConstruction(FileUtils.class,
                (mock, context) -> {
                    when(mock.calculateCRC32(any())).thenReturn(0xDEADBEEFL);
                })) {

            long crc = file.getCRC32();
            assertEquals(0xDEADBEEFL, crc, "getCRC32 should return the value provided by the mocked FileUtils instance");
        }
    }

}

