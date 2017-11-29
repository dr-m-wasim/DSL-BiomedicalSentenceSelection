package pk.edu.kics.dsl.sm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import org.apache.hadoop.util.hash.Hash;
import org.apache.solr.common.SolrDocument;
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
					scoredSentences.put(sentence,getOverlappedScore(qtokens, stokens));
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

	
	
	
	
	public double getOverlappedScore(ArrayList<String> questionTokens, ArrayList<String> sentenceTokens) throws IOException, ParseException {
		//Set<String> question = new HashSet<String>(questionTokens);
		//question.addAll(sentenceTokens);

		Set<String> intersection = new HashSet<String>(questionTokens);
		intersection.retainAll(sentenceTokens);
        double IDFSum =0.0;
        SolrHelper solrHelper = new SolrHelper();
       
        String terms = "";
        
        for (String term : intersection) {
			terms += term + ",";
		}
        terms=terms.substring(0, terms.length() - 1);
        
        ArrayList<HashMap<String, Integer>> statistics = solrHelper.getCorpusStatistics(terms);
        HashMap<String, Integer> documentFrequency = statistics.get(1);
      
			
           // HashMap<String, Integer> tmpData =documentFrequency;
            Set<String> key = documentFrequency.keySet();
             Iterator it = key.iterator();
                while (it.hasNext()) 
                
                {
                String Key = (String)it.next();
                Integer Data = (Integer) documentFrequency.get(Key);
                
                double test1=(double)SentenceSelection.TOTAL_DOCUMENTS /Data;
                double logtest=Math.log10(test1);
                IDFSum= IDFSum + Math.log10((double)SentenceSelection.TOTAL_DOCUMENTS /Data) ;
               
                
                it.remove(); // avoids a ConcurrentModificationException
            }

         
		
	return (float) IDFSum * intersection.size() / questionTokens.size();
		
	}
	
	
	
	
}
