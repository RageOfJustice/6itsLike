package look_inside;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RegexPatterns {
    /**
     * Used in parsing the action attribute to know which symbol to substring from
     */
    static int ACTION_OFFSET = 1;
    /**
     * Used in parsing the inputs to know which symbol to substring from
     */
    static int INPUT_OFFSET = 7;
    /**
     * Pattern to parse the action attribute of the authorization form
     */
    static String PAT_ACTION = "action=\".+\"";
    /**
     * Pattern to parse the _origin input
     */
    static String PAT__ORIGIN = "name=\"_origin\" value=\".+\"";
    /**
     * Pattern to parse the ip_h input
     */
    static String PAT_IP_H = "name=\"ip_h\" value=\".+\"";
    /**
     * Pattern to parse the lg_h input
     */
    static String PAT_LG_H = "name=\"lg_h\" value=\".+\"";
    /**
     * Pattern to parse the to input
     */
    static String PAT_TO = "name=\"to\" value=\".+\"";
    /**
     * Pattern to parse the access_token from the url
     */
    static String PAT_ACCESS_TOKEN = "access_token=.+&e";
    /**
     * Pattern to parse the user_id from the url
     */
    static String PAT_USER_ID = "user_id=.+";

    /**
     * Parses the action attribute of the form
     *
     * @param target String to parse from
     * @return Found match
     */
    static String parseAction(String target) {
        Pattern pattern = Pattern.compile(PAT_ACTION);
        Matcher matcher = pattern.matcher(target);

        String match = "";
        if (matcher.find()) {
            match = matcher.group();
            match =  match.substring(match.indexOf("\"") + ACTION_OFFSET, match.length() - 1);
        }
        return match;
    }

    /**
     * Parses input of the form attribute
     *
     * @param patternStr Regex pattern
     * @param target String to parse from
     * @return Found match
     */
    static String parseInput(String patternStr, String target) {
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(target);

        String match = "";
        if (matcher.find()) {
            match = matcher.group();
            match =  match.substring(match.indexOf("value=\"") + INPUT_OFFSET, match.length() - 1);
        }
        return match;
    }

    /**
     * Parses the url for the access_token value
     *
     * @param target String to parse from
     * @return Found access_token
     */
    static String parseAccessToken(String target) {
        Pattern pattern = Pattern.compile(PAT_ACCESS_TOKEN);
        Matcher matcher = pattern.matcher(target);

        String match = "";
        if (matcher.find()) {
            match = matcher.group();
            match =  match.substring(match.indexOf("=") + 1, match.length() - 2);
        }
        return match;
    }

    /**
     * Parses the url for the user_id value
     *
     * @param target String to parse from
     * @return Found user_id
     */
    static String parseUserId(String target) {
        Pattern pattern = Pattern.compile(PAT_USER_ID);
        Matcher matcher = pattern.matcher(target);

        String match = "";
        if (matcher.find()) {
            match = matcher.group();
            match =  match.substring(match.indexOf("=") + 1);
        }
        return match;
    }
}