package be.khleuven.nieuws;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.TextView;
import android.widget.Toast;

public class KHLNieuwsActivity extends ExpandableListActivity {

	ExpandableListAdapter mAdapter;
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

	/*@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Sample menaaau");
		menu.add(0, 0, 0, R.string.app_name);
	}*/
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		
		Message m = (Message) mAdapter.getChild(groupPosition, childPosition);
		Toast.makeText(KHLNieuwsActivity.this,
				Html.fromHtml(m.toString()),
				Toast.LENGTH_LONG).show();
		System.out.println("Stop clicking meeee");
		
		/* start nieuwe intent voor detailspagina
		 */
		
		Intent intent = new Intent(getApplicationContext(),
				KHLDetailsActivity.class);
		intent.putExtra("title", m.getTitle());
		intent.putExtra("date", m.getDate());
		intent.putExtra("cat", m.getCategory());
		intent.putExtra("auth", m.getAuthor());
		intent.putExtra("descr", m.getDescription());
		//intent.putExtra("link", m.getLink().toString());
		startActivity(intent);
		
		return true;
	}


	@Override
	public boolean onContextItemSelected(MenuItem item) {
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item
				.getMenuInfo();

		String title = ((TextView) info.targetView).getText().toString();

		int type = ExpandableListView
				.getPackedPositionType(info.packedPosition);
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			int groupPos = ExpandableListView
					.getPackedPositionGroup(info.packedPosition);
			int childPos = ExpandableListView
					.getPackedPositionChild(info.packedPosition);
			/*Toast.makeText(
					this,
					title + ": Child " + childPos + " clicked in group "
							+ groupPos, Toast.LENGTH_SHORT).show();*/
			Message m = (Message) mAdapter.getChild(groupPos, childPos);
			Toast.makeText(this, m.getDescription(), Toast.LENGTH_LONG).show();
			return true;
		} else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
			int groupPos = ExpandableListView
					.getPackedPositionGroup(info.packedPosition);
			Toast.makeText(this, title + ": Group " + groupPos + " clicked",
					Toast.LENGTH_SHORT).show();
			return true;
		}

		return false;
	}

	public class MyExpandableListAdapter extends BaseExpandableListAdapter {

		private List<Message> messages;
		private List<String> groups = new ArrayList<String>();
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
		
		private void getAllCategories() {
			groups.clear();
			groups.add("5 newest");
			for (Message m : messages) {
				if (!groups.contains(m.getCategory())) {
					groups.add(m.getCategory());
				}
			}

		}

		private void sortByCategory() {
			children.clear();
			List<Message> temp = new ArrayList<Message>();
			Collections.sort(messages);
			for (int i = 0; i < 5; i++) {
				temp.add(messages.get(i));
			}
			children.add(temp);

			// int pos = 0;
			for (String c : groups) {
				temp = new ArrayList<Message>();
				System.out.println("==" + c + "==");
				for (Message m : messages) {
					if (c.equals(m.getCategory())) {
						temp.add(m);
						System.out.println(m.getTitle());
					}
				}
				if (temp.size()>0) {
					children.add(temp);
				}
				// children.set(pos, temp);
				// pos++;
			}
		}

		public void loadFeed(String feedUrl) {
			if(feedUrl==null || feedUrl.equals("")){
				feedUrl = "https://portaal.khleuven.be/rss.php";
				//feedUrl = "https://portaal.khleuven.be/rss.php?user=511726&key=293e531a4545407844de7e4be73616a1c1278697";
			}
			DomFeedParser parser = new DomFeedParser(feedUrl);
			long start = System.currentTimeMillis();
			messages = parser.parse();
			long duration = System.currentTimeMillis() - start;
			Log.i("AndroidNews", "Parser duration=" + duration);
			for (Message m : messages) {
				System.out.println(m.toString());
			}
		}

		public void populate(String feedUrl) {
			loadFeed(feedUrl);
			getAllCategories();
			sortByCategory();
		}
	}
}