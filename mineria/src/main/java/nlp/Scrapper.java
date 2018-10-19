package nlp;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Scrapper
 */
public class Scrapper {

    public static void main(String[] args) throws IOException {

        final Document document = Jsoup.connect("https://www.imdb.com/chart/top").get();

        for (Element row : document.select("table.chart.full-width tr")) {
            if (row != null) {
                final String tittle = row.select(".titleColumn a").text();
                final String rating = row.select(".imdbRating").text();
                System.out.println(String.format(" tittle [%s] , rating [%s]", tittle, rating));
            }
        }

    }

}