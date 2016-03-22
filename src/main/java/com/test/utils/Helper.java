package com.test.utils;

import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Helper
 *
 * @author Kontcur Alex (bona)
 * @since 12.07.11
 */
@SuppressWarnings({"ClassWithTooManyMethods", "HardcodedLineSeparator", "MagicNumber", "UnusedDeclaration", "OverlyComplexClass", "PublicStaticArrayField"})
public class Helper {

    public static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    public static final boolean isWindows = OS_NAME.contains("win");

    public static final char[] ALL_SYMBOLS = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    public static final char[] ALL_LOWER_SYMBOLS = {
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
    };

    public static final char[] ALL_UPPER_SYMBOLS = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    public static final char[] ALL_UPPER_LOWER_SYMBOLS = {
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };


    public static final char[] DIGITS = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    };

    public static final String EMPTY_STRING = "";
    public static final String RET = "\n";
    public static final String ERROR_CAPTION = "Error";
    public static final String DOT = ".";

    private static final Random random = new SecureRandom();

    private Helper() {
    }

    public static String getTokenValue(String content, String token, String lastBound) {
        if (hasValue(content)) {
            String[] parts = content.split(token);
            return parts.length > 1 ? parts[1].split(lastBound)[0].trim() : "";
        } else {
            return "";
        }
    }

    public static boolean hasValue(String value) {
        return value != null && !value.isEmpty();
    }

    public static String randomReplaceChars(String source, char... chars) {
        int len = source.length();
        if (len <= 0) {
            return source;
        }
        int replacedCount = random.nextInt(len);
        Map<Integer, Character> replacements = new HashMap<>();
        for (int i = 0; i < replacedCount; i++) {
            replacements.put(random.nextInt(len), chars[random.nextInt(chars.length)]);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            Character character = replacements.get(i);
            sb.append(character == null ? source.charAt(i) : character);
        }
        return sb.toString();
    }

    public static String generateADOTR() {
        return "adOtr=" + generate(5, null, ALL_SYMBOLS);
    }

    public static String generate(int count, String escapeFirst, char... array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            char c = array[random.nextInt(array.length)];
            if (i == 0 && hasValue(escapeFirst)) {
                while (c == escapeFirst.charAt(0)) {
                    c = array[random.nextInt(array.length)];
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException ignored) {
            return s;
        }
    }

    public static String generateDigits(int count, String escapeFirst) {
        return generate(count, escapeFirst, DIGITS);
    }

    public static byte[] copyBytes(byte[] source) {
        if (source == null) {
            return null;
        }
        byte[] copy = new byte[source.length];
        System.arraycopy(source, 0, copy, 0, source.length);
        return copy;
    }

    public static String newString(byte[] bytes, String encoding) {
        try {
            return new String(bytes, encoding);
        } catch (UnsupportedEncodingException ignored) {
            return new String(bytes);
        }
    }

    public static byte[] getBytes(String value, String charset) {
        byte[] bytes = null;
        try {
            bytes = value.getBytes(charset);
        } catch (UnsupportedEncodingException ignored) {
        }
        return bytes;
    }

    public static boolean sleep(TimeUnit timeUnit, long delay) {
        try {
            timeUnit.sleep(delay);
        } catch (InterruptedException ignored) {
            return true;
        }
        return false;
    }

    public static InputStream getResourceAsStream(String location) {
        return Helper.class.getResourceAsStream(location);
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmptyTrimed(String value) {
        return isEmpty(value) || value.trim().isEmpty();
    }

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    @SuppressWarnings("RedundantCast")
    public static <T> List<T> safeList(List<T> list) {
        return list == null ? (List<T>) Collections.emptyList() : list;
    }

    @SuppressWarnings("RedundantCast")
    public static <T> Set<T> safeSet(Set<T> set) {
        return set == null ? (Set<T>) Collections.emptySet() : set;
    }

    public static <T> List<T> copyList(List<T> list) {
        return list == null ? new ArrayList<T>() : new ArrayList<>(list);
    }

    @SuppressWarnings("RedundantCast")
    public static <T> Collection<T> safeCollection(Collection<T> collection) {
        return collection == null ? (Collection<T>) Collections.emptyList() : collection;
    }

    // Replace all specific substring.
    public static void multiReplace(StringBuffer fbuffer, String source, String dest) {
        if ((fbuffer == null) || isEmpty(source) || (dest == null)) {
            return;
        }
        int index = -1;
        while ((index = fbuffer.indexOf(source, index + 1)) >= 0) {
            fbuffer.replace(index, index + source.length(), dest);
        }
    }

    //Replace all specific substring.
    public static String multiReplace(String str, String source, String dest) {
        StringBuffer buff = new StringBuffer(str);
        multiReplace(buff, source, dest);
        return buff.toString();
    }

    public static String streamToString(InputStream in, String charset) throws IOException {
        StringBuilder result = new StringBuilder();
        try (BufferedReader input = new BufferedReader(new InputStreamReader(in, charset))) {
            String str;
            while ((str = input.readLine()) != null) {
                result.append(str);
                result.append("\r\n");
            }
        }
        return result.toString();
    }

    public static String nbsp(String s) {
        return s.replaceAll(" ", "&nbsp;");
    }

    @SuppressWarnings("ImplicitNumericConversion")
    public static int getSkipSizeFromLine(File file, int lineNumber, char skipToSymbol) {
        if (lineNumber < 1) {
            throw new IllegalArgumentException("lineNumber must be >= 1");
        }
        int lineSize = 0;
        try {
            try (BufferedReader r = new BufferedReader(new FileReader(file))) {
                int skipCount = 0;
                int i = 1;
                String line = r.readLine();
                while (line != null) {
                    if (i == lineNumber) {
                        break;
                    }
                    skipCount += line.length() + 2;
                    line = r.readLine();
                    i++;
                }
                if (line == null) {
                    throw new IllegalArgumentException("File " + file.getAbsolutePath() + "is empty");
                }

                for (int j = 0; j < line.length(); j++) {
                    char c = line.charAt(j);
                    if (c == skipToSymbol) {
                        return skipCount + j;
                    }
                }
            }
        } catch (IOException ignored) {
        }
        return 0;
    }

    public static byte[] readBytes(File file, int skipCount, int shift) throws IOException {
        byte[] bytes;
        try (FileInputStream is = new FileInputStream(file)) {
            FileChannel src = is.getChannel();
            bytes = new byte[is.available() - skipCount - shift];
            src = src.position(skipCount + shift);
            src.read(ByteBuffer.wrap(bytes));
        }
        return bytes;
    }

    public static void writeBytes(File file, byte[] bytes) throws IOException {
        try (FileOutputStream os = new FileOutputStream(file)) {
            FileChannel dst = os.getChannel();
            dst.write(ByteBuffer.wrap(bytes));
        }
    }

    // Replaces all spaces to <pre>&nbsp</pre>;.
    public static String escape(String s) {
        String out = s.replaceAll(" ", "&nbsp;");
        out = out.replaceAll("<", "&lt;");
        out = out.replaceAll(">", "&gt;");
        out = out.replaceAll("\\n", "<br>");
        return out;
    }

    //Returns stack trace for throable as string.
    public static String getStackTrace(Throwable throable) {
        StringBuilder buffer = new StringBuilder();
        StackTraceElement[] trace = throable.getStackTrace();

        for (StackTraceElement stackTraceElement : trace) {
            buffer.append("[").append(stackTraceElement.toString()).append("]");
        }

        return buffer.toString();
    }

    //Returns stack trace for throable as string.
    public static String getFormattedStackTrace(Throwable throable, String lineBreak) {
        StringBuilder buffer = new StringBuilder();
        StackTraceElement[] trace = throable.getStackTrace();
        for (StackTraceElement stackTraceElement : trace) {
            buffer.append("[").append(stackTraceElement.toString()).append("]").append(lineBreak);
        }
        return buffer.toString();
    }

    //Cuts a long string to smaller string with additional ellipsis.
    public static String fixLongString(String s, int max) {
        if (s != null && s.length() > max) {
            return s.substring(0, max) + "...";
        }
        return s;
    }

    //Divides a long string into parts and assembles them over "\n" as delimiter.
    @SuppressWarnings("AssignmentToForLoopParameter")
    public static String divideLongString(String s, int partMaxLeght) {
        StringBuilder sb = new StringBuilder();
        char[] chars = s.toCharArray();
        int partLength = 0;
        int lines = 0;
        int length = chars.length;
        for (int i = 0; i < length; i++) {
            char aChar = chars[i];
            sb.append(aChar);
            partLength++;
            if (aChar == '\n') {
                lines++;
                partLength = 0;
                if (lines > 40) {
                    sb.append("\n...");
                    break;
                }
                continue;
            }
            if (partLength > partMaxLeght &&
                (aChar == ' ' || aChar == ';' || aChar == '.' || aChar == ':')) {
                lines++;
                if (lines > 40) {
                    sb.append("\n...");
                    break;
                }
                sb.append("\n");
                partLength = 0;
                if (i + 1 < chars.length && chars[i + 1] == ' ') {
                    i++;
                }
            }
        }
        return sb.toString();
    }

    //Packed decimal conversion.
    //@see {http://forum.java.sun.com/thread.jspa?threadID=450146&messageID=2043372}
    public static int showPacked(byte[] pd) {
        int k = 0;
        int u = 0;
        for (int i = 0; i < pd.length; i++) {
            // split byte into 2 nibbles
            int n = pd[i] & 0xff;
            int h = n >> 4;
            int l = n & 0x0f;
            // nibbles over 9n are not packed format
            if (h > 9) {
                return u; // done if not packed decimal
            }
            // nibbles nc, nd and nf are end of field
            if (l > 9) {
                if (l != 12 && l != 13 && l != 15) {
                    return u; // sign is c d or f
                }
                //k = (k * 10 + h) * ((l == 13) ? -1 : 1);
                k = 0;
                u = i + 1;
            } else {
                k = k * 100 + h * 10 + l; // normal case
            }
        }
        return u;
    }

    // Convert a byte[] array to readable string format. This makes the "hex" readable.
    // @see {http://www.devx.com/tips/Tip/13540}
    public static String byteArrayToHexString(byte[] in) {
        return byteArrayToHexString(in, 0, in.length);
    }

    //Convert a byte[] array to readable string format. This makes the "hex" readable.
    //@see {http://www.devx.com/tips/Tip/13540}
    @SuppressWarnings({"AssignmentReplaceableWithOperatorAssignment", "ReuseOfLocalVariable"})
    public static String byteArrayToHexString(byte[] in, int index, int length) {
        int i = index;
        if (in == null || in.length <= 0) {
            return null;
        }
        String[] pseudo = {"0", "1", "2",
            "3", "4", "5", "6", "7", "8",
            "9", "A", "B", "C", "D", "E",
            "F"};

        StringBuilder out = new StringBuilder(in.length * 2);
        while (i < index + length) {
            byte ch = (byte) (in[i] & 0xF0); // Strip off high nibble
            ch = (byte) (ch >>> 4);
            // shift the bits down
            ch = (byte) (ch & 0x0F);
            // must do this is high order bit is on!
            out.append(pseudo[ch]); // convert the nibble to a String Character
            ch = (byte) (in[i] & 0x0F); // Strip off low nibble
            out.append(pseudo[ch]); // convert the nibble to a String Character
            i++;
        }
        return new String(out);
    }

    /**
     * Convert a hex string to a byte array. Permits upper or lower case hex. http://mindprod.com/jgloss/hex.html
     *
     * @param s String must have even number of characters. and be formed only of digits 0-9 A-F or a-f. No spaces,
     *          minus or plus signs.
     *
     * @return corresponding byte array.
     */
    public static byte[] bytesFromHexString(String s) {
        int stringLength = s.length();
        if ((stringLength & 0x1) != 0) {
            throw new IllegalArgumentException("bytesFromHexString requires an even number of hex characters");
        }
        byte[] b = new byte[stringLength / 2];

        int j = 0;
        for (int i = 0; i < stringLength; i += 2, j++) {
            int high = charToNibble(s.charAt(i));
            int low = charToNibble(s.charAt(i + 1));
            b[j] = (byte) ((high << 4) | low);
        }
        return b;
    }

    /**
     * Convert a single char to corresponding nibble.
     *
     * @param c char to convert. must be 0-9 a-f A-F, no spaces, plus or minus signs.
     *
     * @return corresponding integer
     */
    @SuppressWarnings("IfStatementWithTooManyBranches")
    private static int charToNibble(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        } else if (c >= 'a' && c <= 'f') {
            return c - 'a' + 0xa;
        } else if (c >= 'A' && c <= 'F') {
            return c - 'A' + 0xa;
        } else {
            throw new IllegalArgumentException("Invalid hex character: " + c);
        }
    }

    public static boolean maskMathces(String value, String sample, boolean mask) {
        String sample1 = sample;
        if (value == null || sample1 == null) {
            return false;
        }
        if (mask) {
            sample1 = sample1.replace("_", ".");
            sample1 = sample1.replace("%", ".*");
        }
        return value.matches(sample1);
    }

    public static String arrayToString(String... arr) {
        StringBuilder sb = new StringBuilder("");
        if (arr != null) {
            for (int i = 0; i < arr.length; i++) {
                sb.append("<html><b>[");
                sb.append(arr[i]);
                sb.append("]");
                if (i < arr.length - 1) {
                    sb.append(", </b>");
                }
            }
        }
        return sb.toString();
    }

    public static byte[] asByteArray(long value) {
        byte[] rv = new byte[8];
        for (int x = 7; x >= 0; x--)    // big endian
        //    for ( int x = 0; x < 8; x++ )    // little endian
        {
            long temp = value & 0xFF;
            if (temp > 127) {
                temp -= 256;
            }
            rv[x] = (byte) temp;
            value >>= 8;
        }
        return rv;
    }

    // convert a byte sequence into a number
    // offset - start position in array
    // length - number of bytes to convert
    public static long byteArrayToNumber(byte[] array, int offset, int length) {
        long rv = 0;
        for (int x = 0; x < length; x++) {
            long bv = array[offset + x];
            if (x > 0 && bv < 0) {
                bv += 256;
            }
            rv *= 256;
            rv += bv;
        }
        return rv;
    }

    public static <T> List<T> iteratorToList(Iterator<T> iterator) {
        ArrayList<T> list = new ArrayList<>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

}
