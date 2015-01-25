package net.iubris.facri.grapher.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import net.iubris.facri._di.annotations.grapher.corpus.CorpusPathPrefix;

public abstract class AbstractGraphExporter implements GraphExporter {
	
	protected final String corpusNameAsGraphFilesPrefix;

	public AbstractGraphExporter(@CorpusPathPrefix String corpusPrefixProvider) throws IOException {
		this.corpusNameAsGraphFilesPrefix = Files.readAllLines( new File(corpusPrefixProvider).toPath() ).get(0);
	}

}
