package net.scyllamc.matan.prudence.learning;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.validator.UrlValidator;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.scyllamc.matan.prudence.LogHandler;

@SuppressWarnings("deprecation")
public class ArticleParseTask implements Runnable {

	private String url;
	private HttpClient client;
	private Website site;
	private UUID taskID;

	ArticleParseTask(String url, HttpClient client, Website site, UUID taskID) {
		this.url = url;
		this.taskID = taskID;
		this.site = site;
		this.client = client;
	}

	@Override
	public void run() {

		if (url != null && new UrlValidator(new String[] { "http", "https" }).isValid(url) && !url.contains("video")) {

			try {

				String HTML = EntityUtils.toString(client.execute(new HttpGet(url)).getEntity());
				String fin = "";

				Document doc = Jsoup.parse(HTML);
				Elements pars = doc.select(site.getParType());

				if (hasMetaData(doc, site)) {

					for (Element par : pars) {

						if (par.className().contains(site.getParIdentifier())) {
							fin += par.text();
						}

					}
				}else{
					LogHandler.print(3, "NO META DATA: " + url);
				}

				WebsiteFetcher.webFetchTasks.get(taskID).addArticleText(fin, url);
			} catch (ParseException | IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static boolean hasMetaData(Document doc, Website site) {

		for (Element md : doc.select("meta[" + site.getMetaVar() + "]")) {
			if (md.attr(site.getMetaVar()).equalsIgnoreCase(site.getArticleMetaData())) {
				return true;
			}
		}
		return false;
	}
	
}
