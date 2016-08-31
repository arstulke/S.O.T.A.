package sota;

import org.junit.Before;
import org.junit.Test;
import org.testng.Assert;

import java.awt.*;

public class SotaHandlerHandles {

    SotaHandler sHandler;
    @Before
    public void setup(){
        sHandler = new SotaHandler(new Point(0, 0));
    }

    @Test
    public void playerMovesRight() {
        //Given
        boolean[] keys = new boolean[]{true, false, false, false, false, false, false};

        //When
        sHandler.handle(keys);

        //Then
        Assert.assertEquals(true, sHandler.keyRight);
        Assert.assertEquals(false, sHandler.keyLeft);
        Assert.assertEquals(false, sHandler.keyUp);
    }
}
