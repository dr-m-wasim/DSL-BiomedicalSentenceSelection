package pk.edu.kics.dsl.entity;

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
}
