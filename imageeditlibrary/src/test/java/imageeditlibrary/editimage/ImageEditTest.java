package imageeditlibrary.editimage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

/**
 * Created by panyi on 2017/11/15.
 */
@RunWith(JUnit4.class)
public class ImageEditTest {
    @Before
    public void prepare(){
    }

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_hell(){
        assertEquals(100, 20*5);
    }
}//end class
