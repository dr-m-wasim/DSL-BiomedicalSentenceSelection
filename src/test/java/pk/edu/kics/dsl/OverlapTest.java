package pk.edu.kics.dsl;

import static junit.framework.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Map;

import org.junit.Test;

import pk.edu.kics.dsl.sm.Overlap;

public class OverlapTest {
	@Test
	public void testGetMostSimilarSentences() {
		Overlap o = new Overlap();
		ArrayList<String> s = new ArrayList<>();
		s.add("Muenke syndrome is characterized by coronal craniosynostosis (bilateral more often than unilateral), hearing loss, developmental delay, and carpal and/or tarsal bone coalition.");
		s.add("Muenke syndrome caused by the FGFR3 Pro250Arg mutation is associated with craniosynostosis, hearing loss, and various bony anomalies");
		s.add("\"Muenke syndrome caused by the FGFR3(P250R) mutation is an autosomal dominant disorder mostly identified with coronal suture synostosis");
		
		Map<String, Double> result = o.getMostSimilarSentences("What symptoms characterize the Muenke syndrome?", s);
		double[] expectedResults = {2.5142, 3.8867, 3.8867}; 
		
		int counter = 0;
		for (String key : result.keySet()) {
			assertEquals(expectedResults[counter++], result.get(key), 0.09); 
		}
	}
}