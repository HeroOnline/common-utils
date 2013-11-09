package allen.utils;

import org.apache.commons.lang.CharSet;

/**
 * 
 * 
 * @author zhengxu
 * @created 2013-7-9
 * 
 * @version 1.0
 */
public class JSONUtils {

    /**
     * references: http://kb.mozillazine.org/Network.IDN.blacklist_chars
     * 
     * except
     * <ul>
     * <li>Space</li>
     * <li>\u00A0(NO-BREAK SPACE)</li>
     * <li>\u02D0(long-vowel and geminate-consonant mark)</li>
     * <li>\u2000-\u200B</li>
     * <li>\u2027(HYPHENATION POINT)</li>
     * <li>\u2039('‹',SINGLE LEFT-POINTING ANGLE QUOTATION MARK)</li>
     * <li>\u203A('›',SINGLE RIGHT-POINTING ANGLE QUOTATION MARK)</li>
     * <li>\u2153-\u2afd</li>
     * <li>\u3000(IDEOGRAPHIC SPACE)</li>
     * <li>\u3002('。')</li>
     * <li>\u3014('〔', LEFT TORTOISE SHELL BRACKET)</li>
     * <li>\u3015('〕', RIGHT TORTOISE SHELL BRACKET)</li>
     * <li>\ua789</li>
     * <li>\ufe14-\ufe15</li>
     * <li>\ufe3f(PRESENTATION FORM FOR VERTICAL LEFT ANGLE BRACKET)</li>
     * <li>\uFE5D('﹝', SMALL LEFT TORTOISE SHELL BRACKET)</li>
     * <li>\uFE5E('﹞', SMALL RIGHT TORTOISE SHELL BRACKET)</li>
     * <li>\uff0e-\uff0f</li>
     * <li>\uff61</li>
     */

    /**
     * 
     */
    /*
     * public static CharSet BLACKLIST = CharSet
     * .getInstance(
     * "\u00BC\u00BD\u00BE\u01C3\u02D0\u0337\u0338\u0589\u05C3\u05F4\u0609\u060A\u066A\u06D4\u0701\u0702\u0703\u0704\u115F\u1160\u1735\u2024\u2028\u2029\u202F\u2041\u2044\u2052\u205F\u2FF0\u2FF1\u2FF2\u2FF3\u2FF4\u2FF5\u2FF6\u2FF7\u2FF8\u2FF9\u2FFA\u2FFB\u3033\u3164\u321D\u321E\u33AE\u33AF\u33C6\u33DF\uFEFF\uFFA0\uFFF9\uFFFA\uFFFB\uFFFC\uFFFD"
     * );
     */
    /**
     * tested against chrome
     */
    public static CharSet BLACKLIST = CharSet
            .getInstance("\u2028\u2029\uFFF9\uFFFA\uFFFB\uFFFC\uFFFD");

    // references: http://www.ietf.org/rfc/rfc4627.txt?number=4627
    /**
     * <pre>
     * string = quotation-mark *char quotation-mark
     * 
     *          char = unescaped /
     *                 escape (
     *                     %x22 /          ; "    quotation mark  U+0022
     *                     %x5C /          ; \    reverse solidus U+005C
     *                     %x2F /          ; /    solidus         U+002F
     *                     %x62 /          ; b    backspace       U+0008
     *                     %x66 /          ; f    form feed       U+000C
     *                     %x6E /          ; n    line feed       U+000A
     *                     %x72 /          ; r    carriage return U+000D
     *                     %x74 /          ; t    tab             U+0009
     *                     %x75 4HEXDIG )  ; uXXXX                U+XXXX
     * 
     *          escape = %x5C              ; \
     * 
     *          quotation-mark = %x22      ; "
     * unescaped = %x20-21 / %x23-5B / %x5D-10FFFF
     * 
     * <pre>
     */
    public static String filterOutIllegalCharacter(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = s.length(); i < length; i++) {
            char ch = s.charAt(i);
            if ((ch >= '\u0000' && ch <= '\u001F' && ch != '\n' && ch != '\t')
                    || (ch >= '\u007F' && ch <= '\u009F')
                    || BLACKLIST.contains(ch)
            /* || (ch >= '\u2000' && ch <= '\u20FF') */) {
                // ignore
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}