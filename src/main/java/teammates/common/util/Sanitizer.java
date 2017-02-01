package teammates.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import com.google.appengine.api.datastore.Text;

/**
 * Class contains methods to sanitize user provided
 * parameters so that they conform to our data format
 * and possible threats can be removed first.
 */
public final class Sanitizer {

    private static PolicyFactory richTextPolicy =
            new HtmlPolicyBuilder()
                .allowStandardUrlProtocols()
                .allowAttributes("title").globally()
                .allowAttributes("href").onElements("a")
                .allowAttributes("src").onElements("img")
                .allowAttributes("align")
                    .matching(true, "center", "left", "right", "justify", "char")
                    .onElements("p")
                .allowElements(
                    "a", "p", "div", "i", "b", "em", "blockquote", "tt", "strong", "hr",
                    "br", "ul", "ol", "li", "h1", "h2", "h3", "h4", "h5", "h6", "img", "span")
                .allowElements("quote", "ecode")
                .allowStyling()
                .toFactory();

    private Sanitizer() {
        // utility class
    }
    
    /**
     * Sanitizes a google ID by removing leading/trailing whitespace
     * and the trailing "@gmail.com".
     * 
     * @param rawGoogleId
     * @return the sanitized google ID or null (if the parameter was null).
     */
    public static String sanitizeGoogleId(String rawGoogleId) {
        if (rawGoogleId == null) {
            return null;
        }
        
        String sanitized = rawGoogleId.trim();
        if (sanitized.toLowerCase().endsWith("@gmail.com")) {
            sanitized = sanitized.split("@")[0];
        }
        return sanitized.trim();
    }
    
    /**
     * Sanitizes an email address by removing leading/trailing whitespace.
     * 
     * @param rawEmail
     * @return the sanitized email address or null (if the parameter was null).
     */
    public static String sanitizeEmail(String rawEmail) {
        return StringHelper.trimIfNotNull(rawEmail);
    }
    
    /**
     * Sanitizes name by removing leading, trailing, and duplicate internal whitespace.
     * 
     * @param rawName
     * @return the sanitized name or null (if the parameter was null).
     */
    public static String sanitizeName(String rawName) {
        return StringHelper.removeExtraSpace(rawName);
    }
    
    /**
     * Sanitizes title by removing leading, trailing, and duplicate internal whitespace.
     * 
     * @param rawTitle
     * @return the sanitized title or null (if the parameter was null).
     */
    public static String sanitizeTitle(String rawTitle) {
        return StringHelper.removeExtraSpace(rawTitle);
    }
    
    /**
     * Sanitizes a user input text field by removing leading/trailing whitespace.
     * i.e. comments, instructions, etc.
     * 
     * @param rawText
     * @return the sanitized text or null (if the parameter was null).
     */
    public static String sanitizeTextField(String rawText) {
        return StringHelper.trimIfNotNull(rawText);
    }
    
    /**
     * Sanitizes a user input text field by removing leading/trailing whitespace.
     * i.e. comments, instructions, etc.
     * 
     * @param rawText
     * @return the sanitized text or null (if the parameter was null).
     */
    public static Text sanitizeTextField(Text rawText) {
        if (rawText == null) {
            return null;
        }
        return new Text(StringHelper.trimIfNotNull(rawText.getValue()));
    }

    /**
     * Escape the string for inserting into javascript code.
     * This automatically calls {@link #sanitizeForHtml} so make it safe for HTML too.
     *
     * @param str
     * @return the sanitized string or null (if the parameter was null).
     */
    public static String sanitizeForJs(String str) {
        if (str == null) {
            return null;
        }
        return Sanitizer.sanitizeForHtml(
                str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("'", "\\'")
                .replace("#", "\\#"));
    }

    /**
     * Sanitizes the string with rich-text.
     * Removes disallowed elements based on defined policy.
     */
    public static String sanitizeForRichText(String content) {
        if (content == null) {
            return null;
        }
        return richTextPolicy.sanitize(sanitizeTextField(content));
    }
    
    /**
     * Sanitizes the {@link Text} with rich-text.
     * Removes disallowed elements based on defined policy.
     * @return A new sanitized {@link Text} or null if the input was null.
     */
    public static Text sanitizeForRichText(Text text) {
        if (text == null || text.getValue() == null) {
            return null;
        }
        return new Text(sanitizeForRichText(text.getValue()));
    }

    /**
     * Sanitizes the string for inserting into HTML. Converts special characters
     * into HTML-safe equivalents.
     */
    public static String sanitizeForHtml(String str) {
        if (str == null) {
            return null;
        }
        return str.replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("/", "&#x2f;")
                .replace("'", "&#39;")
                //To ensure when apply sanitizeForHtml for multiple times, the string's still fine
                //Regex meaning: replace '&' with safe encoding, but not the one that is safe already
                .replaceAll("&(?!(amp;)|(lt;)|(gt;)|(quot;)|(#x2f;)|(#39;))", "&amp;");
    }

