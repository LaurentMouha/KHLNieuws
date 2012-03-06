package be.khleuven.nieuws;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Laurent Mouha, Robin Vrebos, Bram Miermans
 * 
 */
public class DomFeedParser extends BaseFeedParser {

	protected DomFeedParser(String feedUrl) {
		super(feedUrl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.khleuven.nieuws.FeedParser#parse()
	 */
	public List<Message> parse() {
		List<Message> messages = new ArrayList<Message>();
		try {

			URL url = new URL("https://portaal.khleuven.be/rss.php");

			URLConnection connection = url.openConnection();
			HttpURLConnection.setFollowRedirects(true);
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			HttpURLConnection.setFollowRedirects(true);
			httpConnection.setInstanceFollowRedirects(true);
			Document dom = null;

			InputStream in = httpConnection.getInputStream();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			dom = db.parse(in);

			Element root = dom.getDocumentElement();
			if (root == null) {
				root = dom.getDocumentElement();
			}
			NodeList items = root.getElementsByTagName(ITEM);
			//elk item afgaan
			for (int i = 0; i < items.getLength(); i++) {
				Message message = new Message();
				Node item = items.item(i);
				NodeList properties = item.getChildNodes();
				// elk kind van het betreffende item afgaan
				for (int j = 0; j < properties.getLength(); j++) {
					Node property = properties.item(j);
					String name = property.getNodeName();

					if (name.equalsIgnoreCase(TITLE)) {
						// titel inlezen. 
						String title = "";
						NodeList titlelist = property.getChildNodes();
						for (int k = 0; k < titlelist.getLength(); k++) {
							title += titlelist.item(k).getNodeValue();
						}
						message.setTitle(title);
					} else if (name.equalsIgnoreCase(LINK)) {
						// link inlezen
						message.setLink(property.getFirstChild().getNodeValue());
					} else if (name.equalsIgnoreCase(DESCRIPTION)) {
						// omschrijving inlezen.
						StringBuilder text = new StringBuilder();
						NodeList chars = property.getChildNodes();
						for (int k = 0; k < chars.getLength(); k++) {
							text.append(chars.item(k).getNodeValue());
						}
						message.setDescription(text.toString());
					} else if (name.equalsIgnoreCase(PUB_DATE)) {
						// datum inlezen
						message.setDate(property.getFirstChild().getNodeValue());
						
					} else if (name.equalsIgnoreCase(CATEGORY)) {
						// categorie inlezen
						String title = "";
						NodeList titlelist = property.getChildNodes();
						for (int k = 0; k < titlelist.getLength(); k++) {
							title += titlelist.item(k).getNodeValue();
						}
						message.setCategory(title);
	
					} else if (name.equalsIgnoreCase(AUTHOR)) {
						// auteur inlezen
						message.setAuthor(property.getFirstChild()
								.getNodeValue());

					}
				}
				messages.add(message);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return messages;
	}

}
