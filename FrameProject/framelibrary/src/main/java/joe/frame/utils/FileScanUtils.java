package joe.frame.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Description  文件扫描工具类(需实例化)
 * 用于扫描出指定文件夹中的符合过滤条件(正则表达式)以及符合大小条件的文件
 * Created by chenqiao on 2016/1/14.
 */
public class FileScanUtils {

    private static final String HEAD = ".+(\\.(";
    private static final String TAIL = "){1})$";

    private static final String IMAGE_SUFFIX = "jpg|jpeg|png|bmp|gif";
    private static final String MUSIC_SUFFIX = "mp1|mp2|mp3|wma|aac|wav";
    private static final String VIDEO_SUFFIX = "mp4|rm|rmvb|avi|mpg|mpeg|mov|flv|3gp|dmv|m4v|vob";
    public static final String DOCUMENT_SUFFIX = "txt|doc|log|docx";

    public static final String IMAGE = HEAD + IMAGE_SUFFIX + TAIL;
    public static final String MUSIC = HEAD + MUSIC_SUFFIX + TAIL;
    public static final String MEDIA = HEAD + MUSIC_SUFFIX + "|" + VIDEO_SUFFIX + TAIL;

    private ArrayList<File> results;
    private int depth;

    public FileScanUtils() {
        results = new ArrayList<>();
    }

    /**
     * @param dir      初始扫描的目录
     * @param suffixes 过滤文件的后缀名，例如："mp3|mp4|3gp"
     * @param depth    扫描深度(>=0,0表示初始目录下)
     * @return 符合条件的文件
     */
    public List<File> scanBySuffixes(File dir, String suffixes, int depth) {
        return scanBySuffixes(dir, suffixes, ">", 0, depth);
    }

    /**
     * @param dir        初始扫描的目录
     * @param suffixes   过滤文件的后缀名，例如："mp3|mp4|3gp"
     * @param comparison 比较符：">","<","="
     * @param fileSize   文件大小
     * @param depth      扫描深度(>=0,0表示初始目录下)
     * @return 符合条件的文件
     */
    public List<File> scanBySuffixes(File dir, String suffixes, String comparison, int fileSize, int depth) {
        String regularExpression = HEAD + suffixes + TAIL;
        return scanByRegularExpression(dir, regularExpression, comparison, fileSize, depth);
    }

    /**
     * @param dir               初始扫描的目录
     * @param regularExpression 过滤文件名的正则表达式
     * @param depth             扫描深度(>=0,0表示初始目录下)
     * @return 符合条件的文件
     */
    public List<File> scanByRegularExpression(File dir, String regularExpression, int depth) {
        return scanByRegularExpression(dir, regularExpression, ">", 0, depth);
    }

    /**
     * @param dir               初始扫描的目录
     * @param regularExpression 过滤文件名的正则表达式
     * @param comparison        比较符：">","<","="
     * @param fileSize          文件大小
     * @param depth             扫描深度(>=0,0表示初始目录下)
     * @return 符合条件的文件
     */
    public List<File> scanByRegularExpression(File dir, String regularExpression, String comparison, int fileSize, int depth) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return null;
        }
        results.clear();
        this.depth = depth;
        search(results, dir, regularExpression, comparison, fileSize, 0);
        return results;
    }

    /**
     * 递归扫描
     */
    private void search(List<File> results, File dir, final String regularExpression, final String comparison, final int fileSize, int deep) {
        if (deep > this.depth) {
            return;
        }
        FilenameFilter validFileFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                boolean sizeOk;
                File file = new File(dir, filename);
                switch (comparison) {
                    case ">":
                        sizeOk = file.length() > fileSize;
                        break;
                    case "<":
                        sizeOk = file.length() < fileSize;
                        break;
                    case "=":
                        sizeOk = (file.length() == fileSize);
                        break;
                    default:
                        sizeOk = false;
                        break;
                }
                return filename.matches(regularExpression) && sizeOk;
            }
        };
        File[] temp = dir.listFiles(validFileFilter);
        Collections.addAll(results, temp);
        File[] dirs = dir.listFiles(new DirFileFilter());
        for (File tempDir : dirs) {
            search(results, tempDir, regularExpression, comparison, fileSize, deep + 1);
        }
    }

    /**
     * 目录筛选
     */
    class DirFileFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    }
}