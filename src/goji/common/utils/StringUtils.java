package goji.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Utility class to house common methods for string operations
 *
 *  @author Aaron
 *  @version 2020.10.27
 */
public final class StringUtils {

    private StringUtils() {}

    /** Handles boolean logic for string that is either null value or empty
     *
     * @param s the string that will be checked
     * @return true if the String is null or empty
     */
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }


    /** Applies custom format to a string with arguments. Must be used with arguments
     *  identified in the string to be applied, example:
     *  StringUtils.applyFormat("These args will be applied: {0} {1}", arg1, arg2);
     *
     * @param strings VARARGS list of Strings to apply a syntactic apply format to
     * @return the applied string a format was set to
     */
    public static String applyFormat(String... strings) {
        if(strings.length < 2) {
            throw new IllegalArgumentException("String to format must contain at least one argument");
        }

        Pattern pattern = Pattern.compile("[{]\\d+[}]");
        Matcher matcher = pattern.matcher(strings[0]);
        int count = 0;
        while (matcher.find()) {
            count++;
        }

        if (strings.length-1 != count) {
            throw new IllegalArgumentException("Number of arguments to replace in String does not match arguments passed");
        }

        String base = strings[0];

        for (int i = 1; i < strings.length; i++) {
            String arg = strings[i];
            String argPos = Integer.toString(i-1);
            argPos = "{"+argPos+"}";
            base = base.replace(argPos, arg);
        }

        return base;
    }
}
