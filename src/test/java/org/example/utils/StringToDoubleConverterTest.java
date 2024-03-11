package org.example.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StringToDoubleConverterTest {

    @Test
    void convert_SimpleDoubleValue_ReturnsDouble() {
        assertEquals(2.5, StringToDoubleConverter.convert("2.5"), 0.001);
    }

    @Test
    void convert_Fraction_ReturnsCorrectDouble() {
        assertEquals(0.5, StringToDoubleConverter.convert("1/2"), 0.001);
    }

    @Test
    void convert_WholeNumberFraction_ReturnsCorrectDouble() {
        assertEquals(2.0, StringToDoubleConverter.convert("4/2"), 0.001);
    }

    @Test
    void convert_InvalidFormat_ThrowsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> StringToDoubleConverter.convert("invalid"));
    }

    @Test
    void convert_EmptyString_ThrowsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> StringToDoubleConverter.convert(""));
    }

    @Test
    void convert_InvalidFractionFormat_ThrowsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> StringToDoubleConverter.convert("1/a"));
    }

    @Test
    void convert_MultipleSlashesInFraction_ThrowsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> StringToDoubleConverter.convert("1/2/3"));
    }
}
