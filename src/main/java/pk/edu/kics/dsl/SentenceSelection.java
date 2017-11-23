package pk.edu.kics.dsl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.simple.parser.ParseException;

import pk.edu.kics.dsl.entity.Question;
import pk.edu.kics.dsl.entity.SolrResult;
import pk.edu.kics.dsl.util.IOHelper;
import pk.edu.kics.dsl.util.SolrHelper;
import pk.edu.kics.dsl.util.StringHelper;

public class SentenceSelection {

	public static final String SENTENCE_MATCHING_TECHNIQUE = "Jaccard";
	static String[] questionBatches = {"resources/questions/BioASQ-task3bPhaseA-testset1"};
	public final static String SOLR_SERVER = "10.11.10.210";
	public final static String SOLR_CORE = "medline";
	public final static String CONTENT_FIELD = "abstracttext";	
	public final static int TOTAL_DOCUMENTS = 26774856;
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException, SolrServerException {
		
		SolrHelper solrHelper = new SolrHelper();
		ArrayList<Question> questions = IOHelper.ReadQuestions(questionBatches);
				
		for (Question question : questions) {
			
			// Get top ten documents from server
			ArrayList<SolrResult> resultsList = solrHelper.submitQuery(question, 0, 10);
			
			// Sentence Split and add them to a main map
			for (SolrResult solrResult : resultsList) {
				String content = solrResult.getContent();
				if(content!=null) {
					List<String> abstractSentences = StringHelper.getSentences(content);
				}
			}
		}
	}
}