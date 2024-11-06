package eu.bsinfo.web.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HealthControllerTest {
    @Test
    public void testHealth() {
        HealthController healthController = new HealthController();
        Assertions.assertEquals("OK", healthController.getHealth());
    }
}
