package com.crowbar.reportdata.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author panda421
 * @date 2021-04-20 17:52
 */
public class Util {
    public static List<String> readTxt(String path) {
        try {
            File keyTxtDir = new File(path);
            FileInputStream inputStream = new FileInputStream(keyTxtDir);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String str = null;
            List<String> keyList = new ArrayList<>();
            while ((str = bufferedReader.readLine()) != null) {
                keyList.add(str);
            }
            return keyList;

        } catch (IOException e) {
        }
        return null;
    }

    public static ArrayList<File> getFiles(String path) {
        ArrayList<File> flieList = new ArrayList<File>();
        File file = new File(path);
        if (file.isDirectory()) {
            File[] flies = file.listFiles();
            for (File fileIndex : flies) {
                flieList.add(fileIndex);
            }
        }
        Collections.sort(flieList, new Comparator< File>() {
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                return o1.getName().compareTo(o2.getName());
            }
        });
        return flieList;
    }

    public static void save(String data, String file){
        try {
            File idTxtDir = new File("E:\\device\\"+file);
            FileOutputStream outputStream = new FileOutputStream(idTxtDir,true);
            OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
            streamWriter.write(data+"\n");
            streamWriter.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}
