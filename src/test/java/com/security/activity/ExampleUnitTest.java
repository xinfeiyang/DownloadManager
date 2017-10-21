package com.security.activity;

import com.security.util.StringBuilderWriter;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test(){
        String url="http://183.169.248.170:8080/com.sds.android.ttpod.apk";
        System.out.println(url.substring(url.lastIndexOf("/")+1,url.lastIndexOf(".")));
    }

    @Test
    public void testSep(){
        final StringBuilderWriter buf = new StringBuilderWriter(4);
        String LINE_SEPARATOR = buf.toString();
        System.out.println(LINE_SEPARATOR);
    }

    @Test
    public void testSub(){
        String url="http://dc6vcd.natappfree.cc/app/applist1";
        System.out.println(url.substring(url.lastIndexOf("/")+1));
    }
}