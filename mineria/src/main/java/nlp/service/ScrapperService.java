package nlp.service;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * ScrapperService
 */
public class ScrapperService {

    public static Document name() throws IOException {

        final Document document = Jsoup.connect("https://www.imdb.com/chart/top").get();

        return document;
    }
}