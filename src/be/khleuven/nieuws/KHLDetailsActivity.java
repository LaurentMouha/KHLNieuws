package be.khleuven.nieuws;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/**
 * @author Laurent Mouha, Robin Vrebos en Bram Miermans
 *
 */
public class KHLDetailsActivity  extends Activity {
	
	String title;
	String description;
	String date;
	String category;
	String author;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);
		
		// Info die is doorgegeven uit intent halen.
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		
		System.out.println(extras.keySet().toString());
		category = extras.getString("cat");
		if (category.equals("G&amp;T")) {
			category = "G&T";
		}
		title = extras.getString("title");
		String[] temp = title.split(":");
		if(temp[0].contains(category)){
			StringBuilder sb = new StringBuilder();
			for(int j = 1;j<temp.length;j++){
				sb.append(temp[j] + ":");
			}
			sb.deleteCharAt(sb.length()-1);
			title = sb.toString();
			title.trim();
			
		}
		description = extras.getString("descr");
		date = extras.getString("date");
		author = extras.getString("auth");
		
		SetLayout();
		
		}

	/**
	 * Vult de TextViews in en zorgt ervoor dat je kan scrollen.
	 */
	private void SetLayout() {
		
		TextView tv = new TextView(this);
		
		tv = (TextView) findViewById(R.id.title);
		tv.setText(title);
		
		tv = (TextView) findViewById(R.id.author);
		tv.setText("Author: " + author);
		
		tv = (TextView) findViewById(R.id.cat);
		tv.setText(category);
		
		tv = (TextView) findViewById(R.id.date);
		tv.setText(date);
		
		tv = (TextView) findViewById(R.id.description);
	    tv.setMovementMethod(LinkMovementMethod.getInstance());
		tv.setText(Html.fromHtml(description));
		
	}

	

}
