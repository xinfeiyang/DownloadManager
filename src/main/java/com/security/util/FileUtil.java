package com.security.util;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

/**
 * 有关外部存储SD的工具类;
 */
public class FileUtil {

    /**
     * 根据名称元素集合创建File;
     * Construct a file from the set of name elements.
     */
    public static File getFile(File directory,String... names) {
        if (directory == null) {
            throw new NullPointerException("directory must not be null");
        }
        if (names == null) {
            throw new NullPointerException("names must not be null");
        }
        File file = directory;
        for (String name : names) {
            file = new File(file, name);
        }
        return file;
    }

    /**
     *根据名称元素集合创建File;
     * Construct a file from the set of name elements.
     */
    public static File getFile(String... names) {
        if (names == null) {
            throw new NullPointerException("names must not be null");
        }
        File file = null;
        for (String name : names) {
            if (file == null) {
                file = new File(name);
            } else {
                file = new File(file, name);
            }
        }
        return file;
    }

    /**
     * 把File文件转化成FileInputStream;
     */
    public static FileInputStream openInputStream(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canRead() == false) {
                throw new IOException("File '" + file + "' cannot be read");
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        return new FileInputStream(file);
    }

    /**
     *将file转化成FileOutputStream;
     */
    public static FileOutputStream openOutputStream(final File file) throws IOException {
        return openOutputStream(file, false);
    }

