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

@SuppressWarnings("deprecation")
public class ArticleParseTask implements Runnable {

	private Element element;
	private HttpClient client;
	private Website site;
	private UUID taskID;

	ArticleParseTask(Element element, HttpClient client, Website site, UUID taskID) {
		this.element = element;
		this.taskID = taskID;
		this.site = site;
		this.client = client;
	}

	@Override
	public void run() {

		
		if (element.absUrl("href") != null && new UrlValidator(new String[] { "http", "https" }).isValid(element.absUrl("href")) && !element.absUrl("href").contains("video")) {

			try {
				final String url = element.absUrl("href");
				String HTML = EntityUtils.toString(client.execute(new HttpGet(url)).getEntity());
				String fin = "";
				
				Document doc = Jsoup.parse(HTML);
				Elements pars = doc.select(site.getParType());

				if (HttpUtils.hasMetaData(doc, site)) {

					for (Element par : pars) {

						if (par.className().contains(site.getParIdentifier())) {
							fin += par.text();
						}

					}
				}
				
				WebsiteFetcher.webFetchTasks.get(taskID).addArticleText(fin);
			} catch (ParseException | IOException e) {
				e.printStackTrace();
			}
		}

	}

}
