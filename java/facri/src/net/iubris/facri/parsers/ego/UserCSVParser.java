package net.iubris.facri.parsers.ego;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import com.googlecode.jcsv.annotations.internal.ValueProcessorProvider;
import com.googlecode.jcsv.reader.CSVEntryParser;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.AnnotationEntryParser;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;

public class UserCSVParser<T> {
	
	final String fileRelativePath; 
	final Class<T> clazz;
	
	public UserCSVParser(String fileRelativePath, Class<T> clazz) {
		this.fileRelativePath = fileRelativePath;
		this.clazz = clazz;
	}

	public List<T> parse() throws IOException {
		// annotation way, not working
		Reader reader = new FileReader(fileRelativePath);

		ValueProcessorProvider provider = new ValueProcessorProvider();
		CSVEntryParser<T> entryParser = new AnnotationEntryParser<T>(clazz, provider);
		CSVReader<T> csvEntryReader = new CSVReaderBuilder<T>(reader).entryParser(entryParser).build();

		return csvEntryReader.readAll();
		
//		Reader reader = new FileReader(fileRelativePath);
//
//		CSVReader<String[]> csvUserReader = CSVReaderBuilder.newDefaultReader(reader);
//		List<String[]> users = csvUserReader.readAll();
	}
	
	/*class FriendOrAlikeEntryParser implements CSVEntryParser<FriendOrAlike> {
      public FriendOrAlike parseEntry(String... data) {
              String firstname = data[0];
              String lastname = data[1];
              int age = Integer.parseInt(data[2]);

              return new FriendOrAlike(firstname, lastname, age);
      }
	}*/
}
