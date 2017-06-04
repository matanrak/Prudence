package net.scyllamc.matan.prudence.learning;

public enum Website {

	CNN("http://edition.cnn.com", true, "div", "paragraph", "itemprop", "articleSection"),
	WP("https://www.washingtonpost.com", false, "p", null, "content", "article"),
	NYCTIMES("https://www.nytimes.com", false, "p", "story", "property", "article:collection");

	public static Website fromString(String text) {
		if (text == null) {
			return null;
		}

		for (Website site : Website.values()) {
			if (text.toUpperCase().equalsIgnoreCase(site.toString().toUpperCase())) {
				return site;
			}
		}

		return null;
	}

	private String url;
	private boolean urlPrefix;
	private String parclass;
	private String metadata;
	private String partype;
	private String metavar;

	Website(String url, boolean urlPrefix, String partype, String parclass, String metavar, String metadata) {
		this.url = url;
		this.urlPrefix = urlPrefix;
		this.partype = partype;
		this.parclass = parclass;
		this.metavar = metavar;
		this.metadata = metadata;
	}

	public String getURL() {
		return this.url;
	}

	public String getArticleMetaData() {
		return this.metadata;
	}

	public String getParIdentifier() {
		return this.parclass;
	}

	public String getParType() {
		return this.partype;
	}

	public String getMetaVar() {
		return this.metavar;
	}
	
	public boolean hasURLPrefix(){
		return this.urlPrefix;
	}

}
