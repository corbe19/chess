package service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    @Test
    public void clear_Positive() throws Exception {
        ClearService service = new ClearService();
        assertDoesNotThrow(service::clear);
    }

    @Test
    public void clear_Negative() {
        //uhhh I dont know how to trigger this
        assertThrows(Exception.class, () -> {
        });
    }
}