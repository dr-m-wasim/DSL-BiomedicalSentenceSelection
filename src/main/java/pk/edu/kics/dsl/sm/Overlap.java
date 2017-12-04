package pk.edu.kics.dsl.sm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.parser.ParseException;

import pk.edu.kics.dsl.SentenceSelection;
import pk.edu.kics.dsl.util.SolrHelper;
import pk.edu.kics.dsl.util.StringHelper;

public class Overlap extends SentenceSimilarity {

	@Override
	public Map<String, Double> getMostSimilarSentences(String question, List<String> sentences) {

		Map<String, Double> scoredSentences = new HashMap<String, Double>();

		try {
			ArrayList<String> qtokens = StringHelper.solrTokenizer(question);

			for (String sentence : sentences) {
				ArrayList<String> stokens = StringHelper.solrTokenizer(sentence);

				try {
					scoredSentences.put(sentence, getOverlappedScore(qtokens, stokens));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return scoredSentences;
	}

	public double getOverlappedScore(ArrayList<String> questionTokens, ArrayList<String> sentenceTokens)
			throws IOException, ParseException {

		Set<String> intersection = new HashSet<String>(questionTokens);
		intersection.retainAll(sentenceTokens);
		double IDFSum = 0.0;
		SolrHelper solrHelper = new SolrHelper();

		int intersectionSize = intersection.size();
		if(intersection.size()==0) return 0;
		
		String terms = "";

		for (String term : intersection) {
			terms += term + ",";
		}
		terms = terms.substring(0, terms.length() - 1);

		ArrayList<HashMap<String, Integer>> statistics = solrHelper.getCorpusStatistics(terms);
		HashMap<String, Integer> documentFrequency = statistics.get(1);

		Iterator it = intersection.iterator();

		while (it.hasNext())
		{
			String key = (String) it.next();
			Integer df = SentenceSelection.TOTAL_DOCUMENTS; 
			
			
			if(documentFrequency.containsKey(key)){
				df = (Integer) documentFrequency.get(key); 
			} else {
				System.out.println(key);
			}
			
			double idfValue = (double) SentenceSelection.TOTAL_DOCUMENTS / df;
			double idf = Math.log10(idfValue);
			IDFSum = IDFSum + idf;

			it.remove(); // avoids a ConcurrentModificationException
		}

		return (float) IDFSum * intersectionSize / questionTokens.size();
	}
}