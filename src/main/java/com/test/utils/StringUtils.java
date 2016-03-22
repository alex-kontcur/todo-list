package com.test.utils;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * StringUtils
 *
 * @author Kontcur Alex (bona)
 * @since 12.07.11
 */
@SuppressWarnings ("HardcodedLineSeparator")
public class StringUtils {

    private static final String GUI_RU_ENCODING = "cp1251";

    private StringUtils() {
    }

    public static String concatinateWith(String delimeter, String... values) {
        StringBuilder sb = new StringBuilder();
        int num = 0;
        for (String value : values) {
            if (hasValue(value)) {
                if (num++ > 0) {
                    sb.append(delimeter).append(value);
                } else {
                    sb.append(value);
                }
            }
        }
        return sb.toString();
    }

    public static byte[] getBytes(String source, String charset) {
        try {
            return source.getBytes(charset);
        } catch (UnsupportedEncodingException ignored) {
            return source.getBytes();
        }
    }

    @SuppressWarnings("NestedConditionalExpression")
    public static String replaceValue(String source, String delimeter, String key, String value, boolean attach) {
        String src = source.trim();
        boolean endsWithDelimeter = src.trim().endsWith(delimeter);
        String[] parts = src.split(delimeter);
        StringBuilder sb = new StringBuilder();
        int i = 0;
        boolean attached = false;
        int length = parts.length;
        for (String part : parts) {
            String partValue = part;
            if (part.contains(key)) {
                String[] kv = part.split("=");
                partValue = kv[0] + "=" + value;
                attached = true;
            }
            sb.append(partValue).append(endsWithDelimeter ? delimeter : i++ < length - 1 ? delimeter : "");
        }
        if (attach && !attached) {
            sb.append(endsWithDelimeter ? "" : delimeter).append(" ").append(key).append("=").append(value);
        }
        return sb.toString();
    }

    public static DocumentBuilder createDocumentBuilder() {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setValidating(false);
            documentBuilderFactory.setCoalescing(true);
            documentBuilderFactory.setExpandEntityReferences(true);
            documentBuilderFactory.setIgnoringComments(true);
            documentBuilderFactory.setIgnoringElementContentWhitespace(true);
            documentBuilderFactory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd",
                false);
            return documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ignored) {
            return null;
        }
    }

    public static Document getDocument(String value) {
        try {
            return createDocumentBuilder().parse(new InputSource(new StringReader(value)));
        } catch (Exception ignored) {
        }
        return null;
    }

    public static List<String> readFile(String path, String encoding) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), encoding))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                lines.add(line);
            }
        }
        return lines;
    }


    public static void addURLToClassPath(URL url) throws IOException {
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] {URL.class});
            method.setAccessible(true);
            method.invoke(sysloader, url);
        } catch (Throwable ignored) {
            throw new IOException("Error, could not add URL to system classloader");
        }
    }

    public static File writeImage(byte[] bytes, String fileName, String ext) throws IOException {
        File file = new File(fileName);
        InputStream in = new ByteArrayInputStream(bytes);
        BufferedImage image = ImageIO.read(in);
        ImageIO.write(image, ext, file); // example ext = png
        return file;
    }

    @SafeVarargs
    public static <T> List<T> getValuesList(T... values) {
        List<T> list = new ArrayList<>();
        list.addAll(Arrays.asList(values));
        return list;
    }

    public static String translate(String value) {
        //try {
        //    return new String(value.getBytes(), GUI_RU_ENCODING);
        //} catch (UnsupportedEncodingException ignored) {
        //    return value;
        //}

        return value;
    }

    public static String translate(String value, String encoding) {
        try {
            return new String(value.getBytes(), encoding);
        } catch (UnsupportedEncodingException ignored) {
            return value;
        }
    }

    public static String encodeWin(String value) {
        try {
            return new String(value.getBytes(GUI_RU_ENCODING));
        } catch (UnsupportedEncodingException ignored) {
            return value;
        }
    }

    public static String encode(String value, String encoding) {
        try {
            return new String(value.getBytes(encoding));
        } catch (UnsupportedEncodingException ignored) {
            return value;
        }
    }

    public static boolean noValue(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean hasValue(String value) {
        return value != null && !value.isEmpty();
    }

    public static String fromDefaultStream(InputStream is) throws IOException {
        if (is == null) {
            return "";
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(is))) {
            return fromReader(in);
        }
    }

    public static List<String> getLinesFromFile(String path) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                lines.add(line);
            }
        }
        return lines;
    }

    public static String fromFile(String path, String charset) {
        try {
            try (FileInputStream is = new FileInputStream(path)) {
                return fromCharsetStream(is, charset);
            }
        } catch (IOException ignored) {
        }
        return "";
    }

    private static String fromReader(BufferedReader reader, int bytesCount) throws IOException {
        StringBuilder sb = new StringBuilder("");
        char[] buffer = new char[bytesCount];
        int cnt = reader.read(buffer);
        while (cnt > 0) {
            sb.append(new String(buffer));
            cnt = reader.read(buffer);
        }
        return sb.toString();
    }

    private static String fromReader(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder("");
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            sb.append(line);
        }
        return sb.toString();
    }

    public static String fromCharsetStream(InputStream is, String charSetName) throws IOException {
        if (is == null) {
            return "";
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(is, charSetName))) {
            return fromReader(in);
        }
    }

    @SuppressWarnings ("IOResourceOpenedButNotSafelyClosed")
    public static String fromBytes(byte[] bytes, String charSetName, boolean gzip) throws IOException {
        InputStream is = gzip ? new GZIPInputStream(new ByteArrayInputStream(bytes)) : new ByteArrayInputStream(bytes);
        return fromCharsetStream(is, charSetName);
    }

    public static String domRawString(Document doc) throws TransformerException {
        Source source = new DOMSource(doc);
        StringWriter stringWriter = new StringWriter();
        Result result = new StreamResult(stringWriter);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.transform(source, result);
        return stringWriter.getBuffer().toString();
    }

    public static void insertToFile(String filename, String value, String insertBefore) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if (line.trim().equalsIgnoreCase(insertBefore.trim())) {
                    sb.append(value).append("\r\n");
                }
                sb.append(line).append("\r\n");
            }
        }
        try (FileWriter fw = new FileWriter(filename)) {
            fw.write(sb.toString());
        }
    }

    public static void toFile(String fileName, String value, Logger logger) {
        try {
            toFile(fileName, value);
        } catch (IOException e) {
            logger.error("Error", e);
        }
    }

    public static void toFile(String fileName, String value) throws IOException {
        try (PrintWriter pw = new PrintWriter(fileName)) {
            pw.println(value);
        }
    }

    public static String sliceData(String data, int sliceLength) {
        if (sliceLength < 1) {
            return data;
        }
        int start = 0;
        StringBuilder s = new StringBuilder();
        String slice;
        do {
            int len = (start + 1) * sliceLength;
            len = len > data.length() ? data.length() : len;
            slice = data.substring(start * sliceLength, len);
            s.append(slice);

            start++;

            if (start * sliceLength >= data.length()) {
                break;
            } else {
                s.append("\r\n");
            }

        } while (slice != null);

        return s.toString();
    }

}
