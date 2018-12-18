package crawler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MovieExtractor {

	/**
	 * CRAWLER PARAMETERS
	 */

	private static final int NUMBER_OF_PAGES = 150;
	private static final boolean CREATE_NEW_FILE = true;

	public static void main(String[] args) throws Exception {

		String movieDBDomain = "https://www.themoviedb.org";

		System.out.println("Scrapping ::= " + movieDBDomain);
		System.out.println("Scrapping initiated at " + (new Date()));

		long init = System.currentTimeMillis();

		String fullPathName = createFileWithHeaders("movieDescriptionDataSet.tsv");
		long scrappedPages  = 0;
		for (int i = 1; i <= NUMBER_OF_PAGES; i++) {
			Document document = Jsoup.connect(movieDBDomain + "/movie?page=" + i).get();
			scrappedPages ++;

			Elements overviewDiv = document.select(".item.poster.card");
			List<Movie> movieList = new ArrayList<Movie>();
			for (Element page : overviewDiv) {

				Elements flex = page.select(".flex");
				Elements a = flex.select("a");
				String title = a.attr("title");

				String score = page.select(".user_score_chart").attr("data-percent");
				String link = a.attr("href");
				String date = flex.select("span").text();

				Thread.sleep(50);
				document = Jsoup.connect(movieDBDomain + link).get();
				scrappedPages ++;
				Elements geners = document.select(".genres.right_column").select("li");

				String genresStr = "";

				if (geners.isEmpty() == false) {
					int size = 1;
					for (Element genre : geners) {

						if (genre.text() != null && !"".equals(genre.text().trim())) {
							genresStr += genre.text().toUpperCase() + (size == geners.size()?"":",");
						}
						size++;
					}

				}

				String decription = document.select(".overview").select("p").text();

				if ((!"".equals(genresStr.trim())) && decription != null && (!"".equals(decription.trim()))) {
					Movie movie = new Movie();
					movie.setTitle(title);
					movie.setDescription(decription);
					movie.setRating(score);
					movie.setLink(link);
					movie.setDate(date);
					movie.setGenre(genresStr);

					movieList.add(movie);
				}

			}

			System.out.println("\nWriting [" + movieList.size() + "] entries to file");
			if (movieList.size() > 0) {
				long fileSize = addToFile(fullPathName, movieList);
				System.out.println("File size -> " + fileSize / 1024 + " KB , Scrapped pages -> " + scrappedPages);
			}

			movieList = null;
		}
		
		long millis = System.currentTimeMillis() - init;
		System.out.println("\nScrapping process took -> [" + String.format("%02d min, %02d sec", 
			    TimeUnit.MILLISECONDS.toMinutes(millis),
			    TimeUnit.MILLISECONDS.toSeconds(millis) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
			) + "]");
		System.out.println("Number of pages scrapped -> [" + scrappedPages+"]");
		
	}

	public static String createFileWithHeaders(String filename) {
		FileWriter writer;
		String fullPathName = null;
		try {
			fullPathName = Paths.get(new File(".").getCanonicalPath()).getParent().toString()
					+ System.getProperty("file.separator") + "notebooks" + System.getProperty("file.separator")
					+ "dataset" + System.getProperty("file.separator") + filename;
			
			writer = new FileWriter(fullPathName);

			writer.append("\"Title\"\t");
			writer.append("\"Description\"\t");
			writer.append("\"Genre\"\t");
			writer.append("\"Rating\"\t");
			writer.append("\"Link\"\t");
			writer.append("\"Date\"");
			writer.append("\n");

			System.out.println("File created at -> " + fullPathName);
			writer.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		return fullPathName;
	}

	public static long addToFile(String fullPathName, List<Movie> movieList) {
		FileWriter writer;
		try {
			writer = new FileWriter(fullPathName, true);

			movieList.forEach(a -> {
				try {
					writer.append("\"" + a.getTitle() + "\"\t");
					writer.append("\"" + a.getDescription() + "\"\t");
					writer.append("\"" + a.getGenre() + "\"\t");
					writer.append("\"" + a.getRating() + "\"\t");
					writer.append("\"" + a.getLink() + "\"\t");
					writer.append("\"" + a.getDate() + "\"");
					writer.append("\n");
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			});
			
			writer.close();
			
			
			
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		return new File(fullPathName).length();
	}

}
