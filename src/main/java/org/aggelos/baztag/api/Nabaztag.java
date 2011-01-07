package org.aggelos.baztag.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;

import org.aggelos.baztag.api.inst.WakeUpInstruction;
import org.aggelos.baztag.api.xml.AnswerParsingException;
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
	
	private static final Logger LOGGER = Logger.getLogger(Nabaztag.class.getName());
	
	private String serialNumber;
	
	private String token;
	
	private ServiceAnswerParser answerParser;
	
	private List<ApiAnswer> lastErrors;
	
	/*
	 * The following fields are "status" fields
	 */
	
	private boolean awake;
	private String name;
	private String signature;
	
	private int leftEarPos;
	private int rightEarPos;
	
	private List<String> friends;
	private List<Message> messages;
	
	private NabaztagVersion version;
	
	
		
	
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
	
	/**
	 * a general way to update the whole status of the nabaztag
	 */
	public boolean updateStatus() {
		try {
			String baseUrl = String.format(this.baseFormat, NABAZTAG_API_URL,serialNumber,token);
			
			LOGGER.fine("getting friends");
			// the action 2 provides the list of friends
			URL url = new URL(baseUrl+"&action=2");
			friends = answerParser.parseFriends(url.openStream());
			
			LOGGER.fine("getting messages");
			// the action 3 provides the list of messages
			url = new URL(baseUrl+"&action=3");
			messages = answerParser.parseMessages(url.openStream());
			
			LOGGER.fine("getting signature");
			// the action 5 provides the signature
			url = new URL(baseUrl+"&action=5");
			signature = answerParser.parseSignature(url.openStream());
			
			LOGGER.fine("getting awake state");
			// the action 7 provides the awake state
			url = new URL(baseUrl+"&action=7");
			awake = answerParser.parseAwake(url.openStream());
			
			LOGGER.fine("getting version");
			// the action 8 provides the version
			url = new URL(baseUrl+"&action=8");
			version = answerParser.parseVersion(url.openStream());
			
			LOGGER.fine("getting name");
			// the action 10 provides the name
			url = new URL(baseUrl+"&action=10");
			name = answerParser.parseName(url.openStream());
			
			LOGGER.fine("getting ears status");
			updateEarsStatus();
			
			return true;
			
		} catch (MalformedURLException e) {
			lastErrors = new LinkedList<ApiAnswer>();
			lastErrors.add(new ApiAnswer(null, e.getLocalizedMessage()));
			return false;
		} catch (AnswerParsingException e) {
			lastErrors = new LinkedList<ApiAnswer>();
			lastErrors.add(new ApiAnswer(null, e.getLocalizedMessage()));
			return false;
		} catch (IOException e) {
			lastErrors = new LinkedList<ApiAnswer>();
			lastErrors.add(new ApiAnswer(null, e.getLocalizedMessage()));
			return false;
		}
	}
	
	
	/**
	 * A way to only update the ears status
	 */
	public boolean updateEarsStatus() {
		String baseUrl = String.format(this.baseFormat, NABAZTAG_API_URL,serialNumber,token);
		
		try {
			URL actionURL = new URL(baseUrl+"&ears=ok");
			int[] earpos = answerParser.parseEarPosition(actionURL.openStream());
			leftEarPos =  earpos[0];
			rightEarPos = earpos[1];
			return true;
		} catch (AnswerParsingException e) {
			lastErrors = new LinkedList<ApiAnswer>();
			lastErrors.add(new ApiAnswer(null, e.getLocalizedMessage()));
			return false;
		} catch (IOException e) {
			lastErrors = new LinkedList<ApiAnswer>();
			lastErrors.add(new ApiAnswer(null, e.getLocalizedMessage()));
			return false;
		}
	}

	/**
	 * @return the awake
	 */
	public boolean isAwake() {
		return awake;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the signature
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * @return the leftEarPos
	 */
	public int getLeftEarPos() {
		return leftEarPos;
	}

	/**
	 * @return the rightEarPos
	 */
	public int getRightEarPos() {
		return rightEarPos;
	}

	/**
	 * @return the friends
	 */
	public List<String> getFriends() {
		return friends;
	}

	/**
	 * @return the messages
	 */
	public List<Message> getMessages() {
		return messages;
	}

	/**
	 * @return the version
	 */
	public NabaztagVersion getVersion() {
		return version;
	}
	
	/**
	 * Will awaken or shut down the nabaztag
	 * @param awake
	 * @return true if the command executed successfully
	 */
	public boolean setAwake(boolean awake) {
		NabaztagInstructionSequence seq = new NabaztagInstructionSequence();
		seq.add(new WakeUpInstruction(awake));	
		this.execute(seq);
		this.awake = awake;
		return true;
	}

	
}
