package net.scyllamc.matan.prudence.learning;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import net.scyllamc.matan.prudence.utils.Utils;

public class HttpUtils {

	

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
			fin += Utils.newLine;
		}

		return fin;

	}
}
