package pk.edu.kics.dsl.entity;

public class SnippetResult {
	private int offsetInBeginSection;
	private int offsetInEndSection;
	private String text;
	private String beginSection;
	private String document;
	private String endSection;
	private double score;
	
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public int getOffsetInBeginSection() {
		return offsetInBeginSection;
	}
	public void setOffsetInBeginSection(int offsetInBeginSection) {
		this.offsetInBeginSection = offsetInBeginSection;
	}
	public int getOffsetInEndSection() {
		return offsetInEndSection;
	}
	public void setOffsetInEndSection(int offsetInEndSection) {
		this.offsetInEndSection = offsetInEndSection;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getBeginSection() {
		return beginSection;
	}
	public void setBeginSection(String beginSection) {
		this.beginSection = beginSection;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = "http://www.ncbi.nlm.nih.gov/pubmed/" + document;
	}
	public String getEndSection() {
		return endSection;
	}
	public void setEndSection(String endSection) {
		this.endSection = endSection;
	}
}