    /**
     * Escapes HTML tag safely. This function can be applied multiple times.
     */
    public static String sanitizeForHtmlTag(String str) {
        if (str == null) {
            return null;
        }
        return str.replace("<", "&lt;").replace(">", "&gt;");
    }
    
    /**
     * Sanitizes a list of strings for inserting into HTML.
     */
    public static List<String> sanitizeForHtml(List<String> list) {
        List<String> sanitizedList = new ArrayList<String>();
        for (String str : list) {
            sanitizedList.add(sanitizeForHtml(str));
        }
        return sanitizedList;
    }
    
    /**
     * Sanitizes a set of strings for inserting into HTML.
     */
    public static Set<String> sanitizeForHtml(Set<String> set) {
        Set<String> sanitizedSet = new TreeSet<String>();
        for (String str : set) {
            sanitizedSet.add(sanitizeForHtml(str));
        }
        return sanitizedSet;
    }
    
    /**
     * Converts a string to be put in URL (replaces some characters)
     */
    public static String sanitizeForUri(String uri) {
        try {
            return URLEncoder.encode(uri, Const.SystemParams.ENCODING);
        } catch (UnsupportedEncodingException wonthappen) {
            return uri;
        }
    }
    
    /**
     * Sanitizes the given URL for the parameter {@link Const.ParamsNames.NEXT_URL}.
     * The following characters will be sanitized:
     * <ul>
     * <li>&, to prevent the parameters of the next URL from being considered as
     *     part of the original URL</li>
     * <li>%2B (encoded +), to prevent Google from decoding it back to +,
     *     which is used to encode whitespace in some cases</li>
     * <li>%23 (encoded #), to prevent Google from decoding it back to #,
     *     which is used to traverse the HTML document to a certain id</li>
     * </ul>
     *
     * @param url
     * @return the sanitized url or null (if the parameter was null).
     */
    public static String sanitizeForNextUrl(String url) {
        if (url == null) {
            return null;
        }
        return url.replace("&", "${amp}").replace("%2B", "${plus}").replace("%23", "${hash}");
    }

    /**
     * Sanitize the string for searching.
     */
    public static String sanitizeForSearch(String str) {
        if (str == null) {
            return null;
        }
        return str
                //general case for punctuation
                .replace("`", " ").replace("!", " ").replace("#", " ").replace("$", " ").replace("%", " ").replace("^", " ")
                .replace("&", " ").replace("[", " ").replace("]", " ").replace("{", " ").replace("}", " ").replace("|", " ")
                .replace(";", " ").replace("*", " ").replace(".", " ").replace("?", " ").replace("'", " ").replace("/", " ")
                //to prevent injection
                .replace("=", " ")
                .replace(":", " ")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
    
    /**
     * Sanitizes the string for comma-separated values (CSV) file output.<br>
     * We follow the definition described by RFC 4180:<br>
     * {@link http://tools.ietf.org/html/rfc4180}
     */
    public static String sanitizeForCsv(String str) {
        return "\"" + str.replace("\"", "\"\"") + "\"";
    }
    
    /**
     * Sanitizes the list of strings for comma-separated values (CSV) file output.<br>
     * We follow the definition described by RFC 4180:<br>
     * {@link http://tools.ietf.org/html/rfc4180}
     */
    public static List<String> sanitizeListForCsv(List<String> strList) {
        List<String> sanitizedStrList = new ArrayList<String>();
        
        Iterator<String> itr = strList.iterator();
        while (itr.hasNext()) {
            sanitizedStrList.add(sanitizeForCsv(itr.next()));
        }
        
        return sanitizedStrList;
    }
    
    /**
     * Convert the string to a safer version for XPath
     * For example:
     * Will o' The Wisp => concat('Will o' , "'" , ' The Wisp' , '')
     * This will result in the same string when read by XPath.
     * 
     * This is used when writing the test case for some special characters
     * such as ' and "
     * 
     * @param text
     * @return safer version of the text for XPath
     */
    public static String sanitizeStringForXPath(String text) {
        StringBuilder result = new StringBuilder();
        int startOfChain = 0;
        int textLength = text.length();
        boolean isSingleQuotationChain = false;
        // currentPos iterates through 0 to textLength (inclusive) to include last chain
        for (int currentPos = 0; currentPos <= textLength; currentPos++) {
            boolean isChainBroken = currentPos >= textLength
                    || isSingleQuotationChain && text.charAt(currentPos) != '\''
                    || !isSingleQuotationChain && text.charAt(currentPos) == '\'';
            if (isChainBroken && startOfChain < currentPos) {
                // format text.substring(startOfChain, currentPos) and append to result
                char wrapper = isSingleQuotationChain ? '\"' : '\'';
                result.append(wrapper).append(text.substring(startOfChain, currentPos)).append(wrapper).append(',');
                startOfChain = currentPos;
            }
            // flip isSingleQuotationChain if chain is broken
            if (isChainBroken) {
                isSingleQuotationChain = !isSingleQuotationChain;
            }
        }
        if (result.length() == 0) {
            return "''";
        }
        return "concat(" + result.toString() + "'')";
    }

}
