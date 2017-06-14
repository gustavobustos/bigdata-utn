package ar.bigdata.analisis.util;

import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtil {
	
	private static final Logger log = LoggerFactory.getLogger(StringUtil.class);
	
	private static final String UTF_8 = "UTF-8";

	public static String removeEmojisAndOtherChars (String text) {
		
		//log.info("Before: " + text);
		
		String utf8tweet = Normalizer.normalize(text, Normalizer.Form.NFD);
		utf8tweet = utf8tweet.replaceAll("\\p{M}", "");
		utf8tweet = utf8tweet.replace("\n", "").replace("\r", "");
		
		try {
            byte[] utf8Bytes = utf8tweet.getBytes(UTF_8);
            utf8tweet = new String(utf8Bytes, UTF_8);

        } catch (UnsupportedEncodingException e) {
            log.error("Error decoding ... ", e);
        }
        Pattern unicodeOutliers = Pattern.compile("[^\\x00-\\x7F]", Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE);
        Matcher unicodeOutlierMatcher = unicodeOutliers.matcher(utf8tweet);

        utf8tweet = unicodeOutlierMatcher.replaceAll("");
        
        //log.info("After: " + utf8tweet);
        
        return utf8tweet;
	}
	
}
