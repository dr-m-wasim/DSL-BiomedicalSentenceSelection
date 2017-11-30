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

public class TermFrequency extends SentenceSimilarity {

	@Override
	public Map<String, Double> getMostSimilarSentences(String question, List<String> sentences) {

		Map<String, Double> scoredSentences = new HashMap<String, Double>();

		try {
			ArrayList<String> qtokens = StringHelper.solrTokenizer(question);

			for (String sentence : sentences) {
				ArrayList<String> stokens = StringHelper.solrTokenizer(sentence);

				try {
					scoredSentences.put(sentence, getTermFrequencyScore(qtokens, stokens));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return scoredSentences;
	}

	public double getTermFrequencyScore(ArrayList<String> questionTokens, ArrayList<String> sentenceTokens)
			throws IOException, ParseException {

		Set<String> intersection = new HashSet<String>(questionTokens);
		intersection.retainAll(sentenceTokens);
		double TF_IDF = 0.0;
		SolrHelper solrHelper = new SolrHelper();

		String terms = "";

		for (String term : intersection) {
			terms += term + ",";
		}
		terms = terms.substring(0, terms.length() - 1);

		ArrayList<HashMap<String, Integer>> statistics = solrHelper.getCorpusStatistics(terms);
		HashMap<String, Integer> documentFrequency = statistics.get(1);

		Set<String> keyset = documentFrequency.keySet();
		Iterator it = keyset.iterator();
		
		
		
		
		while (it.hasNext()) {

			String Key = (String) it.next();
			Integer Data = (Integer) documentFrequency.get(Key);
			int questionTermFreq = 0;
			int sentenceTermFreq = 0;

			for (String questionTerms : questionTokens) {
				if (questionTerms.equals(Key)) {
					questionTermFreq++;
				}
			}
			for (String sentenceTerms : sentenceTokens) {
				if (sentenceTerms.equals(Key)) {
					sentenceTermFreq++;
				}
			}

			TF_IDF = TF_IDF + Math.log10(questionTermFreq + 1) * Math.log10(sentenceTermFreq + 1)
					* Math.log10((double) (SentenceSelection.TOTAL_DOCUMENTS + 1) / (Data + 0.5));

			it.remove();
		}

		return (float) TF_IDF;

	}

}
