package org.aggelos.baztag.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * The main class for the API. A Nabaztag thing is identified by its serial number and token.
 * 
 * Pretty straightforward : <code>
 * {@link Nabaztag} myTag = new {@link Nabaztag}(serial, token);
 * {@link NabaztagInstructionSequence} instructions = new {@link NabaztagInstructionSequence}();
 * ...
 * boolean result = myTag.execute(instructions);
 * if(!result) {
 * return myTag.getLastErrorMessage();
 * }
 * @author sinmaniphel
 * 
 */
public class Nabaztag {

	public static final String NABAZTAG_API_URL = "http://api.nabaztag.com/vl/FR/api.jsp";
	public static final String baseFormat = "%1$s?sn=%2$s&token=%3$s";
	
	private String serialNumber;
	
	private String token;
	
	private String lastErrorMessage;
		
	
	/**
	 * The only way to create a Nabaztag instance is to provide the two key informations needed to interact with a given nabaztag : 
	 * @param serialNumber the serial number of the Nabaztag (under the rabbit)
	 * @param token (found on your account on my.nabaztag.com)
	 */
	public Nabaztag(String serialNumber, String token) {
		super();
		this.serialNumber = serialNumber;
		this.token = token;
	}
	
	/**
	 * This method will execute a sequence of instructions, for example saying something and moving the ears
	 * @param sequence your built {@link NabaztagInstructionSequence}
	 */
	public boolean execute(NabaztagInstructionSequence sequence) {
		String baseUrl = String.format(this.baseFormat, NABAZTAG_API_URL,serialNumber,token);
		
		String fullUrl = baseUrl+sequence.toParamUrl();
		System.out.println(fullUrl);
		
		try {
			URL url = new URL(fullUrl);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			// TODO : handle returned message
			// TODO : remove this at the proper time and handle the returned message
			while ((line = reader.readLine()) != null) {
			    System.out.println(line);
			}
			reader.close();
			return true;
		} catch (MalformedURLException e) {
			lastErrorMessage = e.getMessage();
			return false;
		} catch (IOException e) {
			lastErrorMessage = e.getMessage();
			return false;
		}

	}
	
	/**
	 * In case the execution ended poorly, will provide the last error message
	 * @return the last error message
	 */
	public String getLastErrorMessage() {
		return lastErrorMessage;
	}

	
}
