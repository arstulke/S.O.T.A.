package tests;

import org.junit.Test;
import org.testng.Assert;
import sota.SotaHandler;

public class SotaHandlerHandles {

    @Test
    public void keyBooleanUpdates() {
        //Given
        SotaHandler sHandler = new SotaHandler();
        boolean[] keys = new boolean[]{true, false, false, false, false, false, false};

        //When
        sHandler.handle(keys);

        //Then
        Assert.assertEquals(true, sHandler.right);
        Assert.assertEquals(false, sHandler.left);
        Assert.assertEquals(false, sHandler.jump);
    }
}
