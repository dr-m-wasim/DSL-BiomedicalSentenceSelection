package pk.edu.kics.dsl.entity;

import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class QuestionResult {
	private String body;
	private String[] documents;
	private String type;
	private String id;
	private SnippetResult[] snippets;
	
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String[] getDocuments() {
		return documents;
	}
	public void setDocuments(String[] documents) {
		this.documents = documents;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public SnippetResult[] getSnippets() {
		return snippets;
	}
	public void setSnippets(SnippetResult[] snippets) {
		this.snippets = snippets;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		
		JSONObject questionResult = new JSONObject();
		JSONArray documentsArray = new JSONArray();
		JSONArray snippetsArray = new JSONArray();
		documentsArray.addAll(Arrays.asList(documents));		
		
		for (SnippetResult snippetResult : snippets) {
			snippetsArray.add(snippetResult.toJSON());
		}
		
		questionResult.put("id", id);
		questionResult.put("body", body);
		questionResult.put("documents", documentsArray);
		questionResult.put("type", type);
		questionResult.put("snippets", snippetsArray);
		
		return questionResult;
	}
}
