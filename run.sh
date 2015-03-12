java -cp sentence-splitter-1.0.0.jar:stanford-chinese-corenlp-2014-02-24-models.jar:stanford-corenlp-3.4.1.jar -Xmx4g com.yullage.nlp.util.Boot \
-io file \
-language chinese \
-nlNewSentence \
-sourcePath corpus_in \
-targetPath corpus_out \
-targetFileExt split