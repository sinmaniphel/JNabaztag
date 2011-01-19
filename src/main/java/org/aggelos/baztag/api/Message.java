package org.aggelos.baztag.api;

/**
 * Represents a message sent to this Nabaztag
 * @author Sinmaniphel
 */
public class Message {

	private String from;
	private String title;
	private String date;
	private String url;
	
	/**
	 * @return the name of the friend who sent the message
	 */
	public String getFrom() {
		return from;
	}
	
	/**
	 * 
	 * @return the title of the message
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * @return the expedition date, as a String
	 */
	public String getDate() {
		return date;
	}
	
	/**
	 * @return the URL of the audio message
	 */
	public String getUrl() {
		return url;
	}

	public Message(String from, String title, String date, String url) {
		super();
		this.from = from;
		this.title = title;
		this.date = date;
		this.url = url;
	}

	@Override
	public String toString() {		
		return "Message[from="+from+";title="+title+";date="+date+";url="+date+"]";
	}
	
	
	
	
}
