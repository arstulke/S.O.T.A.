package sota;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

public class SotaHandlerHandles {

    private SotaHandler sHandler;

    @Before
    public void setup() {
        sHandler = new SotaHandler(new Point(0, 0), System.getProperty("user.dir") + "\\testMap.txt");
    }

    @Test
    public void playerMovesRight() {
        //Given/When/Then
        Assert.assertThat(sHandler.position, CoreMatchers.is(new Point(17, 82)));

        sHandler.handle(true, false, false, false);
        Assert.assertThat(sHandler.position, CoreMatchers.is(new Point(17, 83)));

        sHandler.handle(true, false, false, false);
        Assert.assertThat(sHandler.position, CoreMatchers.is(new Point(17, 84)));
    }

    @Test
    public void playerMovesLeft() {
        //Given/When/Then
        Assert.assertThat(sHandler.position, CoreMatchers.is(new Point(17, 82)));

        sHandler.handle(false, true, false, false);
        Assert.assertThat(sHandler.position, CoreMatchers.is(new Point(17, 81)));

        sHandler.handle(false, true, false, false);
        Assert.assertThat(sHandler.position, CoreMatchers.is(new Point(17, 80)));
    }

    @Test
    public void playerJump() {
        //Given/When/Then
        Assert.assertThat(sHandler.position, CoreMatchers.is(new Point(17, 82)));

        sHandler.handle(false, false, true, false);
        Assert.assertThat(sHandler.position, CoreMatchers.is(new Point(17, 82)));

        sHandler.handle(false, false, false, false);
        Assert.assertThat(sHandler.position, CoreMatchers.is(new Point(16, 82)));

        sHandler.handle(false, false, false, false);
        Assert.assertThat(sHandler.position, CoreMatchers.is(new Point(15, 82)));

        sHandler.handle(false, false, false, false);
        Assert.assertThat(sHandler.position, CoreMatchers.is(new Point(15, 82)));

        sHandler.handle(false, false, false, false);
        Assert.assertThat(sHandler.position, CoreMatchers.is(new Point(16, 82)));

        sHandler.handle(false, false, false, false);
        Assert.assertThat(sHandler.position, CoreMatchers.is(new Point(17, 82)));
    }
}
