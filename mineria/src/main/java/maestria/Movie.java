package maestria;

import lombok.Data;

@Data
public class Movie {

	private String title;
	private String description;
	private String rating;
	private String date;
	private String link;

	public String toString() {
		return "title ::= [" + this.title + "], rating ::= [" + this.rating + "], date ::= [" + this.date
				+ "], link ::= [ " + this.link + "] \n description ::= [" + this.description + "]";
	}

}
