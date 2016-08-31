package sota;

import org.junit.Test;
import org.testng.Assert;

public class SotaHandlerHandles {

    @Test
    public void keyBooleanUpdates() {
        //Given
        SotaHandler sHandler = new SotaHandler();
        boolean[] keys = new boolean[]{true, false, false, false, false, false, false};

        //When
        sHandler.handle(keys);

        //Then
        Assert.assertEquals(true, sHandler.keyRight);
        Assert.assertEquals(false, sHandler.keyLeft);
        Assert.assertEquals(false, sHandler.keyUp);
    }
}
