import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Deque;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadPageNames {

	private static final Pattern TITLE_PATTERN = Pattern.compile("<li><a href=\".*\" title=\".*\">(.*)</a></li>");
	private static final Pattern NEXT_PAGE_PATTERN = Pattern.compile(".*<a href=\"(.*)\" title=\"Special:AllPages\">Next page .*</a></div></div>");
	private static final String WIKI_URL = "https://wiki.eclipse.org/";
	private static final String STARTING_PAGE = WIKI_URL + "Special:AllPages";

	public static void main(String[] args) throws Exception {
		Deque<String> pages = new LinkedList<>();
		pages.add(STARTING_PAGE);
		while (!pages.isEmpty()) {
			String currentPage = pages.removeFirst();
			URL url = new URL(currentPage);
			try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					Matcher titleMatcher = TITLE_PATTERN.matcher(inputLine);
					if (titleMatcher.matches()) {
						String pageName = titleMatcher.group(1);
						System.out.println(pageName);
						continue;
					}
					Matcher nextPageMatcher = NEXT_PAGE_PATTERN.matcher(inputLine);
					if (nextPageMatcher.matches()) {
						String nextPageUrlSuffix = nextPageMatcher.group(1);
						nextPageUrlSuffix = nextPageUrlSuffix.replaceAll("&amp;", "&");
						//System.out.println(nextPageUrlSuffix);
						String nextPageUrl = WIKI_URL + nextPageUrlSuffix;
						pages.addLast(nextPageUrl);
						continue;
					}
				}
			}
		}
	}
}
