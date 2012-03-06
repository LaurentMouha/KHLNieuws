package be.khleuven.nieuws;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @author Laurent Mouha, Robin Vrebos en Bram Miermans
 * 
 */
public class KHLLoginActivity extends Activity {
	/**
	 * De webview wat gebruikt wordt.
	 */
	WebView myWebView;
	/**
	 * De url die bijgehouden wordt in de preferences wordt hier in gezet.
	 */
	String mFeedUrl = null;
	public static final String PREFS_NAME = "MyPrefsFile";
	/**
	 * Hierin wordt de url opgeslagen.
	 */
	static SharedPreferences settings;
	/**
	 * Editor voor aanpassen van data in SharedPreferences.
	 */
	SharedPreferences.Editor editor;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();

		mFeedUrl = settings.getString("feedUrl", null);

		if (mFeedUrl != null) {
			openRSS(mFeedUrl);
		} else {

			/**
			 * Deze klasse wordt gebruikt als interface tussen de java en
			 * javascriptcode die gebruikt wordt bij de webview
			 * 
			 * @author Laurent Mouha, Robin Vrebos en Bram Miermans
			 * 
			 */
			class MyJavaScriptInterface {

				/**
				 * Verwerkt de html code en opent dan de KHLNieuwsActivity.
				 * Wordt aangeroepen vanuit de javascriptcode.
				 * 
				 * @param html
				 *            De string die de htmlcode van de pagina bevat.
				 */
				@SuppressWarnings("unused")
				public void processHTML(String html) {
					String link = fetchLink(html);
					editor.putString("feedUrl", link);
					editor.commit();
					openRSS(link);
				}

				/**
				 * Deze functie doorzoekt de htmlcode naar een geldige RSS-link.
				 * 
				 * @param html
				 *            De string die de htmlcode van de pagina bevat.
				 * @return De juiste url van de RSS-feed.
				 */
				private String fetchLink(String html) {
					String[] temp;
					temp = html.split("\n");
					String link = "";
					boolean stop = false;
					try {
						for (String t : temp) {
							if (t.contains("<a href=\"http://portaal.khleuven.be/rss.php")) {
								link = t;
								stop = true;
							}
							if (stop)
								break;
						}

					} catch (Exception e) {

					} finally {
						if (link != null) {

							String[] parts = link.split("\"");
							link = parts[1];

						}
					}
					return link;
				}
			}

			// cookie bijhouden
			CookieSyncManager.createInstance(this);
			CookieSyncManager.getInstance().startSync();

			// webview gebruiken voor login
			myWebView = (WebView) findViewById(R.id.webview);
			myWebView.getSettings().setJavaScriptEnabled(true);
			myWebView.setWebViewClient(new WebViewClient() {
				public void onReceivedSslError(WebView view,
						SslErrorHandler handler, SslError error) {
					handler.proceed(); // Ignore SSL certificate errors
				}

				// wat doen wanneer pagina geladen is
				// checken of ingelogd door te kijken of we terug op de
				// portaalsite zitten
				public void onPageFinished(WebView view, String url) {
					if (view.getUrl().equals("https://portaal.khleuven.be/")) {
						view.loadUrl("javascript:window.interfaceName.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
					}
				}

				public void onPageStarted(WebView view, String url,
						Bitmap favicon) {
					if (url.equals("https://portaal.khleuven.be/")) {
						view.setVisibility(View.INVISIBLE);
						ProgressDialog.show(
								KHLLoginActivity.this, "",
								"Fetching RSS. Please wait...", true);
					}
				}
			});

			myWebView.getSettings().setDatabasePath("khl.news");
			myWebView.getSettings().setDomStorageEnabled(true);

			myWebView.addJavascriptInterface(new MyJavaScriptInterface(),
					"interfaceName");

			myWebView
					.loadUrl("https://portaal.khleuven.be/Shibboleth.sso/WAYF/khleuven?target=https%3A%2F%2Fportaal.khleuven.be%2F");

		}

	}

	private void openRSS(String link) {
		Intent intent = new Intent(getApplicationContext(),
				KHLNieuwsActivity.class);
		intent.putExtra("feedUrl", link);
		startActivity(intent);
	}

}