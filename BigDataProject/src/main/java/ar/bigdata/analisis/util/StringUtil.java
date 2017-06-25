package ar.bigdata.analisis.util;

import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtil {
	
	private static final Logger log = LoggerFactory.getLogger(StringUtil.class);
	
	private static final String UTF_8 = "UTF-8";

	public static String removeEmojisAndOtherChars (String text) {
		
		log.info("Before: " + text);
		
		String utf8tweet = Normalizer.normalize(text, Normalizer.Form.NFD);
		//utf8tweet = utf8tweet.replaceAll("\\p{M}", "");
		utf8tweet = utf8tweet.replace("\n", " ").replace("\r", " ");
		
		try {
            byte[] utf8Bytes = utf8tweet.getBytes(UTF_8);
            utf8tweet = new String(utf8Bytes, UTF_8);

        } catch (UnsupportedEncodingException e) {
            log.error("Error decoding ... ", e);
        }

        utf8tweet = cleanString("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE, utf8tweet);
        utf8tweet = cleanString("RT @[A-Za-z0-9:]+", utf8tweet);
        utf8tweet = cleanString("@[A-Za-z0-9:]+", utf8tweet);
        utf8tweet = cleanString("#", utf8tweet);
        
        utf8tweet = substring("http", utf8tweet);
        utf8tweet = substring("|", utf8tweet);
        
        utf8tweet = utf8tweet.replaceAll("_", "");
        utf8tweet = utf8tweet.replaceAll(" d ", " de ");
        utf8tweet = utf8tweet.replaceAll(" q ", " que ");
        
        log.info("After: " + utf8tweet.trim());
        
        return utf8tweet;
	}
	
	public static String substring (String prefix, String text) {
		String newText =  "";
		if (text != null && text.indexOf(prefix) > 0) {

			newText = text.substring(0, text.indexOf(prefix));	
		} else {
			newText = text;
		}
		
		return newText;
	}
	
	public static String cleanString(String regex, int flags, String text) {
		
		String newText = "";
		Pattern unicodeOutliers = Pattern.compile(regex, flags);
        Matcher unicodeOutlierMatcher = unicodeOutliers.matcher(text);
        newText = unicodeOutlierMatcher.replaceAll("");
		return newText;
	}
	
	public static String cleanString(String regex,String text) {
		String newText = "";
		Pattern unicodeOutliers = Pattern.compile(regex);
        Matcher unicodeOutlierMatcher = unicodeOutliers.matcher(text);
        newText = unicodeOutlierMatcher.replaceAll("");
		return newText;
	}
	
}
