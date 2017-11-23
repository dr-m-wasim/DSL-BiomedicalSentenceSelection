package pk.edu.kics.dsl;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.junit.Test;

import pk.edu.kics.dsl.sm.Jaccard;
import pk.edu.kics.dsl.util.StringHelper;

public class JaccardTest {

	@Test
	public void testGetMostSimilarSentences() {
		Jaccard j = new Jaccard();
		ArrayList<String> s = new ArrayList<>();
		s.add("I love mac");
		s.add("Apple developed mac");
		s.add("Apple is a company");
		
		Map<String, Double> result = j.getMostSimilarSentences("Which company developed mac?", s);
		double[] expectedResults = {0.16, 0.40, 0.14}; 
		
		int counter = 0;
		for (String key : result.keySet()) {
			assertEquals(expectedResults[counter++], result.get(key), .009); 
		}

	}

	@Test
	public void testGetJaccardScore() throws IOException {

		String qString = "What is apple";
		String sString = "Apple is a company";
		Jaccard j = new Jaccard();

		ArrayList<String> question = StringHelper.solrTokenizer(qString);
		ArrayList<String> sentence = StringHelper.solrTokenizer(sString);

		double score = j.getJaccardScore(question, sentence);

		assertEquals(0.4, score, 0.009);
	}
}
