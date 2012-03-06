package be.khleuven.nieuws;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

/**
 * @author Laurent Mouha, Robin Vrebos en Bram Miermans
 * 
 */
public class KHLNieuwsActivity extends ExpandableListActivity {

	/**
	 * Linkt de ExpandableListView met de data
	 */
	ExpandableListAdapter mAdapter;
	/**
	 * De url van de feed.
	 */
	String feedUrl;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new MyExpandableListAdapter();
		Intent i = getIntent();
		feedUrl = i.getStringExtra("feedUrl");
		((MyExpandableListAdapter) mAdapter).populate(feedUrl);
		setListAdapter(mAdapter);
		registerForContextMenu(getExpandableListView());

	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {

		Message m = (Message) mAdapter.getChild(groupPosition, childPosition);

		/*
		 * start nieuwe intent voor detailspagina
		 */

		Intent intent = new Intent(getApplicationContext(),
				KHLDetailsActivity.class);
		intent.putExtra("title", m.getTitle());
		intent.putExtra("date", m.getDate());
		intent.putExtra("cat", m.getCategory());
		intent.putExtra("auth", m.getAuthor());
		intent.putExtra("descr", m.getDescription());
		startActivity(intent);

		return true;
	}

	/**
	 * @author Laurent Mouha, Robin Vrebos en Bram Miermans
	 * 
	 */
	public class MyExpandableListAdapter extends BaseExpandableListAdapter {

		/**
		 * Alle berichten, ongeordend.
		 */
		private List<Message> messages;
		/**
		 * Alle categorien.
		 */
		private List<String> groups = new ArrayList<String>();
		/**
		 * Lijst die lijst van berichten per categorie bevat
		 */
		private List<List<Message>> children = new ArrayList<List<Message>>();

		public Object getChild(int groupPosition, int childPosition) {
			return children.get(groupPosition).get(childPosition);
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public int getChildrenCount(int groupPosition) {
			return children.get(groupPosition).size();
		}

		public TextView getGenericView() {
			// Layout parameters for the ExpandableListView
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, 64);

			TextView textView = new TextView(KHLNieuwsActivity.this);
			textView.setLayoutParams(lp);
			// Center the text vertically
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// Set the text starting position
			textView.setPadding(75, 0, 0, 0);
			textView.setBackgroundResource(R.color.KHLRED);
			return textView;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView textView = getGenericView();
			textView.setText(((Message) getChild(groupPosition, childPosition))
					.getTitle());
			return textView;
		}

		public Object getGroup(int groupPosition) {
			String x = groups.get(groupPosition);
			if (x.equals("G&amp;T")) {
				x = "G&T";
			}
			return x;
		}

		public int getGroupCount() {
			return groups.size();
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = getGenericView();
			textView.setText(getGroup(groupPosition).toString());
			return textView;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		public boolean hasStableIds() {
			return true;
		}

		/**
		 * Verwijdert alle categorie‘n uit de lijst en vult ze dan opnieuw in op basis van de berichten die er zijn.
		 */
		private void getAllCategories() {
			groups.clear();
			groups.add("5 newest");
			for (Message m : messages) {
				if (!groups.contains(m.getCategory())) {
					groups.add(m.getCategory());
				}
			}

		}

		/**
		 * Sorteert de berichten per categorie en vult de bijbehorende lijst in.
		 * 
		 */
		private void sortByCategory() {
			children.clear();
			List<Message> temp = new ArrayList<Message>();
			Collections.sort(messages);
			for (int i = 0; i < 5; i++) {
				temp.add(messages.get(i));
			}
			children.add(temp);

			for (String c : groups) {
				temp = new ArrayList<Message>();
				for (Message m : messages) {
					if (c.equals(m.getCategory())) {
						temp.add(m);
					}
				}
				if (temp.size() > 0) {
					children.add(temp);
				}
			}
		}

		/**
		 * Laadt de rss-feed in, roept de parser aan en  vult de berichtenlijst in.
		 * @param feedUrl De url van de feed.
		 */
		public void loadFeed(String feedUrl) {
			if (feedUrl == null || feedUrl.equals("")) {
				feedUrl = "https://portaal.khleuven.be/rss.php";
			}
			DomFeedParser parser = new DomFeedParser(feedUrl);
			long start = System.currentTimeMillis();
			messages = parser.parse();
			long duration = System.currentTimeMillis() - start;
			Log.i("AndroidNews", "Parser duration=" + duration);
		}

		/**
		 * Laadt de rss-feed in, zoekt de categori‘n en sorteert per categorie.
		 * @param feedUrl De url van de feed.
		 */
		public void populate(String feedUrl) {
			loadFeed(feedUrl);
			getAllCategories();
			sortByCategory();
		}
	}
}