    /**
     * 将file转化成FileOutputstream,若append为true,则文件可以进行追加;
     * @param file   the file to open for output
     * @param append if {@code true}, then bytes will be added to the
     *               end of the file rather than overwriting
     */
    public static FileOutputStream openOutputStream(File file,boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canWrite() == false) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }


    /**
     * 比较两个文件的内容是否一致;
     * Compares the contents of two files to determine if they are equal or not.
     */
    public static boolean contentEquals(File file1,File file2) throws IOException {
        boolean file1Exists = file1.exists();
        if (file1Exists != file2.exists()) {
            return false;
        }

        if (!file1Exists) {
            // two not existing files are equal
            return true;
        }

        if (file1.isDirectory() || file2.isDirectory()) {
            // don't want to compare directory contents
            throw new IOException("Can't compare directories, only files");
        }

        if (file1.length() != file2.length()) {
            // lengths differ, cannot be equal
            return false;
        }

        if (file1.getCanonicalFile().equals(file2.getCanonicalFile())) {
            // same file
            return true;
        }

        InputStream input1 = null;
        InputStream input2 = null;
        try {
            input1 = new FileInputStream(file1);
            input2 = new FileInputStream(file2);
            return IOUtils.contentEquals(input1, input2);

        } finally {
            IOUtils.closeQuietly(input1);
            IOUtils.closeQuietly(input2);
        }
    }

    /**
     * 读物文件的内容为字符串;
     */
    public static String readFileToString(File file,Charset encoding) throws IOException {
        InputStream in = null;
        try {
            in = openInputStream(file);
            return IOUtils.toString(in, Charsets.toCharset(encoding));
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * 读物文件的内容为字符串;
     */
    public static String readFileToString(final File file, final String encoding) throws IOException {
        return readFileToString(file, Charsets.toCharset(encoding));
    }


    /**
     * 读物文件的内容为字符串;
     */
    @Deprecated
    public static String readFileToString(final File file) throws IOException {
        return readFileToString(file, Charset.defaultCharset());
    }

    /**
     * 将文件内容转化为字节流;
     */
    public static byte[] readFileToByteArray(File file) throws IOException {
        InputStream in = null;
        try {
            in = openInputStream(file);
            return IOUtils.toByteArray(in); // Do NOT use file.length() - see IO-453
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * 将文件的内容一行行写入List集合中;
     * Reads the contents of a file line by line to a List of Strings.
     */
    public static List<String> readLines(File file, Charset encoding) throws IOException {
        InputStream in = null;
        try {
            in = openInputStream(file);
            return IOUtils.readLines(in, Charsets.toCharset(encoding));
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * 将文件的内容一行行写入List集合中;
     * Reads the contents of a file line by line to a List of Strings. The file is always closed.
     */
    public static List<String> readLines(File file,String encoding) throws IOException {
        return readLines(file, Charsets.toCharset(encoding));
    }

    /**
     * 将文件的内容一行行写入List集合中;
     * Reads the contents of a file line by line to a List of Strings using the default encoding for the VM.
     */
    @Deprecated
    public static List<String> readLines(File file) throws IOException {
        return readLines(file, Charset.defaultCharset());
    }


    /**
     * 根据字符编码格式将字符串写入File中
     * Writes a String to a file creating the file if it does not exist.
     */
    public static void writeStringToFile(File file,String data,Charset encoding)
            throws IOException {
        writeStringToFile(file, data, encoding, false);
    }

    /**
     * 根据字符编码格式将字符串写入File中
     * Writes a String to a file creating the file if it does not exist.
     */
    public static void writeStringToFile(File file,String data,String encoding) throws IOException {
        writeStringToFile(file, data,encoding, false);
    }

    /**
     * 根据字符编码格式将字符串写入File中，并设定是否追加;
     * Writes a String to a file creating the file if it does not exist.
     */
    public static void writeStringToFile(File file,String data,Charset encoding,boolean append) throws IOException {
        OutputStream out = null;
        try {
            out = openOutputStream(file, append);
            IOUtils.write(data, out, encoding);
            out.close(); // don't swallow close Exception if copy completes normally
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * 根据字符编码格式将字符串写入File中，并设定是否追加;
     * Writes a String to a file creating the file if it does not exist.
     */
    public static void writeStringToFile(File file,String data,String encoding,
                                         boolean append) throws IOException {
        writeStringToFile(file, data, Charsets.toCharset(encoding), append);
    }

    /**
     *将字符串写入File中
     */
    @Deprecated
    public static void writeStringToFile(File file,String data) throws IOException {
        writeStringToFile(file, data, Charset.defaultCharset(), false);
    }

    /**
     * 将字符串写入File中,并设定是否进行追加;
     * Writes a String to a file creating the file if it does not exist using the default encoding for the VM.
     */
    @Deprecated
    public static void writeStringToFile(File file,String data,boolean append) throws IOException {
        writeStringToFile(file, data, Charset.defaultCharset(), append);
    }


    /**
     * 将字符串写入FIle
     * Writes a CharSequence to a file creating the file if it does not exist using the default encoding for the VM.
     */
    @Deprecated
    public static void write(File file,CharSequence data) throws IOException {
        write(file, data, Charset.defaultCharset(), false);
    }

    /**
     * 将字符串写入FIle,并设定是否进行追加;
     * Writes a CharSequence to a file creating the file if it does not exist using the default encoding for the VM.
     */
    @Deprecated
    public static void write(File file,CharSequence data,boolean append) throws IOException {
        write(file, data, Charset.defaultCharset(), append);
    }

    /**
     *将字符串写入FIle
     * Writes a CharSequence to a file creating the file if it does not exist.
     */
    public static void write(File file,CharSequence data,Charset encoding) throws IOException {
        write(file, data, encoding, false);
    }

    /**
     * 将字符串写入FIle
     * Writes a CharSequence to a file creating the file if it does not exist.
     */
    public static void write(File file,CharSequence data,String encoding) throws IOException {
        write(file, data, encoding, false);
    }

    /**
     * 将字符串写入FIle,并设定是否进行追加;
     * Writes a CharSequence to a file creating the file if it does not exist.
     */
    public static void write(File file,CharSequence data,Charset encoding,boolean append)
            throws IOException {
        final String str = data == null ? null : data.toString();
        writeStringToFile(file, str, encoding, append);
    }

    /**
     * 将字符串写入FIle,并设定是否进行追加;
     * Writes a CharSequence to a file creating the file if it does not exist.
     */
    public static void write(File file,CharSequence data,String encoding,boolean append)
            throws IOException {
        write(file, data, Charsets.toCharset(encoding), append);
    }

    /**
     * 将字节写入File
     * Writes a byte array to a file creating the file if it does not exist.
     */
    public static void writeByteArrayToFile(File file,byte[] data) throws IOException {
        writeByteArrayToFile(file, data, false);
    }

    /**
     * 将字节写入File,并设定是否追加;
     * Writes a byte array to a file creating the file if it does not exist.
     */
    public static void writeByteArrayToFile(File file,byte[] data,boolean append)
            throws IOException {
        writeByteArrayToFile(file, data, 0, data.length, append);
    }

    /**
     * 将字节写入File,并设定是否追加;
     * Writes {@code len} bytes from the specified byte array starting
     * at offset {@code off} to a file, creating the file if it does
     * not exist.
     */
    public static void writeByteArrayToFile(File file,byte[] data,int off,int len)
            throws IOException {
        writeByteArrayToFile(file, data, off, len, false);
    }

    /**
     *将字节写入File,并设定是否追加;
     */
    public static void writeByteArrayToFile(File file,byte[] data, int off,int len,
                                            boolean append) throws IOException {
        OutputStream out = null;
        try {
            out = openOutputStream(file, append);
            out.write(data, off, len);
            out.close(); // don't swallow close Exception if copy completes normally
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * 将集合中的字符串按照一行一行的样式写入File中;
     * Writes the <code>toString()</code> value of each item in a collection to
     * the specified <code>File</code> line by line.
     */
    public static void writeLines(File file,String encoding,Collection<?> lines)
            throws IOException {
        writeLines(file, encoding, lines, null, false);
    }

    /**
     *将集合中的字符串按照一行一行的样式写入File中,并设定是否进行追加;
     */
    public static void writeLines(File file,String encoding,Collection<?> lines,boolean append) throws IOException {
        writeLines(file, encoding, lines, null, append);
    }

    /**
     *将集合中的字符串按照一行一行的样式写入File中
     */
    public static void writeLines(File file,Collection<?> lines) throws IOException {
        writeLines(file, null, lines, null, false);
    }

    /**
     *将集合中的字符串按照一行一行的样式写入File中,并设定是否进行追加;
     */
    public static void writeLines(File file,Collection<?> lines,boolean append) throws IOException {
        writeLines(file, null, lines, null, append);
    }

    /**
     *将集合中的字符串按照一行一行的样式写入File中,并设定是否进行追加;
     * @param lineEnding the line separator to use, {@code null} is system default
     */
    public static void writeLines(File file,String encoding,Collection<?> lines,
                                  String lineEnding) throws IOException {
        writeLines(file, encoding, lines, lineEnding, false);
    }

    /**
     *将集合中的字符串按照一行一行的样式写入File中,并设定是否进行追加;
     */
    public static void writeLines(File file,String encoding,Collection<?> lines,
                                  String lineEnding,boolean append) throws IOException {
        FileOutputStream out = null;
        try {
            out = openOutputStream(file, append);
            BufferedOutputStream buffer = new BufferedOutputStream(out);
            IOUtils.writeLines(lines, lineEnding, buffer, encoding);
            buffer.flush();
            out.close(); // don't swallow close Exception if copy completes normally
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * 将集合中的字符串按照一行一行的样式写入File中,并设定是否进行追加;
     */
    public static void writeLines(final File file,Collection<?> lines,String lineEnding)
            throws IOException {
        writeLines(file, null, lines, lineEnding, false);
    }

    /**
     * 将集合中的字符串按照一行一行的样式写入File中,并设定是否进行追加;
     */
    public static void writeLines(File file,Collection<?> lines,String lineEnding,
                                  boolean append) throws IOException {
        writeLines(file, null, lines, lineEnding, append);
    }

    /****************************************************************************/
    /**
     * 判断SD卡是否可用;
     * @return :true代表SD卡可用,false代表SD卡不可用;
     */
    public static boolean isSDAvailable() {
        String state = Environment.getExternalStorageState();
        if (state != null && state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /*获取缓存路径，存储临时文件，可被一键清理和卸载清理*/
    /*
    * @param uniqueName :缓存的数据的类型的名称,如JSON代表缓存的是文本资源;
    * 可以看到，当SD卡存在或者SD卡不可被移除的时候，
    * 就调用getExternalCacheDir()方法来获取缓存路径，
    * 否则就调用getCacheDir()方法来获取缓存路径。
    * 前者获取到的就是/sdcard/Android/data/<application package>/cache这个路径，
    * 而后者获取到的是 /data/data/<application package>/cache 这个路径。*/
    public static File getDiskCacheDir(Context context,String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 从缓存文件中读取保存的文本数据,若SD卡可用则从SD卡的缓存文件中读取相应的数据,
     * 若SD卡不可用,则从SharedPreference中读取;
     * @param context
     * @param fileName
     * @return
     */
    public static String readTextFromCacheFile(Context context,String fileName){
        String result="";
        if(isSDAvailable()){
            String key=MD5Util.DatEncryption(fileName);
            File file=new File(getDiskCacheDir(context,"JSON"),key);
            if(file.exists()){
                try {
                    FileInputStream is = new FileInputStream(file);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) != -1) {
                        stream.write(buffer, 0, length);
                    }
                    is.close();
                    stream.close();
                    result = stream.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{
            result= (String) SharedPreferenceUtil.get(context,fileName,"");
        }
        return result;
    }

    /**
     * 緩存文本数据到缓存文件中;若SD卡可用,则将文本数据缓存进SD卡的缓存文件中,
     * 否则将其存放进SharedPreference中;
     * @param fileName:缓存的文件的名称;
     * @param value:缓存的文本数据;
     */
    public static void saveTextToCacheFile(Context context,String fileName,String value){
        if(isSDAvailable()){
            try {
                String key=MD5Util.DatEncryption(fileName);
                File file=new File(getDiskCacheDir(context,"JSON"),key);
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    //创建目录
                    parentFile.mkdirs();
                }
                if(!file.exists()){
                    file.createNewFile();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(value.getBytes());
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            SharedPreferenceUtil.put(context,fileName,value);
        }
    }

}
