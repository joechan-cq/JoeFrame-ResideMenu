package joe.frame.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Description  封装基础操作的File类
 * Created by chenqiao on 2016/5/18.
 */
public class QuickFile extends File {
    public QuickFile(File dir, String name) {
        super(dir, name);
    }

    public QuickFile(String path) {
        super(path);
    }

    public QuickFile(String dirPath, String name) {
        super(dirPath, name);
    }

    public QuickFile(URI uri) {
        super(uri);
    }

    /**
     * 读取File的内容
     *
     * @return byte形式的内容
     * @throws IOException
     * @Warning 不要读取大文件，会出错。
     */
    public byte[] readBytes() throws IOException {
        byte[] temp = new byte[1024];
        byte[] data = new byte[0];
        FileInputStream inputStream = new FileInputStream(this);
        int len;
        while ((len = inputStream.read(temp)) != -1) {
            int originalLen = data.length;
            data = Arrays.copyOf(data, originalLen + len);
            System.arraycopy(temp, 0, data, originalLen, len);
        }
        inputStream.close();
        return data;
    }

    public void writeBytes(byte[] datas) throws IOException {
        if (!exists()) {
            createNewFile();
        }
        FileOutputStream outputStream = new FileOutputStream(this);
        outputStream.write(datas);
        outputStream.flush();
        outputStream.close();
    }

    public String[] readLines() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(this));
        String temp;
        ArrayList<String> lines = new ArrayList<>();
        while ((temp = reader.readLine()) != null) {
            lines.add(temp);
        }
        reader.close();
        return (String[]) lines.toArray();
    }

    public void writeString(String content) throws IOException {
        writeString(content, false);
    }

    public void writeString(String content, boolean isAppend) throws IOException {
        if (!exists()) {
            createNewFile();
        }
        FileWriter writer = new FileWriter(this, isAppend);
        writer.write(content);
        writer.flush();
        writer.close();
    }
}