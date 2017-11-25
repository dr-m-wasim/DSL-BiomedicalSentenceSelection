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

	// http://www.ranks.nl/stopwords
	public static ArrayList<String> stopWords = new ArrayList<>(Arrays.asList("about","above","across","after","again","against","all","almost","alone","along","already","also","although","always","among","an","and","another","any","anybody","anyone","anything","anywhere","are","area","areas","around","as","ask","asked","asking","asks","at","away","b","back","backed","backing","backs","be","became","because","become","becomes","been","before","began","behind","being","beings","best","better","between","big","both","but","by","c","came","can","cannot","case","cases","certain","certainly","clear","clearly","come","could","d","did","differ","different","differently","do","does","done","down","down","downed","downing","downs","during","e","each","early","either","end","ended","ending","ends","enough","even","evenly","ever","every","everybody","everyone","everything","everywhere","f","face","faces","fact","facts","far","felt","few","find","finds","first","for","four","from","full","fully","further","furthered","furthering","furthers","g","gave","general","generally","get","gets","give","given","gives","go","going","good","goods","got","great","greater","greatest","group","grouped","grouping","groups","h","had","has","have","having","he","her","here","herself","high","high","high","higher","highest","him","himself","his","how","however","i","if","important","in","interest","interested","interesting","interests","into","is","it","its","itself","j","just","k","keep","keeps","kind","knew","know","known","knows","l","large","largely","last","later","latest","least","less","let","lets","like","likely","long","longer","longest","m","made","make","making","man","many","may","me","member","members","men","might","more","most","mostly","mr","mrs","much","must","my","myself","n","necessary","need","needed","needing","needs","never","new","new","newer","newest","next","no","nobody","non","noone","not","nothing","now","nowhere","number","numbers","o","of","off","often","old","older","oldest","on","once","one","only","open","opened","opening","opens","or","order","ordered","ordering","orders","other","others","our","out","over","p","part","parted","parting","parts","per","perhaps","place","places","point","pointed","pointing","points","possible","present","presented","presenting","presents","problem","problems","put","puts","q","quite","r","rather","really","right","right","room","rooms","s","said","same","saw","say","says","second","seconds","see","seem","seemed","seeming","seems","sees","several","shall","she","should","show","showed","showing","shows","side","sides","since","small","smaller","smallest","so","some","somebody","someone","something","somewhere","state","states","still","still","such","sure","t","take","taken","than","that","the","their","them","then","there","therefore","these","they","thing","things","think","thinks","this","those","though","thought","thoughts","three","through","thus","to","today","together","too","took","toward","turn","turned","turning","turns","two","u","under","until","up","upon","us","use","used","uses","v","very","w","want","wanted","wanting","wants","was","way","ways","we","well","wells","went","were","what","when","where","whether","which","while","who","whole","whose","why","will","with","within","without","work","worked","working","works","would","x","y","year","years","yet","you","young","younger","youngest","your","yours","z"));

		
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

	public static String formulateQuery(String body) throws IOException {
		
		StringBuilder sb = new StringBuilder();
		ArrayList<String> tokens = solrTokenizer(body);

		for (String token : tokens) {
			if(!stopWords.contains(token)) {
				sb.append(token).append(" ");
			}
		}
		
		//System.out.println(sb.toString());
		
		return sb.toString();
	}
}
