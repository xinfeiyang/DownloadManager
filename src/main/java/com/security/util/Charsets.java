package com.security.util;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 配合IOUtils使用;
 * Charsets required of every implementation of the Java platform.
 */
public class Charsets {

    /**
     * Constructs a sorted map from canonical charset names to charset objects required of every implementation of the
     * Java platform.
     */
    public static SortedMap<String, Charset> requiredCharsets() {
        // maybe cache?
        // TODO Re-implement on Java 7 to use java.nio.charset.StandardCharsets
        final TreeMap<String, Charset> m = new TreeMap<String, Charset>(String.CASE_INSENSITIVE_ORDER);
        m.put(ISO_8859_1.name(), ISO_8859_1);
        m.put(US_ASCII.name(), US_ASCII);
        m.put(UTF_16.name(), UTF_16);
        m.put(UTF_16BE.name(), UTF_16BE);
        m.put(UTF_16LE.name(), UTF_16LE);
        m.put(UTF_8.name(), UTF_8);
        return Collections.unmodifiableSortedMap(m);
    }

    /**
     * Returns the given Charset or the default Charset if the given Charset is null.
     */
    public static Charset toCharset(final Charset charset) {
        return charset == null ? Charset.defaultCharset() : charset;
    }

    /**
     * Returns a Charset for the named charset. If the name is null, return the default Charset.
     */
    public static Charset toCharset(final String charset) {
        return charset == null ? Charset.defaultCharset() : Charset.forName(charset);
    }

    /**
     * CharEncodingISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1.
     */
    @Deprecated
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");

    @Deprecated
    public static final Charset US_ASCII = Charset.forName("US-ASCII");


    @Deprecated
    public static final Charset UTF_16 = Charset.forName("UTF-16");


    @Deprecated
    public static final Charset UTF_16BE = Charset.forName("UTF-16BE");


    @Deprecated
    public static final Charset UTF_16LE = Charset.forName("UTF-16LE");

    @Deprecated
    public static final Charset UTF_8 = Charset.forName("UTF-8");
}
