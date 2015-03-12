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

    public void split(String sourceFileName, String targetFileName) throws IOException {
        File sourceFile = new File(sourceFileName);
        Writer targetFile = new OutputStreamWriter(new FileOutputStream(targetFileName), "UTF-8");

        if ((config.language == LanguageType.CHINESE) && config.nlNewSentence) {
            Reader reader = new InputStreamReader(new FileInputStream(sourceFile), "UTF-8");
            BufferedReader br = new BufferedReader(reader);

            String line;
            while ((line = br.readLine()) != null) {
                writeResult(line, targetFile);
                targetFile.write("\n");
            }
        } else {
            writeResult(IOUtils.slurpFile(sourceFile, "UTF-8"), targetFile);
        }

        targetFile.close();
    }

    private void writeResult(String corpus, Writer targetFile) throws IOException {
        Annotation document = new Annotation(corpus);
        pipeline.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        if (sentences != null) {
            int lastSentenceIndex = sentences.size() - 1;

            for (int i = 0; i < sentences.size(); i++) {
                List<CoreLabel> tokens = sentences.get(i).get(CoreAnnotations.TokensAnnotation.class);
                if (tokens != null) {
                    int lastTokenIndex = tokens.size() - 1;

                    for (int j = 0; j < tokens.size(); j++) {
                        String word = tokens.get(j).get(CoreAnnotations.TextAnnotation.class);
                        targetFile.write(word);
                        if ((lastTokenIndex == 0) ||(j < lastTokenIndex)) {
                            targetFile.write(WORD_SEPARATOR);
                        }
                    }
                }

                if (i < lastSentenceIndex) {
                    targetFile.write("\n");
                }
            }
        }
    }
}
