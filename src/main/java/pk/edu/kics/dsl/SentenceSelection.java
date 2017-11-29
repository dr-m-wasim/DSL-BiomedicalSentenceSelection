package pk.edu.kics.dsl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import pk.edu.kics.dsl.entity.InputQuestion;
import pk.edu.kics.dsl.entity.QuestionResult;
import pk.edu.kics.dsl.entity.SnippetResult;
import pk.edu.kics.dsl.entity.SolrResult;
import pk.edu.kics.dsl.evaluation.EvaluatorTaskB;
import pk.edu.kics.dsl.sm.SentenceSimilarity;
import pk.edu.kics.dsl.util.IOHelper;
import pk.edu.kics.dsl.util.SolrHelper;
import pk.edu.kics.dsl.util.StringHelper;

public class SentenceSelection {

	public static final String SENTENCE_MATCHING_TECHNIQUE = "Overlap";
	public static final int TOP_DOCUMENTS_TO_SELECT = 10;
	public static final int TOP_SENTENCES_TO_SELECT = 1;
	
	static String[] questionBatches = {"resources/questions/BioASQ-task3bPhaseA-testset1" };
	public final static String SOLR_SERVER = "10.11.10.210";
	public final static String SOLR_CORE = "medline-lm";
	public final static String CONTENT_FIELD = "abstracttext";
	public final static int TOTAL_DOCUMENTS = 26759399;
	
	public final static String GOLDEN_FILE = "resources/gold/BioASQ-task3bPhaseB-testset1";
	public final static String SYSTEM_GENERATED_FILE = "resources/result.txt";

	@SuppressWarnings("unchecked")
	public static void main(String[] args)
			throws FileNotFoundException, IOException, ParseException, SolrServerException, InstantiationException, IllegalAccessException, ClassNotFoundException {

		ArrayList<InputQuestion> questions = IOHelper.ReadQuestions(questionBatches);
		
		JSONObject results = new JSONObject();
		JSONArray questionResults = new JSONArray();
		
		int counter = 1;
		for (InputQuestion question : questions) {
			questionResults.add(processQuestion(question).toJSON());
			System.out.println("Processed Question: " + counter++);
		}

		results.put("questions", questionResults);
		IOHelper.writeResults(SYSTEM_GENERATED_FILE, results.toJSONString());
		
		EvaluatorTaskB eval = new EvaluatorTaskB(GOLDEN_FILE, SYSTEM_GENERATED_FILE, EvaluatorTaskB.BIOASQ3);
		eval.setVERSION_OF_CHALLENGE(EvaluatorTaskB.BIOASQ3);
		eval.EvaluatePhaseA();
	}

	private static QuestionResult processQuestion(InputQuestion question) throws SolrServerException, IOException,
			InstantiationException, IllegalAccessException, ClassNotFoundException {

		SolrHelper solrHelper = new SolrHelper();
		ArrayList<SnippetResult> snippetsResult = new ArrayList<>();
		ArrayList<SolrResult> resultsList = solrHelper.submitQuery(question, 0, TOP_DOCUMENTS_TO_SELECT);
		String ssClass = "pk.edu.kics.dsl.sm." + SENTENCE_MATCHING_TECHNIQUE;
		SentenceSimilarity setenceSimilarity = (SentenceSimilarity) Class.forName(ssClass).newInstance();

		for (SolrResult solrResult : resultsList) {
			
			String abstractText = solrResult.getContent();
			String titleText = solrResult.getTitle();
			
			if (abstractText != null) {
				List<String> abstractSentences = StringHelper.getSentences(abstractText);
				Map<String, Double> scoredSentences = setenceSimilarity.getMostSimilarSentences(question.body,
						abstractSentences);

				for (String key : scoredSentences.keySet()) {
					SnippetResult snippetResult = new SnippetResult();
					snippetResult.setBeginSection("abstract");
					snippetResult.setEndSection("abstract");
					snippetResult.setOffsetInBeginSection(abstractText.indexOf(key));
					snippetResult.setOffsetInEndSection(abstractText.indexOf(key) + key.length());
					snippetResult.setText(key);
					snippetResult.setScore(scoredSentences.get(key));
					snippetResult.setDocument(solrResult.getPmid());
					
					snippetsResult.add(snippetResult);
				}
			}
			
			if(titleText != null) {
				List<String> titleTextList = new ArrayList<>();
				titleTextList.add(titleText);
				Map<String, Double> scoredSentences = setenceSimilarity.getMostSimilarSentences(question.body,
						titleTextList);
				// will iterate only single time as title is one sentence
				for (String key : scoredSentences.keySet()) {
					SnippetResult snippetResult = new SnippetResult();
					snippetResult.setBeginSection("title");
					snippetResult.setEndSection("title");
					snippetResult.setOffsetInBeginSection(0);
					snippetResult.setOffsetInEndSection(key.length());
					snippetResult.setText(key);
					snippetResult.setScore(scoredSentences.get(key));
					snippetResult.setDocument(solrResult.getPmid());
					
					snippetsResult.add(snippetResult);
				}
			}
		}

		ArrayList<SnippetResult> selectedSnippets = getTopSnippets(snippetsResult);
		
		/*System.out.println();
		System.out.println("Question: " + question.body);
		
		for (SnippetResult snippetResult : selectedSnippets) {
			System.out.println(snippetResult.getText());
			System.out.println(snippetResult.getScore());
		}*/
		
		return getFinalResult(question, selectedSnippets, resultsList);
	}

	private static QuestionResult getFinalResult(InputQuestion question, ArrayList<SnippetResult> snippets, ArrayList<SolrResult> resultsList) {
		
		QuestionResult questionResult = new QuestionResult();
		String documents[] = new String[TOP_DOCUMENTS_TO_SELECT];
		
		for (int i = 0; i < resultsList.size(); i++) {
			SolrResult result = resultsList.get(i);
			documents[i] = "http://www.ncbi.nlm.nih.gov/pubmed/" + result.getPmid();
		}
		
		questionResult.setId(question.id);
		questionResult.setBody(question.body);
		questionResult.setDocuments(documents);
		questionResult.setType(question.type);
		questionResult.setSnippets(snippets.toArray(new SnippetResult[snippets.size()]));
		
		return questionResult;
	}
	
	private static ArrayList<SnippetResult> getTopSnippets(ArrayList<SnippetResult> snippetsResult) {
		
		Collections.sort(snippetsResult, new Comparator<SnippetResult>() {
	        @Override public int compare(SnippetResult p1, SnippetResult p2) {
	            return Double.compare(p2.getScore(), p1.getScore());
	        }
	    });
		
		ArrayList<SnippetResult> selectedSnippets = new ArrayList<>();
		int counter = 1;
		
		for (SnippetResult snippetResult : snippetsResult) {
			selectedSnippets.add(snippetResult);
			if(counter++==TOP_SENTENCES_TO_SELECT) break;
		}
		
		return selectedSnippets;
	}
}