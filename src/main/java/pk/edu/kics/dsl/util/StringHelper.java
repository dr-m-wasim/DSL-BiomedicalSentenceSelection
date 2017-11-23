package pk.edu.kics.dsl.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeFactory;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class StringHelper {
	
	public static List<String> getSentences(String content) throws IOException{
		InputStream inputStream = new FileInputStream("models/en-sent.bin"); 
		SentenceModel model = new SentenceModel(inputStream);
		SentenceDetectorME detector = new SentenceDetectorME(model);
		String sentences[] = detector.sentDetect(content);
		
		return Arrays.asList(sentences);
	}
	
	public static ArrayList<String> solrTokenizer(String content) throws IOException {
		AttributeFactory factory = AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY;
		StandardTokenizer tokenizer = new StandardTokenizer(factory);
		ArrayList<String> tokens = new ArrayList<String>();
		tokenizer.setReader(new StringReader(content));
		tokenizer.reset();
		CharTermAttribute attr = tokenizer.addAttribute(CharTermAttribute.class);

		Map<String, String> param = new HashMap<>();
		param.put("luceneMatchVersion", "LUCENE_66");

		LowerCaseFilterFactory lowerCaseFactory = new LowerCaseFilterFactory(param);
		TokenStream tokenStream = lowerCaseFactory.create(tokenizer);

		while(tokenStream.incrementToken()) {
			String term = attr.toString();
			tokens.add(term);
		}

		tokenStream.close();
		return tokens;
	}
}
