package nlp;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import nlp.service.ScrapperService;

/**
 * Scrapper
 */
public class Scrapper {

    public static void main(String[] args) throws IOException {

        final Document document = ScrapperService.name();

        for (Element row : document.select("table.chart.full-width tr")) {
            if (row != null) {
                final String tittle = row.select(".titleColumn a").text();
                final String rating = row.select(".imdbRating").text();
                System.out.println(String.format(" tittle [%s] , rating [%s]", tittle, rating));
            }
        }

    }

}