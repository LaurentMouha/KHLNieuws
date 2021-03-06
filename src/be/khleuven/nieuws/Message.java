package be.khleuven.nieuws;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message implements Comparable<Message> {
	/**
	 * Wordt gebruikt om de datum juist te kunnen inlezen. De datum staat in een
	 * engelstalige notatie en heeft bijgevolg een engelstalige locale nodig.
	 */
	static Locale locale = Locale.ENGLISH;
	/**
	 * Formatter die gebruikt wordt om de ingelezen datum-string om te zetten
	 * naar een Date.
	 */
	static SimpleDateFormat FORMATTER = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss Z", locale);
	/**
	 * Formatter die gebruikt wordt om de Date om te zetten naar een leesbaar
	 * formaat ("dd/MM/yyyy HH:mm").
	 */
	static SimpleDateFormat FORMATTER2 = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm");

	private String title;
	private URL link;
	private String description;
	private Date date;
	private String category;
	private String author;

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public URL getLink() {
		return link;
	}

	public void setLink(String link) {
		try {
			this.link = new URL(link);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description.trim();
	}

	public String getDate() {
		return FORMATTER2.format(this.date);
	}

	public void setDate(String date) {
		// toevoegen van nullen indien nodig.
		while (!date.endsWith("00")) {
			date += "0";
		}
		try {
			this.date = FORMATTER.parse(date.trim());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public Message copy() {
		Message copy = new Message();
		copy.title = title;
		copy.link = link;
		copy.description = description;
		copy.date = date;
		return copy;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Title: ");
		sb.append(title);
		sb.append('\n');
		sb.append("Date: ");
		sb.append(this.getDate());
		sb.append('\n');
		sb.append("Link: ");
		sb.append(link);
		sb.append('\n');
		sb.append("Description: ");
		sb.append(description);
		sb.append('\n');
		sb.append("Author: ");
		sb.append(author);
		sb.append('\n');
		sb.append("Category: ");
		sb.append(category);
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	public int compareTo(Message another) {
		if (another == null)
			return 1;
		// sort descending, most recent first
		return another.date.compareTo(date);
	}
}
