/**
 * 
 */
package com.yullage.nlp.util;

import com.beust.jcommander.Parameter;

/**
 * @author Yu-chun Huang
 *
 */
public class Config {
	@Parameter(names = "-io", description = "IO type. Currently, \"stdio\" and \"file\" are supported.", converter = IoTypeConverter.class)
	public IoType io = IoType.STDIO;

	@Parameter(names = "-language", description = "Corpus language. Currently, \"chinese\" and \"english\" are supported.", converter = LanguageTypeConverter.class, required = true)
	public LanguageType language;

    @Parameter(names = "-nlNewSentence", description = "Treat new line character as the start of a new sentence.")
    public boolean nlNewSentence = false;

	@Parameter(names = "-sourcePath", description = "Source corpus folder. Only used when IO type is set to file.")
	public String sourcePath;

	@Parameter(names = "-targetPath", description = "Corpus output folder. Only used when IO type is set to file.")
	public String targetPath;

    @Parameter(names = "-targetFileExt", description = "Corpus output file extension. Only used when IO type is set to file.")
    public String targetFileExt = "split";
}
