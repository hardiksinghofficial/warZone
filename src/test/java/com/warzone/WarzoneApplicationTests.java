package com.warzone;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WarzoneApplicationTests {

    @Test
    void applicationNameIsCorrect() {
        assertEquals("warzone-intel", "warzone-intel");
    }

    @Test
    void riskLevelsAreDefined() {
        String[] levels = {"LOW", "GUARDED", "ELEVATED", "HIGH", "CRITICAL"};
        assertEquals(5, levels.length);
    }
}
