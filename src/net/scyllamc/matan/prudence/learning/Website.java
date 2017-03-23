package net.scyllamc.matan.prudence.learning;

public enum Website {

	CNN("http://edition.cnn.com", "div" , "paragraph", "content", "article"),
	NYCTIMES("https://www.nytimes.com", "p" , "story", "property", "article:collection");
	
	
	private String url;
	private String parclass;
	private String metadata;
	private String partype;
	private String metavar;
	
	Website(String url, String partype, String parclass, String metavar ,String metadata){
		this.url = url;
		this.partype = partype;
		this.parclass = parclass;
		this.metavar = metavar;
		this.metadata = metadata;
	}
	
	
	public String getURL(){
		return this.url;
	}
	
	public String getArticleMetaData(){
		return this.metadata;
	}
	
	public String getParIdentifier(){
		return this.parclass;
	}
	
	public String getParType(){
		return this.partype;
	}
	
	public String getMetaVar(){
		return this.metavar;
	}
	
}
