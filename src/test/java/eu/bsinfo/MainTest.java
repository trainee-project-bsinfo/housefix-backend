package eu.bsinfo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MainTest {
    @Test
    public void testValidateTestModeOnly() {
        Main.ensureOnlyForTesting();
        System.setProperty("testing", "false");
        Assertions.assertThrows(UnsupportedOperationException.class, Main::ensureOnlyForTesting);
        System.setProperty("testing", "true");
    }
}
