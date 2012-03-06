package be.khleuven.nieuws;
import java.util.List;

public interface FeedParser {
	/**
	 * Parst de xml en geeft een List van Messages terug.
	 * @return Lijst van messages.
	 */
	List<Message> parse();
}
