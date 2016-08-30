package SOTA;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

public class SOTAMapReaderTests {

    @Test
    public void readingMap(){
        // given:
        String path = "C:\\Users\\kaabert\\Desktop\\map.txt";
        String[][][] test = new String[][][]{{{""}}};
        when(SOTAMapReader.getMap()).thenReturn(test);

        // when:
        SOTAMapReader.readMap(path);


        // then:
        assertThat(SOTAMapReader.getMap(), is(test));
    }

}
