package service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    @Test
    public void clearPositive() throws Exception {
        ClearService service = new ClearService();
        assertDoesNotThrow(service::clear);
    }

}