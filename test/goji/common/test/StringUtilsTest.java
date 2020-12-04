package goji.common.test;

import static org.junit.Assert.*;
import org.junit.Test;
import goji.common.utils.StringUtils;

/** Tests String utility methods
 *
 *  @author Aaron
 *  @version 2020.10.30
 */
public class StringUtilsTest {

    @Test
    public void isNullOrEmptyTest() {
        String test1 = null;
        assertTrue(StringUtils.isNullOrEmpty(test1));

        String test2 = "";
        assertTrue(StringUtils.isNullOrEmpty(test2));

        String test3 = "notNullOrEmpty";
        assertFalse(StringUtils.isNullOrEmpty(test3));
    }

    @Test
    public void applyFormatTest() {
        String s = "This is the arg {0} I want to apply a {1} to.. {2} {ignore} {a} {a1}";

        s = StringUtils.applyFormat(s, "value", "format", "third");

        assertEquals("This is the arg value I want to apply a format to.. third {ignore} {a} {a1}", s);
    }

}
