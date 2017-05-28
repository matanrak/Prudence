package net.scyllamc.matan.prudence.learning;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonObject;

import net.scyllamc.matan.prudence.FileHandler;
import net.scyllamc.matan.prudence.LogHandler;
import net.scyllamc.matan.prudence.PTask;
import net.scyllamc.matan.prudence.TaskManager;
import net.scyllamc.matan.prudence.parser.ParseTask;

public class WebsiteFetcher implements PTask {

	public static HashMap<UUID, WebsiteFetcher> webFetchTasks = new HashMap<UUID, WebsiteFetcher>();

	private List<String> articles = Collections.synchronizedList(new ArrayList<String>());

	private UUID ID;
	private Website site;
	private boolean finished;
	private boolean started;

	public WebsiteFetcher(Website site) {
		this.ID = UUID.randomUUID();
		this.site = site;
		this.finished = false;
		this.started = false;
		webFetchTasks.put(this.ID, this);

		LogHandler.print(0, "Adding website fetcher task, ID: " + this.ID.toString());
		TaskManager.tasks.add(this);
	}

	@Override
	public void run() {
		try {

			HttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
			HttpGet request = new HttpGet(site.getURL());
			HttpResponse response = client.execute(request);

			String HTML = EntityUtils.toString(response.getEntity());

			Document doc = Jsoup.parse(HTML);
			Elements links = doc.select("a");

			int count = 0;
			int max = 100;

			ExecutorService executor = Executors.newFixedThreadPool(15);
			JsonObject article_history = FileHandler.Files.ARTICLE_HISTORY.getJson();
			Date date = new Date();

			for (final Element element : links) {
				if (count < max) {

					MessageDigest md = MessageDigest.getInstance("MD5");
					md.update(element.absUrl("href").getBytes("UTF-8"));
					String hash = new String(md.digest());

					if (!article_history.has(hash)) {
						JsonObject a = new JsonObject();
						a.addProperty("URL", element.absUrl("href"));
						a.addProperty("DATE", date.toString());
						article_history.add(hash, a);
						executor.execute(new ArticleParseTask(element, client, site, getID()));
					}else{
						LogHandler.print(1, "Article " + hash + " has been read before");
					}
					
					count++;
				}
			}

			executor.shutdown();
			while (!executor.isTerminated()) {
			}

			int length = 0;

			for (String s : articles) {
				length += s.length();
				new ParseTask(s);
			}

			FileHandler.Files.ARTICLE_HISTORY.setJson(article_history);

			finished = true;
			LogHandler.print(0, "Finished article fetch, Found " + articles.size() + " articles, total length: " + length);

		} catch (IOException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

	}

	public void addArticleText(String text) {
		this.articles.add(text);
	}

	@Override
	public UUID getID() {
		return this.ID;
	}

	@Override
	public long getElasped() {
		return 0;
	}

	@Override
	public boolean hasStarted() {
		return this.started;
	}

	@Override
	public boolean didFinish() {
		return this.finished;
	}

	@Override
	public double getPercentage() {
		return 0;
	}

	@Override
	public String getStatus() {
		return "NO STATUS MESSAGE";
	}

}
