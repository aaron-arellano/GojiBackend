package goji.common.test;

import static org.junit.Assert.*;
import goji.common.utils.StringUtils;
import org.junit.Test;

/** Tests String utility methods
 *
 *  @author Aaron
 *  @version 2020.10.30
 */
public class StringUtilsTest {

    @SuppressWarnings("javadoc")
    @Test
    public void isNullOrEmptyTest() {
        String test1 = null;
        assertTrue(StringUtils.isNullOrEmpty(test1));

        String test2 = "";
        assertTrue(StringUtils.isNullOrEmpty(test2));

        String test3 = "notNullOrEmpty";
        assertFalse(StringUtils.isNullOrEmpty(test3));
    }

    @SuppressWarnings("javadoc")
    @Test
    public void applyFormatTest() {
        String s = "This is the arg {0} I want to apply a {1} to.. {2} {ignore} {a} {a1}";

        s = StringUtils.applyFormat(s, "value", "format", "third");

        assertEquals("This is the arg value I want to apply a format to.. third {ignore} {a} {a1}", s);
    }

}
