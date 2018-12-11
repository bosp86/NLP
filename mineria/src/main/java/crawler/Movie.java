package crawler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.Data;

@Data
public class Movie {

	private String title;
	private String description;
	private String rating;
	private String date;
	private String link;
	private String genre;

	public String toString() {
		return "title ::= [" + this.title + "], rating ::= [" + this.rating + "], date ::= [" + this.date
				+ "], link ::= [ " + this.link + "] \n description ::= [" + this.description + "]";
	}

	
	public static void main(String[] args) throws IOException
	{
		System.out.println( new File(".").getCanonicalPath());
		
		Path path = Paths.get(new File(".").getCanonicalPath());
		
		System.out.println(path.getParent().toString());
	}
}
