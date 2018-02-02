package com.rh.core.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * 整合增量sql
 * Created by shenh on 2018/2/2.
 */
public class CollectSql {

    private static String startDateString = "2017-12-26";

    private static String endDateString = "2018-02-01";


    public static Date startDate = null;
    public static Date endDate = null;

    public static void main(String[] args) throws IOException, ParseException {
        startDate = DateUtils.parseDate(startDateString);
        endDate = DateUtils.parseDate(endDateString);
        CollectSql.collectionSql();
    }

    public static void collectionSql() throws IOException {
        String path = CollectSql.class.getResource("").getPath();
        String folder = path.substring(0, path.indexOf("pro")) + "db/increment/";
        String allFileContent = getAllFileContent(new File(folder));

        File file = new File(folder + "temp.sql");
        if (!file.exists())
            file.createNewFile();
        FileUtils.write(file, allFileContent);
        System.out.println(path);
    }

    public static String getAllFileContent(File folder) throws IOException {
        StringBuilder result = new StringBuilder();

        File[] files = folder.listFiles();
        for (File file : files != null ? files : new File[0]) {
            if (!ignoreFile(file)) {
                result.append("/***").append(file.getName()).append("***/\n");
                String fileToString = FileUtils.readFileToString(file);
                result.append(fileToString).append("\n");
            }
        }
        return result.toString();
    }

    public static boolean ignoreFile(File file) {
        boolean result = false;
        try {
            Date date = DateUtils.parseDate(file.getName().substring(0, 10));
            result = date.before(startDate) || date.after(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return true;
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return true;
        }
        return result;
    }
}
