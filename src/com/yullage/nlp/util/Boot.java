package com.yullage.nlp.util;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import java.io.File;
import java.io.IOException;

/**
 * Created by Yu-chun Huang on 3/11/15.
 */
public class Boot {
    public static void main(String[] args) throws IOException {
        Config config = new Config();
        JCommander jCommander = new JCommander(config);

        if (args.length == 0) {
            jCommander.usage();
            return;
        }

        try {
            jCommander.parse(args);
        } catch (ParameterException e) {
            System.out.println(e.getMessage());
            jCommander.usage();
            return;
        }

        long timeStart = System.currentTimeMillis();
        if (config.io == IoType.FILE) {
            splitFile(config);
        } else if (config.io == IoType.STDIO) {
            System.out.println("Standard I/O is not yet implemented.");
            //stdioSplitter(config);
        }

        float timeElapsed = ((float)(System.currentTimeMillis() - timeStart)) / 60000;
        System.out.println("All done. (" + Float.toString(timeElapsed) + " minutes)");
    }

    private static void splitFile(Config config) {
        Splitter splitter = new Splitter(config);

        String sourcePath = config.sourcePath;
        String targetPath = config.targetPath;
        String targetFileExt = config.targetFileExt;

        File sourceDir = new File(config.sourcePath);
        if (!sourceDir.isDirectory()) {
            System.out.println(config.sourcePath + " is not a directory. Process aborted!");
        }

        File targetDir = new File(config.targetPath);
        if (!targetDir.isDirectory()) {
            System.out.println(config.targetPath + " is not a directory. Process aborted!");
        }

        for (File file : sourceDir.listFiles()) {
            if (file.isFile()) {
                try {
                    long timeStart = System.currentTimeMillis();
                    System.out.println("Processing file \"" + file.getName() + "\" ...");
                    splitter.split(sourcePath + "/" + file.getName(), targetPath + "/" + file.getName() + "." + targetFileExt);
                    float timeElapsed = ((float)(System.currentTimeMillis() - timeStart)) / 1000;
                    System.out.println("File \"" + file.getName() + "\" processed. (" + Float.toString(timeElapsed) + " seconds)");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
