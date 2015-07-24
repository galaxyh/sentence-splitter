package com.yullage.nlp.util;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import java.io.*;

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
            splitStdio(config);
        }

        float timeElapsed = ((float)(System.currentTimeMillis() - timeStart)) / 60000;
        System.out.println("All done. (" + Float.toString(timeElapsed) + " minutes)");
    }

    private static void splitStdio(Config config) {
        Splitter splitter = new Splitter(config);

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
            Writer writer = new OutputStreamWriter(System.out, "UTF-8");

            String input = "";
            String line;
            if (config.stdioEofMark) {
                while ((line = reader.readLine()) != null) {
                    if (line.trim().equals(Splitter.EOF_MARK)) {
                        input.trim();
                        splitter.splitStdio(input, writer);
                        System.out.print("\n");
                        input = "";
                    } else {
                        input += line + "\n";
                    }
                }
            } else {
                while ((line = reader.readLine()) != null) {
                    splitter.splitStdio(line, writer);
                    System.out.print("\n");
                }
            }

            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    splitter.splitFile(sourcePath + "/" + file.getName(), targetPath + "/" + file.getName() + "." + targetFileExt);
                    float timeElapsed = ((float)(System.currentTimeMillis() - timeStart)) / 1000;
                    System.out.println("File \"" + file.getName() + "\" processed. (" + Float.toString(timeElapsed) + " seconds)");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
