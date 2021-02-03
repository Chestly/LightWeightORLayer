package net.questcraft;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tests concerning Regex operations
 *
 * @see https://www.tutorialspoint.com/java/java_regular_expressions.htm
 */
public class RegexTests {
    @Test
    public void testContainsRegex() {
        String testLine = "The Quick brown jumps over the lazy dog";
        String pattern = "quick";

        Pattern regexPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = regexPattern.matcher(testLine);

        if (matcher.find()) System.out.println("We found it!");
        else System.out.println("We didnt find it");
    }

    @Test
    public void testRegexGroups() {
        String testLine = "Hi 1Man how r u doing HI? 1 2 345! ?% pook COOL";
        String pattern = "((Hi)(1Man))";

        Pattern regexPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = regexPattern.matcher(testLine);

        if (matcher.find()) {
            System.out.println("We found it!");
            System.out.println(matcher.group(0));
        } else System.out.println("We didnt find it");
    }

    @Test
    public void testReplace() {
        String testLine = "The Person eats Person food. All persons eat person food";
        String regex = "(person)";
        String replace = "dog";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(testLine);
        System.out.println(matcher.replaceAll(replace));
    }

    @Test
    public void testOtherREGEX() {
        String testLine = "The Person eats Person food. All persons eat person food next";
        String regex = "\\w";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(testLine);
        if (matcher.find()) {
            System.out.println("We found it!");
        } else System.out.println("We didnt find it");
    }
    /**
     * | = OR
     * ^something = matches beginning of line. NOT THE ENTIRE STRING
     * something$ = matches end of the line
     * [any character(s)] = matches any single character found in brackets, If even one is present it is a match
     * [^any character(s)] = matches any single characters not in the brackets. If all characters present in the tested against string are present in the REGEX it will not match
     *
     *
     */

    @Test
    public void jdbcURL() {
        //TODO take this out
        String url = "jdbc:mariadb://192.168.0.75:3306/SundtMemesDB";

        String regex = "^jdbc:(a-zA-Z)://():()/()$";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);

        System.out.println(matcher.find());
    }

    @Test
    public void testJDBCStart() {
        String test = "jdbc:mariadb";
        String regex = "^jdbc:([a-zA-Z]+)$";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(test);

        System.out.println(matcher.find());

        System.out.println(matcher.group(1));
    }

    @Test
    public void testIPStart() {
        String test = "://192.168.0.75";
        String regex = "^://([\\w.]+)(?::([0-9]{1,4}))*$";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(test);

        System.out.println(matcher.find());

        System.out.println(matcher.group(1));
        System.out.println(matcher.group(2));

    }

    @Test
    public void testDBStart() {
        String test = "/SundtMemesDB";
        String regex = "^/([\\w]+)$";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(test);

        System.out.println(matcher.find());

        System.out.println(matcher.group(1));
    }

    @Test
    public void testFullDBURL() {
        String url = "jdbc:mariadb://192.168.0.75/Test";

        String regex = "^jdbc:([a-zA-Z]+)" +
                "://([\\w.]+)(?::([0-9]{1,4}))*" +
                "/*([\\w]*)$";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);

        System.out.println(matcher.find());

        System.out.println(matcher.group(1));
        System.out.println(matcher.group(2));
        System.out.println(matcher.group(3));
        System.out.println(matcher.group(4));

    }
}
