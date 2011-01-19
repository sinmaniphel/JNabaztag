package org.aggelos.baztag.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;

import org.aggelos.baztag.api.inst.RetrieveInfoInstruction;
import org.aggelos.baztag.api.inst.UpdateEarsInstruction;
import org.aggelos.baztag.api.inst.streaming.StreamInstructionSequence;
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
	public static final String NABAZTAG_STREAM_API_URL = "http://api.nabaztag.com/vl/FR/api_stream.jsp";
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
	
	private NabaztagInstructionSequence sequence = new NabaztagInstructionSequence();
	private StreamInstructionSequence playlist = new StreamInstructionSequence();
	
	
		
	
	/**
	 * The only way to create a Nabaztag instance is to provide the two key informations needed to interact with a given nabaztag : 
	 * @param serialNumber the serial number of the Nabaztag (under the rabbit)
	 * @param token (found on your account on my.nabaztag.com)
	 */
	public Nabaztag(String serialNumber, String token) {
		super();
		this.serialNumber = serialNumber;
		this.token = token;
		answerParser = new ServiceAnswerParser(this);
		friends = new ArrayList<String>();
		messages = new ArrayList<Message>();
		
	}
	
	/**
	 * This method call the Stream API execute a sequence of instructions 
	 * @todo
	 * 		implements a stop() method (when i will find a convenient method to stop to read a stream)
	 */
	public boolean play() {
		String baseUrl = String.format(baseFormat, NABAZTAG_STREAM_API_URL,serialNumber,token);
		
		String fullUrl = baseUrl+playlist.toParamUrl();
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
		}finally{
			playlist.clear();
		}

	}
	
	/**
	 * This method will execute a sequence of instructions, for example saying something and moving the ears 
	 */
	public boolean execute() {
		String baseUrl = String.format(baseFormat, NABAZTAG_API_URL,serialNumber,token);
		
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
		}finally{
			sequence.clear();
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
			
		LOGGER.fine("getting friends");
		// the action 2 provides the list of friends
		addInstruction(RetrieveInfoInstruction.FRIENDS_LIST);
		execute();
		
		LOGGER.fine("getting messages");
		// the action 3 provides the list of messages
		addInstruction(RetrieveInfoInstruction.INBOX);
		execute();
		
		LOGGER.fine("getting signature");		
		addInstruction(RetrieveInfoInstruction.SIGNATURE);		
		execute();
		
		LOGGER.fine("getting awake state");
		addInstruction(RetrieveInfoInstruction.SLEEPING_STATUS);
		execute();
		
		LOGGER.fine("getting version");
		addInstruction(RetrieveInfoInstruction.VERSION);
		execute();			
		
		LOGGER.fine("getting name");
		addInstruction(RetrieveInfoInstruction.NAME);
		execute();
		
		LOGGER.fine("getting ears status");
		addInstruction(new UpdateEarsInstruction());
		execute();
		
		return true;		
	}
	


	/**
	 * @return the awake status
	 */
	public boolean isAwake() {
		return awake;
	}
	
	/**
	 * Set the "awake" status of the Nabaztag
	 * @param the awake
	 */
	public void setAwake(boolean awake) {
		this.awake = awake;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * set the name
	 */
	public void setName(String name) {
		this.name = name;
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
	 * Set the version
	 * @param
	 */
	public void setVersion(NabaztagVersion version) {
		this.version = version;
	}

	public void setLeftEarPos(int leftEarPos) {
		this.leftEarPos = leftEarPos;
	}

	public void setRightEarPos(int rightEarPos) {
		this.rightEarPos = rightEarPos;
	}	
	
	public NabaztagInstructionSequence getSequence() {
		return sequence;
	}

	/**
	 * method that allow convenient syntax like: <br/>
	 * <pre> 
	 * nab
	 * 		.addInstruction(new LeftEarInstruction(10))
	 *		.addInstruction(new RightEarInstruction(10))
	 *		.addInstruction(new TextInstruction("say hoho"))
	 *		.execute();
	 * </pre>
	 * @param inst
	 * @return
	 */
	public Nabaztag addInstruction(NabaztagInstruction inst){
		sequence.add(inst);
		return this;
	}
	
	/**
	 * method that allow convenient syntax like: <br/>
	 * <pre> 
	 * nab
	 * 		.addStream(new OnlineStreamInstruction(new URL("http://my.server.org/music.mp3"))
	 *		.addStream(new OnlineStreamInstruction("http://my.server.org/music.mp2"))
	 *		.play();
	 * </pre>
	 * @param inst
	 * @return
	 */
	public Nabaztag addStream(StreamInstruction inst){
		playlist.add(inst);
		return this;
	}
	


	
}
