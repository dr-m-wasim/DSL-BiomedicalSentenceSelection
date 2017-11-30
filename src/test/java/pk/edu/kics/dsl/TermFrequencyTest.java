package pk.edu.kics.dsl;

import static junit.framework.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Map;

import org.junit.Test;

import pk.edu.kics.dsl.sm.TermFrequency;

public class TermFrequencyTest {
	@Test
	public void testGetMostSimilarSentences() {
		TermFrequency tf = new TermFrequency();
		ArrayList<String> s = new ArrayList<>();
		s.add(" is characterized by coronal craniosynostosis (bilateral more often than unilateral), hearing loss, developmental delay, and carpal and/or tarsal bone coalition.");
		s.add("Muenke syndrome caused by the FGFR3 Pro250Arg mutation is associated with craniosynostosis, hearing loss, and various bony anomalies");
		s.add("\"Muenke syndrome caused by the FGFR3(P250R) mutation is an autosomal dominant disorder mostly identified with coronal suture synostosis");

		Map<String, Double> result = tf.getMostSimilarSentences("What symptoms characterize the Muenke syndrome?", s);
		double[] expectedResults = { 0, 0.7039, 0.7039 };

		int counter = 0;
		for (String key : result.keySet()) {
			assertEquals(expectedResults[counter++], result.get(key), 0.09);
		}

	}

}
