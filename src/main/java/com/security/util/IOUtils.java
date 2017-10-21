package com.security.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * 有关输入输出流以及各种流之间转换的工具类;
 */
public class IOUtils {

    /**
     * The Windows line separator string.
     */
    public static final String LINE_SEPARATOR_WINDOWS = "\r\n";

    /**
     *行分隔符;
     */
    public static final String LINE_SEPARATOR;

    static {
        final StringBuilderWriter buf = new StringBuilderWriter(4);
        final PrintWriter out = new PrintWriter(buf);
        out.println();
        LINE_SEPARATOR = buf.toString();
        out.close();
    }

    /**
     * 代表文件结束;
     * Represents the end-of-file (or stream).
     */
    public static final int EOF = -1;

    /**
     * 默认缓冲区大小;
     * The default buffer size
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    /**
     * 生成随机汉字
     * http://www.cnblogs.com/skyivben/archive/2012/10/20/2732484.html
     * @return :返回随机生成的汉字;
     */
    public static char generateRandomChar() {
        String str = "";
        int hightPos;
        int lowPos;
        Random random = new Random();
        hightPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = (161 + Math.abs(random.nextInt(93)));
        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(hightPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();
        try {
            str = new String(b,"GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str.charAt(0);
    }

    /**
     * 关闭URLConnection连接.
     * @param conn the connection to close.
     */
    public static void closeConnection(URLConnection conn) {
        if (conn instanceof HttpURLConnection) {
            ((HttpURLConnection) conn).disconnect();
        }
    }

    /**
     * 关闭Reader流;
     */
    public static void closeQuietly(Reader input) {
        closeQuietly((Closeable) input);
    }

    /**
     * 关闭Writer流;
     * @param output the Writer to close, may be null or already closed
     */
    public static void closeQuietly(Writer output) {
        closeQuietly((Closeable) output);
    }

    /**
     * 关闭InputStream;
     * @param input the InputStream to close, may be null or already closed
     */
    public static void closeQuietly(InputStream input) {
        closeQuietly((Closeable) input);
    }

    /**
     * 关闭OutputStream;
     * @param output the OutputStream to close, may be null or already closed
     */
    public static void closeQuietly(OutputStream output) {
        closeQuietly((Closeable) output);
    }

    /**
     * InputStrean、outputStream均为Closeable的子类;
     * @param closeable the objects to close, may be null or already closed
     */
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException ioe) {
            // ignore
        }
    }

    /*
     *关闭多个输入输出流;
     */
    public static void closeQuietly(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (final Closeable closeable : closeables) {
            closeQuietly(closeable);
        }
    }


    /**************************读取byte[]字节流**************************/

    /**
     * 将InputStream转化为byte[]数组;
     * Gets the contents of an <code>InputStream</code> as a <code>byte[]</code>.
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    /**
     * 将InputStream中的内容复制到OutputStream中;
     * Copies bytes from an <code>InputStream</code> to an
     * <code>OutputStream</code>.
     */
    public static int copy(InputStream input,OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    /**
     * 将大容量的字节流(可能超过2GB)的输入流InputStream转化为输入流Outputstream;
     * Copies bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>.
     */
    public static long copyLarge(InputStream input,OutputStream output)
            throws IOException {
        return copy(input, output, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 根据提供的默认的缓冲区大小,将输入流InputStream转化为输入流Outputstream;
     * Copies bytes from an <code>InputStream</code> to an <code>OutputStream</code> using an internal buffer of the
     * given size.
     */
    public static long copy(InputStream input,OutputStream output,int bufferSize)
            throws IOException {
        return copyLarge(input, output, new byte[bufferSize]);
    }

    /**
     * 利用buffer将大容量的字节流(可能超过2GB)的输入流InputStream转化为输入流Outputstream;
     * Copies bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>.
     */
    public static long copyLarge(InputStream input,OutputStream output,byte[] buffer)
            throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * 将Reader中的内容转化为byte[]数组;
     * Gets the contents of a <code>Reader</code> as a <code>byte[]</code>
     */
    @Deprecated
    public static byte[] toByteArray(Reader input) throws IOException {
        return toByteArray(input, Charset.defaultCharset());
    }

    /**
     * 根据编码格式将Reader中的内容转化为byte[]数组;
     * Gets the contents of a <code>Reader</code> as a <code>byte[]</code>
     */
    public static byte[] toByteArray(Reader input,Charset encoding) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output, encoding);
        return output.toByteArray();
    }

    /**
     * 根据编码格式，将Reader中的内容转化为byte[]数组;
     * Gets the contents of a <code>Reader</code> as a <code>byte[]</code>
     */
    public static byte[] toByteArray(Reader input,String encoding) throws IOException {
        return toByteArray(input, Charsets.toCharset(encoding));
    }

    /**
     * 根据编码格式，将Reader中的内如复制入OutputStreamWriter中；
     * Copies chars from a <code>Reader</code> to bytes on an OutputStreamWriter
     */
    public static void copy(final Reader input, final OutputStream output, final Charset outputEncoding)
            throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(output, Charsets.toCharset(outputEncoding));
        copy(input, out);
        out.flush();
    }

    /**
     * 将字符串转化为byte[]数组;
     * Gets the contents of a <code>String</code> as a <code>byte[]</code>
     */
    @Deprecated
    public static byte[] toByteArray(String input) throws IOException {
        return input.getBytes(Charset.defaultCharset());
    }

    /**
     * 将InputStream中的内容复制入Writer中;
     * Copies bytes from an <code>InputStream</code> to chars on a
     * <code>Writer</code> using the default character encoding of the platform.
     */
    @Deprecated
    public static void copy(InputStream input,Writer output)
            throws IOException {
        copy(input, output, Charset.defaultCharset());
    }


    /**
     * 根据字符编码格式将InputStream中的字节内容复制入Writer中;
     * Copies bytes from an <code>InputStream</code> to chars on a
     * <code>Writer</code> using the specified character encoding.
     */
    public static void copy(InputStream input,Writer output,Charset inputEncoding)
            throws IOException {
        InputStreamReader in = new InputStreamReader(input, Charsets.toCharset(inputEncoding));
        copy(in, output);
    }

    /**
     * 根据特定的字符编码格式将InputStream转化为Writer;
     * Copies bytes from an <code>InputStream</code> to chars on a
     */
    public static void copy(InputStream input,Writer output,String inputEncoding)
            throws IOException {
        copy(input, output, Charsets.toCharset(inputEncoding));
    }

    /**
     * 将Reader中的内容复制入Writer中;
     * Copies chars from a <code>Reader</code> to a <code>Writer</code>.
     */
    public static int copy(Reader input,Writer output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    /**
     * 将大容量(超过2GB)的字符内容从Reader转入到Writer中;
     * Copies chars from a large (over 2GB) <code>Reader</code> to a <code>Writer</code>.
     */
    public static long copyLarge(Reader input,Writer output) throws IOException {
        return copyLarge(input, output, new char[DEFAULT_BUFFER_SIZE]);
    }

    /**
     * 根据缓冲区将大容量(超过2GB)的字符内容从Reader转入到Writer中;
     * Copies chars from a large (over 2GB) <code>Reader</code> to a <code>Writer</code>.
     */
    public static long copyLarge(Reader input,Writer output,char[] buffer) throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }


    /**************************读取char[]字符流**************************/
    /**
     * 根据默认的编码格式将InputStream中的内容转化为char[]字符数组;
     * Gets the contents of an <code>InputStream</code> as a character array
     * using the default character encoding of the platform.
     */
    @Deprecated
    public static char[] toCharArray(InputStream is) throws IOException {
        return toCharArray(is, Charset.defaultCharset());
    }

    /**
     * 根据特定的编码格式将InputStream中的内容转化为char[]字符数组;
     * Gets the contents of an <code>InputStream</code> as a character array
     * using the specified character encoding.
     */
    public static char[] toCharArray(InputStream is,Charset encoding)
            throws IOException {
        CharArrayWriter output = new CharArrayWriter();
        copy(is, output, encoding);
        return output.toCharArray();
    }

    /**
     * 根据特定的编码格式将InputStream中的内容转化为char[]字符数组;
     * Gets the contents of an <code>InputStream</code> as a character array
     * using the specified character encoding.
     */
    public static char[] toCharArray(InputStream is,String encoding) throws IOException {
        return toCharArray(is, Charsets.toCharset(encoding));
    }

    /**
     * 将Reader中的内容转化为char[]字符数组
     * Gets the contents of a <code>Reader</code> as a character array.
     */
    public static char[] toCharArray(Reader input) throws IOException {
        CharArrayWriter sw = new CharArrayWriter();
        copy(input, sw);
        return sw.toCharArray();
    }

    /**************************read toString******************************/

    /**
     *将InputStream中的内容转化为String字符串;
     * Gets the contents of an <code>InputStream</code> as a String
     * using the default character encoding of the platform.
     */
    @Deprecated
    public static String toString(InputStream input) throws IOException {
        return toString(input, Charset.defaultCharset());
    }

    /**
     * 根据特定的编码格式将InputStream中的内容转化为String字符串;
     * Gets the contents of an <code>InputStream</code> as a String
     * using the specified character encoding.
     */
    public static String toString(InputStream input,Charset encoding) throws IOException {
        StringBuilderWriter sw = new StringBuilderWriter();
        copy(input, sw, encoding);
        return sw.toString();
    }

    /**
     *根据特定的编码格式将InputStream中的内容转化为String字符串;
     * Gets the contents of an <code>InputStream</code> as a String
     * using the specified character encoding.
     */
    public static String toString(InputStream input,String encoding)
            throws IOException {
        return toString(input, Charsets.toCharset(encoding));
    }

    /**
     * 将Reader中的内容转化为String字符串;
     * Gets the contents of a <code>Reader</code> as a String.
     */
    public static String toString(Reader input) throws IOException {
        StringBuilderWriter sw = new StringBuilderWriter();
        copy(input, sw);
        return sw.toString();
    }

    /**
     * Gets the contents at the given URI.
     * @param uri The URI source.
     * @return The contents of the URL as a String.
     * @throws IOException if an I/O exception occurs.
     */
    @Deprecated
    public static String toString(URI uri) throws IOException {
        return toString(uri, Charset.defaultCharset());
    }

    /**
     * 根据提供的URI获取相应的内容;
     * Gets the contents at the given URI.
     */
    public static String toString(URI uri,Charset encoding) throws IOException {
        return toString(uri.toURL(), Charsets.toCharset(encoding));
    }

    /**
     * 根据提供的URI和字符编码格式获取相应的内容;
     * Gets the contents at the given URI.
     */
    public static String toString(URI uri,String encoding) throws IOException {
        return toString(uri, Charsets.toCharset(encoding));
    }

    /**
     * 根据提供的URI获取相应的内容;
     * Gets the contents at the given URL.
     */
    @Deprecated
    public static String toString(URL url) throws IOException {
        return toString(url, Charset.defaultCharset());
    }

    /**
     * 根据提供的URL和字符编码格式获取相应的内容;
     * Gets the contents at the given URL.
     * @param url The URL source.
     * @param encoding The encoding name for the URL contents.
     * @return The contents of the URL as a String.
     * @throws IOException if an I/O exception occurs.
     */
    public static String toString(URL url,Charset encoding) throws IOException {
        InputStream inputStream = url.openStream();
        try {
            return toString(inputStream, encoding);
        } finally {
            inputStream.close();
        }
    }

    /**
     * 根据提供的URL和字符编码格式获取相应的内容;
     * Gets the contents at the given URL.
     */
    public static String toString(final URL url, final String encoding) throws IOException {
        return toString(url, Charsets.toCharset(encoding));
    }

    /**
     *将byte[]字节数组转化为String字符串;
     * Gets the contents of a <code>byte[]</code> as a String
     * using the default character encoding of the platform.
     */
    @Deprecated
    public static String toString(byte[] input) throws IOException {
        return new String(input, Charset.defaultCharset());
    }

    /**
     * 根据相应的字符编码格式将byte[]字节数组转化为String字符串;
     * Gets the contents of a <code>byte[]</code> as a String
     * using the specified character encoding.
     */
    public static String toString(byte[] input,String encoding) throws IOException {
        return new String(input, Charsets.toCharset(encoding));
    }


    /************************Read Lines************************/

    /**
     * 将InputStream中的内容按行读取入List中;
     * Gets the contents of an <code>InputStream</code> as a list of Strings
     */
    @Deprecated
    public static List<String> readLines(InputStream input) throws IOException {
        return readLines(input, Charset.defaultCharset());
    }

    /**
     *根据特定的字符编码格式将InputStream中的内容按行读取入List中;
     * Gets the contents of an <code>InputStream</code> as a list of Strings,
     * one entry per line, using the specified character encoding.
     */
    public static List<String> readLines(InputStream input,Charset encoding) throws IOException {
        InputStreamReader reader = new InputStreamReader(input, Charsets.toCharset(encoding));
        return readLines(reader);
    }

    /**
     *根据特定的字符编码格式将InputStream中的内容按行读取入List中;
     * Gets the contents of an <code>InputStream</code> as a list of Strings,
     * one entry per line, using the specified character encoding.
     */
    public static List<String> readLines(InputStream input,String encoding) throws IOException {
        return readLines(input, Charsets.toCharset(encoding));
    }

    /**
     * 将Reader中的内容按行读取入List中;
     * Gets the contents of a <code>Reader</code> as a list of Strings,
     * one entry per line.
     */
    public static List<String> readLines(Reader input) throws IOException {
        BufferedReader reader = toBufferedReader(input);
        List<String> list = new ArrayList<String>();
        String line = reader.readLine();
        while (line != null) {
            list.add(line);
            line = reader.readLine();
        }
        return list;
    }


    /**
     * 将Reader转化为BufferedReader;
     * Returns the given reader if it is a {@link BufferedReader}, otherwise creates a BufferedReader from the given
     * reader.
     */
    public static BufferedReader toBufferedReader(Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

    /***********************将CharSequence、String转化为InputStrean************************/

    /**
     * 将特定的CharSequence转化为InputStream;
     * Converts the specified CharSequence to an input stream, encoded as bytes
     * using the default character encoding of the platform.
     */
    @Deprecated
    public static InputStream toInputStream(CharSequence input) {
        return toInputStream(input, Charset.defaultCharset());
    }

    /**
     * 按照特定的编码格式将特定的CharSequence转化为InputStream;
     * Converts the specified CharSequence to an input stream, encoded as bytes
     * using the specified character encoding.
     */
    public static InputStream toInputStream(CharSequence input,Charset encoding) {
        return toInputStream(input.toString(), encoding);
    }

    /**
     *根据特定的编码格式将特定的CharSequence转化为InputStream;
     * Converts the specified CharSequence to an input stream, encoded as bytes
     * using the specified character encoding.
     */
    public static InputStream toInputStream(CharSequence input,String encoding) throws IOException {
        return toInputStream(input, Charsets.toCharset(encoding));
    }

    /**
     *将特定的String字符串转化为InputStream;
     * Converts the specified string to an input stream, encoded as bytes
     * using the default character encoding of the platform.
     */
    @Deprecated
    public static InputStream toInputStream(String input) {
        return toInputStream(input, Charset.defaultCharset());
    }

    /**
     *根据特定的编码格式将特定的String字符串转化为InputStream;
     * Converts the specified string to an input stream, encoded as bytes
     * using the specified character encoding.
     */
    public static InputStream toInputStream(String input,Charset encoding) {
        return new ByteArrayInputStream(input.getBytes(Charsets.toCharset(encoding)));
    }

    /**
     *按照特定的编码格式将特定的String字符串转化为InputStream;
     * Converts the specified string to an input stream, encoded as bytes
     * using the specified character encoding.
     */
    public static InputStream toInputStream(String input,String encoding) throws IOException {
        byte[] bytes = input.getBytes(Charsets.toCharset(encoding));
        return new ByteArrayInputStream(bytes);
    }

    /**************************将byte[]写入输出流*******************************/

    /**
     * 将字节数组byte[]写入OutputStream;
     * Writes bytes from a <code>byte[]</code> to an <code>OutputStream</code>.
     */
    public static void write(byte[] data,OutputStream output)
            throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    /**
     * Writes bytes from a <code>byte[]</code> to an <code>OutputStream</code> using chunked writes.
     * This is intended for writing very large byte arrays which might otherwise cause excessive
     * memory usage if the native code has to allocate a copy.
     */
    public static void writeChunked(byte[] data,OutputStream output)
            throws IOException {
        if (data != null) {
            int bytes = data.length;
            int offset = 0;
            while (bytes > 0) {
                int chunk = Math.min(bytes, DEFAULT_BUFFER_SIZE);
                output.write(data, offset, chunk);
                bytes -= chunk;
                offset += chunk;
            }
        }
    }

    /**
     * 将字节数组byte[]写入Writer;
     * Writes bytes from a <code>byte[]</code> to chars on a <code>Writer</code>
     * using the default character encoding of the platform.
     */
    @Deprecated
    public static void write(byte[] data,Writer output) throws IOException {
        write(data, output, Charset.defaultCharset());
    }

    /**
     *按照特定的编码格式将字节数组byte[]写入OutputStream;
     * Writes bytes from a <code>byte[]</code> to chars on a <code>Writer</code>
     * using the specified character encoding.
     */
    public static void write(byte[] data,Writer output,Charset encoding) throws IOException {
        if (data != null) {
            output.write(new String(data, Charsets.toCharset(encoding)));
        }
    }

    /**
     *根据特定的编码格式将字节数组byte[]写入OutputStream;
     * Writes bytes from a <code>byte[]</code> to chars on a <code>Writer</code>
     * using the specified character encoding.
     */
    public static void write(byte[] data,Writer output,String encoding) throws IOException {
        write(data, output, Charsets.toCharset(encoding));
    }

    /**************************将char[]写入输出流*******************************/

    /**
     * 将char[]字符数组写入Writer中;
     * Writes chars from a <code>char[]</code> to a <code>Writer</code>
     * @param data the char array to write, do not modify during output,
     */
    public static void write(char[] data,Writer output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    /**
     * Writes chars from a <code>char[]</code> to a <code>Writer</code> using chunked writes.
     * This is intended for writing very large byte arrays which might otherwise cause excessive
     * memory usage if the native code has to allocate a copy.
     */
    public static void writeChunked(char[] data,Writer output) throws IOException {
        if (data != null) {
            int bytes = data.length;
            int offset = 0;
            while (bytes > 0) {
                int chunk = Math.min(bytes, DEFAULT_BUFFER_SIZE);
                output.write(data, offset, chunk);
                bytes -= chunk;
                offset += chunk;
            }
        }
    }

    /**
     *将char[]字符数组写入OutputStream中;
     * Writes chars from a <code>char[]</code> to bytes on an
     */
    @Deprecated
    public static void write(char[] data,OutputStream output)
            throws IOException {
        write(data, output, Charset.defaultCharset());
    }

    /*
     *根据特定的编码格式将char[]字符数组写入OutputStream中;
     * Writes chars from a <code>char[]</code> to bytes on an
     * <code>OutputStream</code> using the specified character encoding.
     */
    public static void write(char[] data,OutputStream output,Charset encoding) throws IOException {
        if (data != null) {
            output.write(new String(data).getBytes(Charsets.toCharset(encoding)));
        }
    }

    /**
     *按照特定的编码格式将char[]字符数组写入OutputStream中;
     * Writes chars from a <code>char[]</code> to bytes on an
     * <code>OutputStream</code> using the specified character encoding.
     */
    public static void write(char[] data,OutputStream output,String encoding)
            throws IOException {
        write(data, output, Charsets.toCharset(encoding));
    }

    /*********************将CharSequence写入输出流**************************/

    /**
     *将CharSequence写入Writer;
     * Writes chars from a <code>CharSequence</code> to a <code>Writer</code>.
     */
    public static void write(CharSequence data,Writer output) throws IOException {
        if (data != null) {
            write(data.toString(), output);
        }
    }

    /**
     * 将CharSequence写入Outputstream;
     * Writes chars from a <code>CharSequence</code> to bytes on an
     * <code>OutputStream</code> using the default character encoding of the
     * platform.
     */
    @Deprecated
    public static void write(CharSequence data,OutputStream output)
            throws IOException {
        write(data, output, Charset.defaultCharset());
    }

    /**
     *按照特定的编码格式将CharSequence写入OutputStream;
     * Writes chars from a <code>CharSequence</code> to bytes on an
     * <code>OutputStream</code> using the specified character encoding.
     */
    public static void write(CharSequence data,OutputStream output,Charset encoding)
            throws IOException {
        if (data != null) {
            write(data.toString(), output, encoding);
        }
    }

    /**
     *依据特定的编码格式将CharSequence写入OutputStream;
     * Writes chars from a <code>CharSequence</code> to bytes on an
     * <code>OutputStream</code> using the specified character encoding.
     */
    public static void write(CharSequence data,OutputStream output,String encoding)
            throws IOException {
        write(data, output, Charsets.toCharset(encoding));
    }

    /*********************将String写入输出流**************************/

    /**
     * 将String字符串写入Writer
     * Writes chars from a <code>String</code> to a <code>Writer</code>.
     */
    public static void write(String data,Writer output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    /**
     *将String字符串写入OutputStream;
     * Writes chars from a <code>String</code> to bytes on an
     * <code>OutputStream</code> using the default character encoding of the
     * platform.
     */
    @Deprecated
    public static void write(String data,OutputStream output)
            throws IOException {
        write(data, output, Charset.defaultCharset());
    }

    /**
     *按照特定的编码格式将String字符串写入OutputStream;
     * Writes chars from a <code>String</code> to bytes on an
     * <code>OutputStream</code> using the specified character encoding.
     */
    public static void write(String data,OutputStream output,Charset encoding) throws IOException {
        if (data != null) {
            output.write(data.getBytes(Charsets.toCharset(encoding)));
        }
    }

    /**
     *依据特定的编码格式将String字符串写入OutputStream;
     * Writes chars from a <code>String</code> to bytes on an
     * <code>OutputStream</code> using the specified character encoding.
     */
    public static void write(String data,OutputStream output,String encoding)
            throws IOException {
        write(data, output, Charsets.toCharset(encoding));
    }

    /***************将StringBuffer中的内容写入输出流*****************/

    /**
     * 将StringBuffer中的内容写入Writer;
     * Writes chars from a <code>StringBuffer</code> to a <code>Writer</code>.
     */
    @Deprecated
    public static void write(StringBuffer data,Writer output)
            throws IOException {
        if (data != null) {
            output.write(data.toString());
        }
    }

    /**
     *将StringBuffer中的内容写入OutputStream;
     * Writes chars from a <code>StringBuffer</code> to bytes on an
     * <code>OutputStream</code> using the default character encoding of the
     * platform.
     */
    @Deprecated
    public static void write(StringBuffer data,OutputStream output)
            throws IOException {
        write(data, output, (String) null);
    }

    /**
     *按照特定的字符格式将StringBuffer中的内容写入OutputStream;
     * Writes chars from a <code>StringBuffer</code> to bytes on an
     * <code>OutputStream</code> using the specified character encoding.
     */
    @Deprecated
    public static void write(StringBuffer data,OutputStream output,String encoding)
            throws IOException {
        if (data != null) {
            output.write(data.toString().getBytes(Charsets.toCharset(encoding)));
        }
    }

    /**********************Writer Lines************************/

    /**
     * Writes the <code>toString()</code> value of each item in a collection to
     * an <code>OutputStream</code> line by line, using the default character
     * encoding of the platform and the specified line ending.
     * @param lines the lines to write, null entries produce blank lines
     * @param lineEnding the line separator to use, null is system default
     * @param output the <code>OutputStream</code> to write to, not null, not closed
     */
    @Deprecated
    public static void writeLines(Collection<?> lines,String lineEnding,
                                  OutputStream output) throws IOException {
        writeLines(lines, lineEnding, output, Charset.defaultCharset());
    }

    /**
     * Writes the <code>toString()</code> value of each item in a collection to
     * an <code>OutputStream</code> line by line, using the specified character
     * encoding and the specified line ending.
     */
    public static void writeLines(Collection<?> lines, String lineEnding,OutputStream output,
                                  Charset encoding) throws IOException {
        if (lines == null) {
            return;
        }
        if (lineEnding == null) {
            lineEnding = LINE_SEPARATOR;
        }
        final Charset cs = Charsets.toCharset(encoding);
        for (final Object line : lines) {
            if (line != null) {
                output.write(line.toString().getBytes(cs));
            }
            output.write(lineEnding.getBytes(cs));
        }
    }

    /**
     * Writes the <code>toString()</code> value of each item in a collection to
     * an <code>OutputStream</code> line by line, using the specified character
     * encoding and the specified line ending.
     */
    public static void writeLines(Collection<?> lines,String lineEnding,
                                  OutputStream output,String encoding) throws IOException {
        writeLines(lines, lineEnding, output, Charsets.toCharset(encoding));
    }

    /**
     * Writes the <code>toString()</code> value of each item in a collection to
     * a <code>Writer</code> line by line, using the specified line ending.
     */
    public static void writeLines(Collection<?> lines, String lineEnding,
                                  Writer writer) throws IOException {
        if (lines == null) {
            return;
        }
        if (lineEnding == null) {
            lineEnding = LINE_SEPARATOR;
        }
        for (final Object line : lines) {
            if (line != null) {
                writer.write(line.toString());
            }
            writer.write(lineEnding);
        }
    }

    /*************content equals:内容比较****************/

    /**
     * 比较两个输入流InputStream的内容是否相等;
     * Compares the contents of two Streams to determine if they are equal or not.
     * @return true if the content of the streams are equal or they both don't
     * exist, false otherwise
     */
    public static boolean contentEquals(InputStream input1, InputStream input2)
            throws IOException {
        if (input1 == input2) {
            return true;
        }
        if (!(input1 instanceof BufferedInputStream)) {
            input1 = new BufferedInputStream(input1);
        }
        if (!(input2 instanceof BufferedInputStream)) {
            input2 = new BufferedInputStream(input2);
        }

        int ch = input1.read();
        while (EOF != ch) {
            final int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
            ch = input1.read();
        }

        final int ch2 = input2.read();
        return ch2 == EOF;
    }

    /**
     * 比较两个输入流Reader的内容是否相等;
     * Compares the contents of two Readers to determine if they are equal or
     * not.
     */
    public static boolean contentEquals(Reader input1, Reader input2)
            throws IOException {
        if (input1 == input2) {
            return true;
        }

        input1 = toBufferedReader(input1);
        input2 = toBufferedReader(input2);

        int ch = input1.read();
        while (EOF != ch) {
            final int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
            ch = input1.read();
        }

        final int ch2 = input2.read();
        return ch2 == EOF;
    }

    /**
     * 比较两个Reader的内容,ignoring EOL characters;
     * Compares the contents of two Readers to determine if they are equal or
     * not, ignoring EOL characters.
     */
    public static boolean contentEqualsIgnoreEOL(Reader input1,Reader input2)
            throws IOException {
        if (input1 == input2) {
            return true;
        }
        BufferedReader br1 = toBufferedReader(input1);
        BufferedReader br2 = toBufferedReader(input2);

        String line1 = br1.readLine();
        String line2 = br2.readLine();
        while (line1 != null && line2 != null && line1.equals(line2)) {
            line1 = br1.readLine();
            line2 = br2.readLine();
        }
        return line1 == null ? line2 == null ? true : false : line1.equals(line2);
    }



}
