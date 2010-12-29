package org.aggelos.baztag.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.aggelos.baztag.api.xml.ApiAnswer;
import org.aggelos.baztag.api.xml.ServiceAnswerParser;

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
	
	private ServiceAnswerParser answerParser;
	
	private List<ApiAnswer> lastErrors;
		
	
	/**
	 * The only way to create a Nabaztag instance is to provide the two key informations needed to interact with a given nabaztag : 
	 * @param serialNumber the serial number of the Nabaztag (under the rabbit)
	 * @param token (found on your account on my.nabaztag.com)
	 */
	public Nabaztag(String serialNumber, String token) {
		super();
		this.serialNumber = serialNumber;
		this.token = token;
		answerParser = new ServiceAnswerParser();
		
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
			
			List<ApiAnswer> result = answerParser.parse(url.openStream());
			if(result.size() == 0) {
				return true;
			}
			else {
				lastErrors = result;
				return false;
			}
		
		} catch (MalformedURLException e) {
			lastErrors = new LinkedList<ApiAnswer>();
			lastErrors.add(new ApiAnswer(null, e.getLocalizedMessage()));
			return false;
		} catch (IOException e) {
			lastErrors = new LinkedList<ApiAnswer>();
			lastErrors.add(new ApiAnswer(null, e.getLocalizedMessage()));
			return false;
		} catch (XMLStreamException e) {
			lastErrors = new LinkedList<ApiAnswer>();
			lastErrors.add(new ApiAnswer(null, e.getLocalizedMessage()));
			return false;
		}

	}


	/**
	 * In case the execution ended poorly, will provide the last error messages
	 * @return the last error message
	 */
	public List<ApiAnswer> getLastErrors() {
		return lastErrors;
	}

	
}
