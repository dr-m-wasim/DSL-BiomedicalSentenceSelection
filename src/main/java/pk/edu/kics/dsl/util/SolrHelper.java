package pk.edu.kics.dsl.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import pk.edu.kics.dsl.SentenceSelection;
import pk.edu.kics.dsl.entity.*;

public class SolrHelper {

	static String urlString = "http://" + SentenceSelection.SOLR_SERVER + ":8983/solr/" + SentenceSelection.SOLR_CORE;
	static SolrClient solr;
	static SolrQuery solrQuery;

	public SolrHelper() {
		solr = new HttpSolrClient.Builder(urlString).build();
		solrQuery = new SolrQuery();
	}

	public ArrayList<SolrResult> submitQuery(InputQuestion question, int start, int rowNo)
			throws SolrServerException, IOException {

		solrQuery.setQuery(StringHelper.formulateQuery(question.body));
		solrQuery.setRequestHandler("/select");
		solrQuery.setStart(start);
		solrQuery.setRows(rowNo);
		solrQuery.set("fl", "", "score", "pmid", "abstracttext", "articletitle");

		QueryResponse response = solr.query(solrQuery);
		SolrDocumentList documentList = response.getResults();
		ArrayList<SolrResult> resultsList = new ArrayList<SolrResult>();
		int rank = 1;

		for (SolrDocument document : documentList) {

			SolrResult solrResult = new SolrResult();

			solrResult.setTopicId(String.valueOf(question.id));
			String pmid = document.get("pmid").toString();
			solrResult.setPmid(pmid.replace("[", "").replace("]", ""));

			solrResult.setRank(rank++);
			solrResult.setStartOffset(0);
			solrResult.setLength(0);
			solrResult.setScore(Float.parseFloat(document.get("score").toString()));

			if (document.get("articletitle") != null) {
				solrResult.setTitle(document.get("articletitle").toString());
			}

			if (document.get(SentenceSelection.CONTENT_FIELD) != null) {
				solrResult.setContent(document.get(SentenceSelection.CONTENT_FIELD).toString().replaceAll("\\[", "")
						.replaceAll("\\]", ""));
			}

			resultsList.add(solrResult);
		}

		return resultsList;
	}

	public ArrayList<HashMap<String, Integer>> getCorpusStatistics(String terms) throws IOException, ParseException {

		String urlParameters = "terms.list=" + terms;
		String url = urlString + "/terms?wt=json&terms.fl=" + SentenceSelection.CONTENT_FIELD + "&terms.ttf=true";
		String response = HttpHelper.getResponse(url, urlParameters);
		JSONParser parser = new JSONParser();

		JSONObject termsObject = (JSONObject) parser.parse(response);
		JSONObject body = (JSONObject) termsObject.get(SentenceSelection.CONTENT_FIELD);

		return getTTFDFDictionary(body);
	}

	public static ArrayList<HashMap<String, Integer>> getTTFDFDictionary(JSONObject body) {

		HashMap<String, Integer> totalTermFrequency = new HashMap<String, Integer>();
		HashMap<String, Integer> documentFrequency = new HashMap<String, Integer>();
		ArrayList<HashMap<String, Integer>> statistics = new ArrayList<HashMap<String, Integer>>();

		Iterator<?> keys = body.keySet().iterator();

		while (keys.hasNext()) {
			String key = (String) keys.next();
			JSONObject df_ttf = (JSONObject) body.get(key);
			documentFrequency.put(key, Integer.parseInt(df_ttf.get("df").toString()));
			totalTermFrequency.put(key, Integer.parseInt(df_ttf.get("ttf").toString()));
		}

		statistics.add(totalTermFrequency);
		statistics.add(documentFrequency);

		return statistics;
	}

	public static HashMap<String, Integer> getCorpusTermsFrequency(ArrayList<String> terms)
			throws IOException, ParseException, SolrServerException {

		HashMap<String, Integer> termsFrequency = new HashMap<>();

		ArrayList<String> tempTermsList = new ArrayList<>();
		for (int i = 0; i < terms.size(); i++) {
			tempTermsList.add("ttf(" + SentenceSelection.CONTENT_FIELD + "," + terms.get(i) + ")");

			// SOLR not accepting all terms together - so sending chunks of 4000
			if (tempTermsList.size() == 20) {
				String concatenateTerms = String.join(",", tempTermsList);
				termsFrequency.putAll(getCorpusTermsFrequencySubset(concatenateTerms));
				System.out.println(concatenateTerms);
				tempTermsList.clear();
			}
		}

		if (tempTermsList.size() > 0) {
			String concatenateTerms = String.join(",", tempTermsList);
			termsFrequency.putAll(getCorpusTermsFrequencySubset(concatenateTerms));
			tempTermsList.clear();
		}

		return termsFrequency;
	}

	private static HashMap<String, Integer> getCorpusTermsFrequencySubset(String terms)
			throws IOException, ParseException {
		String urlParameters = "fl=" + terms;
		String url = urlString + "/select?q=*:*&rows=1&wt=json&indent=true";
		String response = HttpHelper.getResponse(url, urlParameters);
		JSONParser parser = new JSONParser();

		JSONObject jsonObject = (JSONObject) parser.parse(response);
		JSONObject jsonResponse = (JSONObject) jsonObject.get("response");
		JSONObject docs = (JSONObject) (((JSONArray) jsonResponse.get("docs")).get(0));

		return parseCorpusTermFrequency(docs);
	}

	public static HashMap<String, Integer> parseCorpusTermFrequency(JSONObject docs) {
		HashMap<String, Integer> allTerms = new HashMap<>();
		Iterator<?> keys = docs.keySet().iterator();

		while (keys.hasNext()) {
			String fieldKey = (String) keys.next();

			String key = fieldKey.split(",")[1];
			key = key.substring(0, key.length() - 1);

			allTerms.put(key, (int) docs.get(fieldKey));
		}

		return allTerms;
	}

	public static long getCorpusTermsFrquencySum() throws IOException, ParseException {
		String url = urlString + "/select?q=*:*&rows=1&wt=json&indent=true&fl=sttf(body)";
		String response = HttpHelper.getResponse(url, "");
		JSONParser parser = new JSONParser();

		JSONObject jsonObject = (JSONObject) parser.parse(response);
		JSONObject jsonResponse = (JSONObject) jsonObject.get("response");
		JSONObject docs = (JSONObject) (((JSONArray) jsonResponse.get("docs")).get(0));

		return parseCorpusTermTotalFrequency(docs);
	}

	public static long parseCorpusTermTotalFrequency(JSONObject docs) {

		long totalTermFrequency = 0;
		Iterator<?> keys = docs.keySet().iterator();

		while (keys.hasNext()) {
			String fieldKey = (String) keys.next();
			String tempValue = String.valueOf(docs.get(fieldKey));
			totalTermFrequency = Long.parseLong(tempValue);
		}

		return totalTermFrequency;
	}

}