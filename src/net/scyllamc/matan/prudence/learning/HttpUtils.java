package net.scyllamc.matan.prudence.learning;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.scyllamc.matan.prudence.main;

public class HttpUtils {

	public static String getHtmlClassText(String HTML, Website site) {

		String text = "";

		Document doc = Jsoup.parse(HTML);
		Elements pars = doc.select(site.getParType());

		if (HttpUtils.hasMetaData(doc, site)) {

			for (Element par : pars) {

				if (par.className().contains(site.getParIdentifier())) {
					text += par.text();
				}

			}
		}

		return text;
	}

	public static boolean hasMetaData(Document doc, Website site) {

		for (Element md : doc.select("meta[" + site.getMetaVar() + "]")) {

			if (md.attr("property").equalsIgnoreCase(site.getArticleMetaData())) {
				return true;
			}

		}

		return false;
	}

	public static String arrayToString(ArrayList<String> a) {

		String fin = "";

		for (String s : a) {
			fin += s;
			fin += main.newLine;
		}

		return fin;

	}
}
