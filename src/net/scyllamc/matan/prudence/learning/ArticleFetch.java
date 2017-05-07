package net.scyllamc.matan.prudence.learning;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.apache.commons.validator.UrlValidator;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.scyllamc.matan.prudence.utils.Utils;

@SuppressWarnings("deprecation")
public class ArticleFetch implements Callable<String> {

	private Website site;
	private String text;

	public ArticleFetch(Website site) {
		this.site = site;
		this.text = "EMPTY";
	}

	@Override
	public String call() throws Exception {

		try {
			text = "";

			final HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(site.getURL());

			HttpResponse response = client.execute(request);
			String HTML = EntityUtils.toString(response.getEntity());

			Document doc = Jsoup.parse(HTML);
			Elements links = doc.select("a");

			System.out.print("Searching URL: " + site.getURL() + Utils.newLine);

			int count = 0;
			int max = 50;
			int min = 0;

			for (final Element el : links) {

				if (count < max) {

					if (count > min && el.absUrl("href") != null && new UrlValidator(new String[] { "http", "https" }).isValid(el.absUrl("href")) && !el.absUrl("href").contains("video")) {

						final String elurl = el.absUrl("href");
						System.out.print("	Sub URL: " + elurl + Utils.newLine);

						String elHTML = EntityUtils.toString(client.execute(new HttpGet(elurl)).getEntity());
						String urltext = HttpUtils.getHtmlClassText(elHTML, site);

						System.out.print("		text lenght: " + urltext.length() + Utils.newLine);

						text +=  " " + urltext;
						//UI.inputProb.setText(text);

					}

					count++;
				}

			}

			return text;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "EMPTY";
	}

}