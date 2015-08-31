package com.yullage.nlp.util;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.*;
import java.util.List;
import java.util.Properties;

/**
 * Created by Yu-chun Huang on 3/11/15.
 */
public class Splitter {
    public static final String WORD_SEPARATOR = " ";
    public static final String EOF_MARK = "__EOF__";
    private Config config;
    private Properties props = new Properties();
    private StanfordCoreNLP pipeline;

    public Splitter(Config config) {
        this.config = config;

        if (config.language == LanguageType.CHINESE) {
            props.setProperty("annotators", "segment, ssplit");
            props.setProperty("customAnnotatorClass.segment", "edu.stanford.nlp.pipeline.ChineseSegmenterAnnotator");
            props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese/ctb.gz");
            props.setProperty("segment.sighanCorporaDict", "edu/stanford/nlp/models/segmenter/chinese");
            props.setProperty("segment.serDictionary", "edu/stanford/nlp/models/segmenter/chinese/dict-chris6.ser.gz");
            props.setProperty("segment.sighanPostProcessing", "true");
            props.setProperty("ssplit.boundaryTokenRegex", "[.]|[! ?]+ |[。]|[！？]+");
        } else if (config.language == LanguageType.ENGLISH) {
            props.setProperty("annotators", "tokenize, ssplit");
            if (config.nlNewSentence) {
                props.setProperty("ssplit.newlineIsSentenceBreak", "always");
            }
        } else {
            throw new IllegalArgumentException("Language type not supported.");
        }

        pipeline = new StanfordCoreNLP(props);
    }

    public void splitStdio(String sentences, Writer writer) {
        try {
            writeResult(sentences, writer);

            if (config.eofMark) {
                writer.write("\n");
                writer.write(Splitter.EOF_MARK);
                writer.write("\n");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void splitFile(String sourceFileName, String targetFileName) throws IOException {
        File sourceFile = new File(sourceFileName);
        Writer targetFile = new OutputStreamWriter(new FileOutputStream(targetFileName), "UTF-8");

        if ((config.language == LanguageType.CHINESE) && config.nlNewSentence) {
            Reader reader = new InputStreamReader(new FileInputStream(sourceFile), "UTF-8");
            BufferedReader br = new BufferedReader(reader);

            String line;
            while ((line = br.readLine()) != null) {
                boolean isOk = false;
                try {
                    isOk = writeResult(line.trim(), targetFile);
                } catch (Exception e) {
                    System.err.println("Fail to split sentence! " + sourceFile);
                    System.err.println("File: " + sourceFile);
                    System.err.println("Sentence: " + line.trim());
                    e.printStackTrace(System.err);
                }

                if (isOk) {
                    targetFile.write("\n");
                }
            }
        } else {
            try {
                writeResult(IOUtils.slurpFile(sourceFile, "UTF-8"), targetFile);
                targetFile.write("\n");
            } catch (Exception e) {
                System.err.println("Fail to split file: " + sourceFile);
                e.printStackTrace(System.err);
            }
        }

        targetFile.close();
    }

    private boolean writeResult(String corpus, Writer writer) {
        if (corpus.length() > 0) {
            Annotation document = new Annotation(corpus);
            pipeline.annotate(document);

            List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
            if (sentences != null) {
                int lastSentenceIndex = sentences.size() - 1;

                try {
                    for (int i = 0; i < sentences.size(); i++) {
                        List<CoreLabel> tokens = sentences.get(i).get(CoreAnnotations.TokensAnnotation.class);
                        if (tokens != null) {
                            int lastTokenIndex = tokens.size() - 1;

                            for (int j = 0; j < tokens.size(); j++) {
                                String word = tokens.get(j).get(CoreAnnotations.TextAnnotation.class);
                                writer.write(word);
                                if ((lastTokenIndex == 0) || (j < lastTokenIndex)) {
                                    writer.write(WORD_SEPARATOR);
                                }
                            }
                        }

                        if (i < lastSentenceIndex) {
                            writer.write("\n");
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Fail to write split sentence!");
                    return false;
                }
            }
            return true;
        }

        return false;
    }
}
