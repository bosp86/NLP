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


	public static void main(String[] args) throws Exception {

		// Parameters

		String movieDBDomain = "https://www.themoviedb.org";

		int numberOfPages = 10;
		System.out.println("Scrapping ::= " + movieDBDomain);
		System.out.println("Scrapping initiated at " + (new Date()));
		
		long init = System.currentTimeMillis();
		
		
		
		String fullPathName = createFileWithHeaders("movieDescriptionDataSet.tsv");
		
		
		for (int i = 1; i <= numberOfPages; i++) {
			Document document = Jsoup.connect(movieDBDomain + "/movie?page=" + i).get();
			System.out.println("Obtained content from page(" + i + ")");

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

				Elements geners = document.select(".genres.right_column").select("li");

				String genresStr = "";
				
				if (geners.isEmpty() == false) {
					
					for(Element genre : geners)
					{
						
						if( genre.text() != null && !"".equals(genre.text().trim()))
						{
							genresStr += genre.text().toUpperCase() + ",";
						}
					}
					
				}

				String decription = document.select(".overview").select("p").text();
				
				if((!"".equals(genresStr.trim())) &&  decription != null && (!"".equals(decription.trim())))
				{
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
			
			System.out.println("Writing ["+movieList.size() +"] entries to file from page: (" + i + ")");
			addToFile(fullPathName, movieList);
			
			movieList = null;
		}
		int seconds = (int)(((System.currentTimeMillis() - init) / 1000) % 60);
		System.out.println("Scrapping process took ::= [" + seconds + " seconds]");

//		System.out.println("Movives found ::= " + movieList.size());

		
	}
	
	public static String createFileWithHeaders(String filename) {
		System.out.println("Init Writer");
		FileWriter writer;
		String fullPathName = null;
		try {
			fullPathName = new File(".").getCanonicalPath() + System.getProperty("file.separator") + "src"
					+ System.getProperty("file.separator") + "main" +

					System.getProperty("file.separator") + "resources" + System.getProperty("file.separator")
					+ filename;
			System.out.println(" fullPathName :: = " + fullPathName);
			writer = new FileWriter(fullPathName);

			writer.append("\"Title\"\t");
			writer.append("\"Description\"\t");
			writer.append("\"Genre\"\t");
			writer.append("\"Rating\"\t");
			writer.append("\"Link\"\t");
			writer.append("\"Date\"");
			writer.append("\n");

			File f = new File(filename);
			System.out.println(f.getAbsolutePath());

			writer.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		return fullPathName;
	}
	

	public static void addToFile(String fullPathName, List<Movie> movieList) {
		System.out.println("Init Writer");
		FileWriter writer;
		try {
			writer = new FileWriter(fullPathName, true);

			movieList.forEach(a -> {
				try {
					// String temp = "- Title: " + a.getTitle() + " (link: " +
					// a.getLink() + ")\n";
					// display to console
					// System.out.println(temp);
					// save to file
					// writer.write(temp);

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
	}

}
