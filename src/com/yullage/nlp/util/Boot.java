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

        if (config.io == IoType.FILE) {
            splitFile(config);
        } else if (config.io == IoType.STDIO) {
            System.out.println("Standard I/O is not yet implemented.");
            //stdioSplitter(config);
        }

        System.out.println("All done.");
    }

    private static void splitFile(Config config) {
        Splitter splitter = new Splitter(config);

        String sourcePath = config.sourcePath;
        String targetPath = config.targetPath;
        String targetFileExt = config.targetFileExt;

        File directory = new File(config.sourcePath);
        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                try {
                    splitter.split(sourcePath + "/" + file.getName(), targetPath + "/" + file.getName() + "." + targetFileExt);
                    System.out.println("File \"" + file.getName() + "\" processed.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
