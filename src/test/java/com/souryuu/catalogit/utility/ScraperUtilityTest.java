package com.souryuu.catalogit.utility;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Disabled
public class ScraperUtilityTest {


    @Test
    @DisplayName("Null String As Input")
    public void shouldThrowIllegalArgumentException_whenInputIsNull() {
        String testedString = null;
        assertThrowsExactly(IllegalArgumentException.class, () -> ScraperUtility.formatToCamelCase(testedString));
    }

    @Test
    @DisplayName("String Containing Lowercase Words")
    public void whenInputIsLowercase_thenReturnCamelCase() {
        String[] testString = {"teststring", "this is a test string"};
        String[] expected = {"Teststring", "This Is A Test String"};
        // Execute All Assertions
        for(int i = 0; i < testString.length; i++) {
            assertEquals(expected[i], ScraperUtility.formatToCamelCase(testString[i]));
        }
    }

    @Test
    @DisplayName("String Contains Single UpperCase Word")
    public void whenInputIsUppercase_shouldReturnCamelCase() {
        String[] testString = {"TESTSTRING", "THIS IS A TEST STRING"};
        String[] expected = {"Teststring", "This Is A Test String"};

        for(int i = 0; i < testString.length; i++) {
            assertEquals(expected[i], ScraperUtility.formatToCamelCase(testString[i]));
        }
    }

    @Test
    @DisplayName("String Containing Hyphen")
    public void when_inputContainsHyphen_thenReturnCameCase() {
        String[] testString = {"test-string", "-this-is-a-test-string", "-----", "- - -", "A --B -- c --d"};
        String[] expected = {"Test-String", "-This-Is-A-Test-String", "-----", "- - -", "A --B -- C --D"};

        for(int i = 0; i < testString.length; i++) {
            assertEquals(expected[i], ScraperUtility.formatToCamelCase(testString[i]));
        }
    }

    @Test
    @DisplayName("Group Of Special Characters Inside String")
    public void whenSpecialCharactersNoSpaces_thenReturnCamelCase() {
        String[] testString = {"this!@#$string", "!@#$%^&*()_+cde|}{\":?><,./;'[]\\-='"};
        String[] expected = {"This!@#$String" , "!@#$%^&*()_+Cde|}{\":?><,./;'[]\\-='"};

        for(int i = 0; i < testString.length; i++) {
            assertEquals(expected[i], ScraperUtility.formatToCamelCase(testString[i]));
        }
    }



}
