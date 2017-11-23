package pk.edu.kics.dsl.sm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pk.edu.kics.dsl.util.StringHelper;

public class Jaccard extends SentenceSimilarity {

	@Override
	public Map<String, Double> getMostSimilarSentences(String question, ArrayList<String> sentences) {

		Map<String, Double> scoredSentences = new HashMap<String, Double>();

		try {
			ArrayList<String> qtokens = StringHelper.solrTokenizer(question);

			for (String sentence : sentences) {
				ArrayList<String> stokens = StringHelper.solrTokenizer(sentence);

				scoredSentences.put(sentence,getJaccardScore(qtokens, stokens));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return scoredSentences;
	}

	public double getJaccardScore(ArrayList<String> questionTokens, ArrayList<String> sentenceTokens) {
		Set<String> union = new HashSet<String>(questionTokens);
		union.addAll(sentenceTokens);

		Set<String> intersection = new HashSet<String>(questionTokens);
		intersection.retainAll(sentenceTokens);

		return (float) intersection.size() / union.size();
	}

}
