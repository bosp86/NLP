package maestria;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MovieExtractor {

	/** References
	 * 
	 * https://towardsdatascience.com/a-practitioners-guide-to-natural-language-processing-part-i-processing-understanding-text-9f4abfd13e72
	 * https://stackoverflow.com/questions/18439795/nlp-machine-learning-text-comparison
	 * https://towardsdatascience.com/overview-of-text-similarity-metrics-3397c4601f50
	 * 
	 */
	
	
	public static void main(String[] args) throws Exception {

		// Parameters

		String movieDBDomain = "https://www.themoviedb.org";

		int numberOfPages = 10;
		System.out.println("Scrapping ::= " + movieDBDomain);
		System.out.println("Scrapping initiated at " + (new Date()));
		List<Movie> movieList = new ArrayList<Movie>();
		long init = System.currentTimeMillis();
		for (int i = 1; i <= numberOfPages; i++) {
			Document document = Jsoup.connect(movieDBDomain + "/movie?page=" + i).get();
			System.out.println("Obtained content from page(" + i + ")");

			Elements overviewDiv = document.select(".item.poster.card");

			for (Element page : overviewDiv) {

				Elements flex = page.select(".flex");
				Elements a = flex.select("a");
				String title = a.attr("title");
				String decription = page.select(".overview").text();
				String score = page.select(".user_score_chart").attr("data-percent");
				String link = a.attr("href");
				String date = flex.select("span").text();

				Thread.sleep(50);
//				document = Jsoup.connect(movieDBDomain + link).get();

				Movie movie = new Movie();
				movie.setTitle(title);
				movie.setDescription(decription);
				movie.setRating(score);
				movie.setLink(link);
				movie.setDate(date);

				movieList.add(movie);
			}
		}

		System.out.println("Scrapping process took ::= [" + (System.currentTimeMillis() - init) + " ms]");

		System.out.println("Movives found ::= " + movieList.size());
		
		writeToFile("movieDescriptionDataSet.tsv", movieList);
	}

	public static void writeToFile(String filename, List<Movie> movieList) {
		System.out.println("Init Writer");
		FileWriter writer;
		try {
			writer = new FileWriter(filename);
			
			writer.append("\"Title\"\t");
			writer.append("\"Description\"\t");
			writer.append("\"Rating\"\t");
			writer.append("\"Link\"\t");
			writer.append("\"Date\"\t");
			writer.append("\n");
			
			File f = new File(filename);
			System.out.println(f.getAbsolutePath());
			movieList.forEach(a -> {
				try {
//					String temp = "- Title: " + a.getTitle() + " (link: " + a.getLink() + ")\n";
					// display to console
//					System.out.println(temp);
					// save to file
//					writer.write(temp);
					
					writer.append("\""+a.getTitle()+"\"\t");
					writer.append("\""+a.getDescription()+"\"\t");
					writer.append("\""+a.getRating()+"\"\t");
					writer.append("\""+a.getLink()+"\"\t");
					writer.append("\""+a.getDate()+"\"\t");
					writer.append("\n");
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			});
			
			writer.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

}
