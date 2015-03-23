package teammates.common.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/** Holds String-related helper functions
 */
public class StringHelper {

    public static String generateStringOfLength(int length) {
        return StringHelper.generateStringOfLength(length, 'a');
    }

    public static String generateStringOfLength(int length, char character) {
        assert (length >= 0);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(character);
        }
        return sb.toString();
    }

    public static boolean isWhiteSpace(String string) {
        return string.trim().isEmpty();
    }
    
    /**
     * Check whether the input string matches the regex repression
     * @param input The string to be matched
     * @param regex The regex repression used for the matching
     */
    public static boolean isMatching(String input, String regex) {
        // Important to use the CANON_EQ flag to make sure that canonical characters
        // such as é is correctly matched regardless of single/double code point encoding
        return Pattern.compile(regex, Pattern.CANON_EQ).matcher(input).matches();
    }
    
    /**
     * Check whether any substring of the input string matches any of the group of given regex expressions
     * Currently only used in header row processing in StudentAttributesFactory: locateColumnIndexes
     * Case Insensitive
     * @param input The string to be matched
     * @param regexArray The regex repression array used for the matching
     */
    public static boolean isAnyMatching(String input, String[] regexArray) {
        for(String regex : regexArray){
            if(isMatching(input.trim().toLowerCase(), regex)){
                return true;
            }
        }   
        return false;
    }

    public static String getIndent(int length) {
        return generateStringOfLength(length, ' ');
    }

    /**
     * Checks whether the {@code inputString} is longer than a specified length
     * if so returns the truncated name appended by ellipsis,
     * otherwise returns the original input. <br>
     * E.g., "12345678" truncated to length 6 returns "123..."
     */
    public static String truncate(String inputString, int truncateLength){
        if(!(inputString.length()>truncateLength)){
            return inputString;
        }
        String result = inputString;
        if(inputString.length()>truncateLength){
            result = inputString.substring(0,truncateLength-3)+"...";
        }
        return result;
    }
    
    /**
     * Substitutes the middle third of the given string with dots
     * and returns the "obscured" string
     * 
     * @param inputString
     * @return
     */
    public static String obscure(String inputString) {
        Assumption.assertNotNull(inputString);
        String frontPart = inputString.substring(0, inputString.length() / 3);
        String endPart = inputString.substring(2 * inputString.length() / 3);
        return frontPart + ".." + endPart;
    }

    public static String encrypt(String value) {
        try {
            SecretKeySpec sks = new SecretKeySpec(
                    hexStringToByteArray(Config.ENCRYPTION_KEY), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
            byte[] encrypted = cipher.doFinal(value.getBytes());
            return byteArrayToHexString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String message) {
        try {
            SecretKeySpec sks = new SecretKeySpec(
                    hexStringToByteArray(Config.ENCRYPTION_KEY), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, sks);
            byte[] decrypted = cipher.doFinal(hexStringToByteArray(message));
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Concatenates a list of strings to a single string, separated by line breaks.
     * @return Concatenated string.
     */
    public static String toString(List<String> strings) {
        return toString(strings, Const.EOL);    
    }

    /**
     * Concatenates a list of strings to a single string, separated by the given delimiter.
     * @return Concatenated string.
     */
    public static String toString(List<String> strings, String delimiter) {
        String returnValue = "";
        
        if(strings.size()==0){
            return returnValue;
        }
        
        for(int i=0; i < strings.size()-1; i++){
            String s = strings.get(i);
            returnValue += s + delimiter;
        }
        //append the last item
        returnValue += strings.get(strings.size()-1);
        
        return returnValue;        
    }
    
    public static String toDecimalFormatString(double doubleVal) {
        DecimalFormat df = new DecimalFormat("0.#");
        return df.format(doubleVal);
    }

    public static String toUtcFormat(double hourOffsetTimeZone) {
        String utcFormatTimeZone = "UTC";
        if (hourOffsetTimeZone != 0) {
            if ((int) hourOffsetTimeZone == hourOffsetTimeZone)
                utcFormatTimeZone += String.format(" %+03d:00",
                        (int) hourOffsetTimeZone);
            else
                utcFormatTimeZone += String.format(
                        " %+03d:%02d",
                        (int) hourOffsetTimeZone,
                        (int) (Math.abs(hourOffsetTimeZone
                                - (int) hourOffsetTimeZone) * 300 / 5));
        }

        return utcFormatTimeZone;
    }
    
    //From: http://stackoverflow.com/questions/5864159/count-words-in-a-string-method
    public static int countWords(String s){
        int wordCount = 0;
        boolean word = false;
        int endOfLine = s.length() - 1;
        for (int i = 0; i < s.length(); i++) {
            // if the char is a letter, word = true.
            if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
                word = true;
                // if char isn't a letter and there have been letters before,
                // counter goes up.
            } else if (!Character.isLetter(s.charAt(i)) && word) {
                wordCount++;
                word = false;
                // last word of String; if it doesn't end with a non letter, it
                // wouldn't count without this.
            } else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
                wordCount++;
            }
        }
        return wordCount;
    }
    
    
    
    /**
     * split a full name string into first and last names
     * <br>
     * 1.If passed in empty string, both last and first name will be empty string
     * <br>
     * 2.If single word, this will be last name and first name will be an empty string
     * <br>
     * 3.If more than two words, the last word will be last name and 
     * the rest will be first name.
     * <br>
     * 4.If the last name is enclosed with braces "{}" such as first {Last1 Last2},
     * the last name will be the String inside the braces
     * <br>
     * Example: 
     * <br><br>
     * full name "Danny Tim Lin"<br>
     * first name: "Danny Tim" <br>
     * last name: "Lin" <br>
     * processed full name: "Danny Tim Lin" <br>
     * <br>
     * full name "Danny {Tim Lin}"<br>
     * first name: "Danny" <br>
     * last name: "Tim Lin" <br>
     * processed full name: "Danny Tim Lin" <br>
     * 
     * 
     * @return split name array{0--> first name, 1--> last name, 2--> processed full name by removing "{}"}
     */
    
    public static String[] splitName(String fullName){  
        
        if(fullName == null){
            return null;
        }
           
        String lastName;
        String firstName;
        
        if(fullName.contains("{") && fullName.contains("}")){
            int startIndex = fullName.indexOf("{");
            int endIndex = fullName.indexOf("}");
            lastName = fullName.substring(startIndex + 1, endIndex);
            firstName = fullName.replace("{", "")
                                .replace("}", "")
                                .replace(lastName, "")
                                .trim();           
            
        } else {         
            lastName = fullName.substring(fullName.lastIndexOf(" ")+1).trim();
            firstName = fullName.replace(lastName, "").trim();
        }
        
        String processedfullName = fullName.replace("{", "")
                                           .replace("}", "");
        
        String[] splitNames = {firstName, lastName, processedfullName};       
        return splitNames;
    }
    
    
    /**
     * trims the string and reduces consecutive white spaces to only one space
     * Example: " a   a  " --> "a a"
     * @return processed string, returns null if parameter is null
     */
    public static String removeExtraSpace(String str){       
        if(str == null){
            return null;
        }
        
        return str.trim().replaceAll("\\s+", " ");
        
    }
    
    
    private static String byteArrayToHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    private static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }
    
    
    
    /**
     * This recovers a html-sanitized string to original encoding for appropriate display in files such as csv file <br>
     * It restores encoding for < > \ / ' &  <br>
     * @param sanitized string 
     * @return recovered string  
     */
    public static String recoverFromSanitizedText(String str){  
        
        if(str == null){
            return null;
        }
        
        return str.replace("&lt;", "<")
                  .replace("&gt;", ">")
                  .replace("&quot;", "\"")
                  .replace("&#x2f;", "/")
                  .replace("&#39;", "'")
                  .replaceAll("&amp;", "&");
    }
    
    /**
     * This recovers a set of html-sanitized string to original encoding for appropriate display in files such as csv file <br>
     * It restores encoding for < > \ / ' &  <br>
     * @param sanitized string set
     * @return recovered string set
     */
    public static Set<String> recoverFromSanitizedText(Set<String> textSet) {
        Set<String> textSetTemp = new HashSet<String>();
        for (String text : textSet) {
            textSetTemp.add(StringHelper.recoverFromSanitizedText(text));
        }
        return textSetTemp;
    }
    
    /**
     * Convert a csv string to a html table string for displaying
     * @param str
     * @return html table string
     */
    public static String csvToHtmlTable(String str) {
        str = handleNewLine(str);
        String[] lines = str.split(Const.EOL);

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            
            List<String> rowData = getTableData(lines[i]);
            
            if(checkIfEmptyRow(rowData)){
                continue;
            }
            
            result.append("<tr>");
            for (String td : rowData) {
                result.append(String.format("<td>%s</td>\n", td));
            }
            result.append("</tr>");
        }

        return String.format("<table class=\"table table-bordered table-striped table-condensed\">\n%s</table>",
                             result.toString());
    }

    /**
     * Combines all the strings inside the passed list using the specified delimiter. The result will be
     * another list with a single entry, which is the combined string, or an empty list if the passed
     * argument is an empty list.
     * @param list of string
     * @param delimiter the connectors in between the strings
     * @return list containing the combined string or an empty list
     */
    private static List<String> combineListEntries(List<String> list,
            String delimiter) {
        if (list.isEmpty()) {
            return list;
        } else {
            String combinedEntries = list.get(0);
            List<String> result = new ArrayList<String>();
            if (list.size() > 1) {
                for (int i = 1; i < list.size(); i++) {
                    combinedEntries += delimiter + list.get(i);
                }
            }
            result.add(combinedEntries);
            return result;
        }
    }
    
    /**
     * If the content of the rowData is one of the following, they will be processed
     * accordingly (for the sake of aesthetics):<br><ol>
     * <li> Course, coursename -> Course: coursename</li>
     * <li> Session Name, sessionname -> Session Name: sessionname</li>
     * <li> Question #, qntitle -> Question #: qntitle</li>
     * <li> Summary Statistics, (empty) -> Summary statistics</li>
     * <li> In the points given below, (...) (comma here is the column separator) ->
     * In the points given below, (...) (comma here is the punctuation comma)</li>
     * </ol>
     * 4 and 5 need to be done as they are unintended consequences of comma-separating algorithm.
     * 
     * @param rowData
     * @return processed list of string
     */
    private static List<String> processSpecialKeyword(List<String> rowData) {
        if (rowData.get(0).equals(Const.INSTRUCTOR_FEEDBACK_RESULT_COURSE_HEADING)
                || rowData.get(0).equals(Const.INSTRUCTOR_FEEDBACK_RESULT_SESSION_NAME_HEADING)
                || rowData.get(0).matches(Const.INSTRUCTOR_FEEDBACK_RESULT_QUESTION_REGEXP)) {
            rowData = combineListEntries(rowData, ": ");
        } else if (rowData.get(0).equals(
                Const.INSTRUCTOR_FEEDBACK_RESULT_STATISTICS_HEADING)) {
            rowData = combineListEntries(rowData, "");
        } else if (rowData.get(0).equals(
                Const.FEEDBACK_CONTRIBUTION_HEADER_BEFORE_COMMA)) {
            rowData = combineListEntries(rowData, ", ");
        }
        return rowData;
    }

    /**
     * If the content of the rowData is one of the following, they will be assigned certain
     * colspan (for the sake of aesthetics):<br><ol>
     * <li> Course: coursename, Session Name: sessionname -> globalMaxLength (max. possible)</li>
     * <li> Question #: qntitle -> qnMaxLength (max. that is needed by that question's response 
     * entries)</li>
     * <li> Others -> qnMaxLength / # of columns (max. that is needed by that question's response 
     * entries, spread evenly) </li></ol>
     * 
     * @param rowData
     * @return processed list of string
     */
    private static int adaptColspanSize(List<String> rowData, int globalMaxLength, int qnMaxLength) {
        if (rowData.get(0).matches(wrapAsRegExp(Const.INSTRUCTOR_FEEDBACK_RESULT_COURSE_HEADING)) ||
                rowData.get(0).matches(wrapAsRegExp(Const.INSTRUCTOR_FEEDBACK_RESULT_SESSION_NAME_HEADING))) {
            return globalMaxLength;
        } else if (rowData.get(0).matches(wrapAsRegExp(Const.INSTRUCTOR_FEEDBACK_RESULT_QUESTION_REGEXP))) {
            return qnMaxLength;
        } else {
            return (int) Math.floor(qnMaxLength / rowData.size());
        }
    }

    private static String wrapAsRegExp(String str) {
        String wrapper = "^%s: .*";
        return String.format(wrapper, str);
    }

    /**
     * Convert a csv string to a beautified html table string for displaying.
     * There are two ways in which the table will be 'beautified':<br>
     * <ol>
     * <li>The colspan is adapted in appropriate ways so that white space
     * wastage are minimised.</li>
     * <li>Certain row data entries (course name, session name) are rewritten
     * for aesthetics purpose.</li>
     * </ol>
     * 
     * @param str
     * @return beautified html table string
     */
    public static String csvToBeautifiedHtmlTable(String str) {

        str = handleNewLine(str);
        String[] lines = str.split(Const.EOL);

        StringBuilder result = new StringBuilder();
        int globalMaxLength = 0; // global max. no. of table columns needed
        int qnMaxLength = 0; // max. no. of columns for one question
        List<ArrayList<String>> rowDataArray = new ArrayList<ArrayList<String>>();
        List<Integer> qnMaxLengthArray = new ArrayList<Integer>();

        // we first pre-process the table entries to calculate the colspan that will
        // eventually be required for each entry
        for (int i = 0; i < lines.length; i++) {

            List<String> rowData = getTableData(lines[i]);

            if (checkIfEmptyRow(rowData)) {
                continue;
            }

            if (rowData.get(0).matches(
                    Const.INSTRUCTOR_FEEDBACK_RESULT_QUESTION_REGEXP)) {
                // new question entry! add the max. no. of columns needed for the
                // previous question to the qnMaxLengthArray
                qnMaxLengthArray.add(qnMaxLength);
                // and reset the counter
                qnMaxLength = 0;
            }
            rowData = processSpecialKeyword(rowData);
            
            if (rowData.size() > globalMaxLength) {
                globalMaxLength = rowData.size();
            }
            if (rowData.size() > qnMaxLength) {
                qnMaxLength = rowData.size();
            }
            rowDataArray.add((ArrayList<String>) rowData);

        }

        // adds the max. no. of columns needed for the last question
        qnMaxLengthArray.add(qnMaxLength);
        // removes the max. no. of columns needed for the "0th question"
        qnMaxLengthArray.remove(0);

        for (List<String> rowData : rowDataArray) {

            if (rowData.get(0).matches(wrapAsRegExp(Const.INSTRUCTOR_FEEDBACK_RESULT_QUESTION_REGEXP))) {
                // new question entry! get the max. no. of columns needed from qnMaxLengthArray
                qnMaxLength = qnMaxLengthArray.get(0);
                qnMaxLengthArray.remove(0);
            } 
            int colspan = adaptColspanSize(rowData, globalMaxLength, qnMaxLength);

            result.append("<tr>");
            for (String td : rowData) {
                if (colspan == 1) {
                    result.append(String.format("<td>%s</td>\n", td));
                } else {
                    result.append(String.format("<td colspan='%d'>%s</td>\n",
                            colspan, td));
                }
            }
            result.append("</tr>");

        }

        return String
                .format("<table class=\"table table-bordered table-striped table-condensed\">\n%s</table>",
                        result.toString());
    }

    private static String handleNewLine(String str) {

        StringBuilder buffer = new StringBuilder();
        char[] chars = str.toCharArray();

        boolean inquote = false;

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '"') {
                inquote = !inquote;
            }

            if (chars[i] == '\n' && inquote) {
                buffer.append("<br>");
            } else {
                buffer.append(chars[i]);
            }
        }

        return buffer.toString();
    }

    private static List<String> getTableData(String str){
        List<String> data = new ArrayList<String>();
        
        boolean inquote = false;
        StringBuilder buffer = new StringBuilder();
        char[] chars = str.toCharArray();
        
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '"') {
                inquote = !inquote;
                continue;
            }
            
            if(chars[i] == ','){    
                if(inquote){
                    buffer.append(chars[i]);                   
                } else {
                    data.add(buffer.toString());
                    buffer.delete(0, buffer.length());
                }
            } else {
                buffer.append(chars[i]);             
            }
            
        }
        
        data.add(buffer.toString().trim());
        
        return data;
    }
    
    private static boolean checkIfEmptyRow(List<String> rowData){
           
        for(String td : rowData){
            if(!td.isEmpty()){
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * From: http://stackoverflow.com/questions/11969840/how-to-convert-a-base-10-number-to-alphabetic-like-ordered-list-in-html
     * Converts an integer to alphabetical form (base26)
     * 1 - a
     * 2 - b
     * ...
     * 26 - z
     * 27 - aa
     * 28 - ab
     * ...
     * 
     * @param n - number to convert
     */
    public static String integerToLowerCaseAlphabeticalIndex(int n) {
        String result = "";
        while (n > 0) {
            n--; // 1 => a, not 0 => a
            int remainder = n % 26;
            char digit = (char) (remainder + 97);
            result = digit + result;
            n = (n - remainder) / 26;
        }
        return result;
    }
    
    /**
     * Trim the given string if it is not equals to null
     */
    public static String trimIfNotNull(String untrimmedString){
        if(untrimmedString != null){
            return untrimmedString.trim();
        }
        return untrimmedString;
    }
}
