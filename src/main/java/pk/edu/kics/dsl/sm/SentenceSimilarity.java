package pk.edu.kics.dsl.sm;

import java.util.ArrayList;
import java.util.Map;

public abstract class SentenceSimilarity {
	abstract public Map<String, Double> getMostSimilarSentences(String question, ArrayList<String> sentences);
}
