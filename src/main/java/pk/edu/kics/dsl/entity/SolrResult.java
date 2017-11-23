package pk.edu.kics.dsl.entity;

public class SolrResult {
	
	private String topicId;
	private String pmid;
	private int rank;
	private int startOffset;
	private int length;
	private float score;
	private String title;
	private String content;
	
	public SolrResult() {}
	
	public SolrResult(String topicId, String pmid, int rank, int startOffset, int length, float score, String content) {
		this.topicId = topicId;
		this.pmid = pmid;
		this.rank = rank;
		this.startOffset = startOffset;
		this.length = length;
		this.score = score;
		this.content = content;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTopicId() {
		return topicId;
	}
	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}
	public String getPmid() {
		return pmid;
	}
	public void setPmid(String pmid) {
		this.pmid = pmid;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public int getStartOffset() {
		return startOffset;
	}
	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}
	
	
}
