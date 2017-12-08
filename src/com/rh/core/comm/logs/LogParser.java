package com.rh.core.comm.logs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by well on 07/12/2017.
 */
public class LogParser {
    private  static long defaultMaxQtime = 200;

    public static void handleFile(String file) throws IOException {
        System.out.println(" handleFile file:" + file);
        FileReader fr = null;
        try {
            fr = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader bf = new BufferedReader(fr);
        @SuppressWarnings("unused")
		int b;
        while ((b = bf.read()) != -1) {
            String line = bf.readLine();
            handleLine(line);
        }
    }

    /*
     * 分析单行日志
     */
    private static void handleLine(String line) {
        printLongQtime(line);
    }

    private static void printLongQtime(String line) {
        Pattern p = Pattern.compile("count:.*] (.*)ms ");
        Matcher m = p.matcher(line);
        while(m.find()){
            String qtimeStr = m.group(1);
            if (isNumber(qtimeStr)) {
                long qtime = Long.valueOf(qtimeStr);
                if (qtime > defaultMaxQtime) {
                 System.out.println(line);
                }
            } else {
                System.err.println("qtime parsed error: qtimeStr:" + qtimeStr + "  data: " + line);
            }
        }
    }

    private static boolean isNumber(String str) {
        for (int i = 0; i < str.length(); i++){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;

    }


    public static void main (String [] args) {
        if (args.length > 0) {
        String file = args[0];
        try {
            defaultMaxQtime = Long.valueOf(args[1]);
        } catch (Exception e){
            System.err.println(" max qtime set error");
            e.printStackTrace();
        }

        System.out.println("file path:" + file);
        System.out.println("MaxQtime:" + defaultMaxQtime);

            try {
                LogParser parser = new LogParser();
                parser.handleFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("no args");
        }
    }
}

