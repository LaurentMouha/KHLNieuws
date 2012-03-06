package be.khleuven.nieuws;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * @author Laurent Mouha, Robin Vrebos en Bram Miermans
 * 
 */
public class KHLIntro extends Activity {
	/**
	 * De url die bijgehouden wordt in de preferences wordt hier in gezet.
	 */
	String mFeedUrl = null;
	public static final String PREFS_NAME = "MyPrefsFile";
	/**
	 * Hierin wordt de url opgeslagen.
	 */
	static SharedPreferences settings;
	Button button, button2;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro);

		settings = getSharedPreferences(PREFS_NAME, 0);
		mFeedUrl = settings.getString("feedUrl", null);

		button = new Button(this);
		button2 = new Button(this);
		button = (Button) findViewById(R.id.button3);
		button2 = (Button) findViewById(R.id.button2);
		if (mFeedUrl == null) {
			button.setEnabled(false);
			button2.setEnabled(false);
		} else {
			button.setEnabled(true);
			button2.setEnabled(true);
		}

		// controleer();
		// geef uitleg
		// openLoginActivity();
	}

	/**
	 * Controleert of er een url in de SharedPreferences zitten.
	 * 
	 * @param v
	 *            De view waar de button in zat die deze functie opriep. Is
	 *            nodig voor onClick van button.
	 */
	public void controleer(View v) {
		mFeedUrl = settings.getString("feedUrl", null);
		if (!isOnline()) {
			Toast.makeText(getApplicationContext(),
					"Je moet verbinden met het Internet", Toast.LENGTH_LONG)
					.show();
		} else if (mFeedUrl != null) {
			Intent intent = new Intent(getApplicationContext(),
					KHLNieuwsActivity.class);
			intent.putExtra("feedUrl", mFeedUrl);
			startActivity(intent);
		}
	}

	/**
	 * Start activity voor in te loggen.
	 */
	private void openLoginActivity() {
		Intent intent = new Intent(getApplicationContext(),
				KHLLoginActivity.class);
		startActivity(intent);
	}

	/**
	 * Handler voor onClick voor openLoginActivity()
	 * 
	 * @param v
	 *            De view waar de button in zat die deze functie opriep. Is
	 *            nodig voor onClick van button.
	 */
	public void leClick(View v) {
		if (!isOnline()) {
			Toast.makeText(getApplicationContext(),
					"Je moet verbinden met het Internet", Toast.LENGTH_LONG)
					.show();
		} else {
			openLoginActivity();
		}
	}

	/**
	 * Deze verwijdert de url uit de SharedPreferences. Zet ook enkele knoppen
	 * op disabled.
	 * 
	 * @param v
	 *            De view waar de button in zat die deze functie opriep. Is
	 *            nodig voor onClick van button.
	 */
	public void killAllTheThings(View v) {
		Editor edit = settings.edit();
		edit.clear();
		edit.commit();
		button = new Button(this);
		button2 = new Button(this);
		button = (Button) findViewById(R.id.button3);
		button2 = (Button) findViewById(R.id.button2);
		button.setEnabled(false);
		button2.setEnabled(false);
	}

	public static boolean isOnline() {
		try {
			InetAddress.getByName("google.ca").isReachable(3);
			return true;
		} catch (UnknownHostException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

}
