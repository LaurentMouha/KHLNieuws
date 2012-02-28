package be.khleuven.nieuws;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class KHLNieuwsActivity extends Activity {

	private List<Message> messages;
	private List<String> categories = new ArrayList<String>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		loadFeed();
		getAllCategories();
		sortByCategory();

	}

	private void getAllCategories() {
		categories.clear();
		for(Message m: messages){
			if(!categories.contains(m.getCategory())){
				categories.add(m.getCategory());
			}
		}
		
	}

	private void sortByCategory() {
		for(String c : categories){
			System.out.println(c);
			for(Message m: messages){
				if(c.equals(m.getCategory())){
					System.out.println(m.getTitle());
//					if(categories.size()>15){
//						categories.clear();
//					}
				}
			}
		}
	}

	public void loadFeed() {
		String feedUrl = "https://portaal.khleuven.be/rss.php";
		DomFeedParser parser = new DomFeedParser(feedUrl);
		long start = System.currentTimeMillis();
		messages = parser.parse();
		long duration = System.currentTimeMillis() - start;
		Log.i("AndroidNews", "Parser duration=" + duration);
		for (Message m : messages) {
			System.out.println(m.toString());
		}
	}

}