package org.aggelos.baztag.api.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.aggelos.baztag.api.Message;
import org.aggelos.baztag.api.Nabaztag;
import org.aggelos.baztag.api.NabaztagVersion;

/**
 * The purpose of this class is to handle the answer of the API which will usually be 
 * @author Daniel
 *
 */

public class ServiceAnswerParser {
	
	private XMLInputFactory inputFactory;
	private static final String START_TAG = "rsp";
	private static final String MESSAGE_TYPE_TAG = "message";
	private static final String FRIEND_TYPE_TAG = "friend";
	private static final String RABBIT_SLEEP_TYPE_TAG = "rabbitSleep";
	private static final String RABBIT_VERSION_TYPE_TAG = "rabbitVersion";
	private static final String RABBIT_NAME_TYPE_TAG = "rabbitName";
	private static final String LEFT_POSITION_TYPE_TAG = "leftposition";
	private static final String RIGHT_POSITION_TYPE_TAG = "rightposition";
	private static final String SIGNATURE_TAG = "signature";
	private static final String EMBED_SIGNATURE_TAG = "embed";
	private static final String MESSAGE_TAG = "msg";
	private static final String LISTFRIEND_TYPE_TAG = "listfriend";
	private static final String MESSAGE_COMMENT_TAG = "comment";
	
	private static final ResourceBundle errorsBundle = ResourceBundle.getBundle("apierrors");
	
	private Nabaztag model = null;
	
	public ServiceAnswerParser(Nabaztag model) {
		inputFactory = XMLInputFactory.newInstance();
		this.model = model;
	}

	
	/**
	 * Will parse the stream returned by the API to find errors
	 * @param answer 
	 * @return a list of errors
	 * @throws XMLStreamException
	 */
	public LinkedList<ApiAnswer> parse(InputStream answer) throws AnswerParsingException  {
		/*
		 * Going for a StAX implementation, far easier, serves many purposes, while being fast and efficient
		 * May pose compatibility issues
		 */
		LinkedList<ApiAnswer> errorMessages = new LinkedList<ApiAnswer>();
		XMLStreamReader reader = null;
		try{
			reader = inputFactory.createXMLStreamReader(answer);
			
			while(reader.hasNext()) {
				if(START_ELEMENT == reader.getEventType()) {
					// the answer XML has no namespace so QName == localName
					if(START_TAG.equals(reader.getLocalName())) {
						handleRsp(reader, errorMessages);
					}
				}
				reader.next();
			}
		}catch(XMLStreamException xe){
			throw new AnswerParsingException(xe);
		}finally{
			if(reader!=null)
				try {
					reader.close();
				} catch (XMLStreamException e) {
					throw new AnswerParsingException(e);
				}
		}
		return errorMessages;
		
		
	}

	private void handleRsp(XMLStreamReader reader, LinkedList<ApiAnswer> errorMessages) throws XMLStreamException {
		while(reader.hasNext()) {
			if(START_ELEMENT == reader.getEventType()) {
				// the answer XML has no namespace so QName == localName
				if(MESSAGE_TYPE_TAG.equals(reader.getLocalName())) {
					handleMessage(reader, errorMessages);
				}
				if(FRIEND_TYPE_TAG.equals(reader.getLocalName())) {
					model.getFriends().add(reader.getAttributeValue(null, "name"));
				}
				if(RABBIT_SLEEP_TYPE_TAG.equals(reader.getLocalName())){
					String strIsSleeping = reader.getElementText();
					model.setAwake(
							!"YES".equals(strIsSleeping)
						);
				}
				if(RABBIT_VERSION_TYPE_TAG.equals(reader.getLocalName())){
					String strVersion = reader.getElementText();					
					model.setVersion(NabaztagVersion.valueOf(strVersion));					
				}
				if(RABBIT_NAME_TYPE_TAG.equals(reader.getLocalName())){
					String strName = reader.getElementText();					
					model.setName(strName);					
				}
				if(LEFT_POSITION_TYPE_TAG.equals(reader.getLocalName())) {
					String sig = reader.getElementText();
					model.setLeftEarPos(Integer.parseInt(sig));
					
				}
				if(RIGHT_POSITION_TYPE_TAG.equals(reader.getLocalName())) {
					String sig = reader.getElementText();
					model.setRightEarPos(Integer.parseInt(sig));
					
				}
				if(SIGNATURE_TAG.equals(reader.getLocalName())) {
					String sig = handleSignature(reader, errorMessages);
					model.setSignature(sig);
					return;
				}
				if(MESSAGE_TAG.equals(reader.getLocalName())) {					
					model.getMessages().addAll(
							handleInbox(reader, errorMessages)
						);
					return;
				}
				
			}
			if(END_ELEMENT == reader.getEventType()) {
				if(START_TAG.equals(reader.getLocalName())) {
					return;
				}
			}
 			reader.next();
		}
	}


	private void handleMessage(XMLStreamReader reader,
			LinkedList<ApiAnswer> errorMessages) {
		String type="";
		try {
			type = reader.getElementText();
		} catch (XMLStreamException e) {
			errorMessages.add(new ApiAnswer(null, e.getLocalizedMessage()));
			return;
		}
		ApiAnswers ansType = ApiAnswers.valueOf(type);
		switch(ansType) {
		case ABUSESENDING:
			errorMessages.add(generateAnswer(ansType));
			break;
		case CHORNOTSENT:
			errorMessages.add(generateAnswer(ansType));
			break;
		case CHORSENT:
			break;
		case EARPOSITIONNOTSENT:
			errorMessages.add(generateAnswer(ansType));
			break;
		case EARPOSITIONSENT:
			break;
		case MESSAGENOTSENT:
			errorMessages.add(generateAnswer(ansType));
			break;
		case MESSAGESENT:
			break;
		case NABCASTNOTSENT:
			errorMessages.add(generateAnswer(ansType));
			break;
		case NABCASTSENT:
			break;
		case NOCORRECTPARAMETERS:
			errorMessages.add(generateAnswer(ansType));
			break;
		case NOGOODTOKENORSERIAL:
			errorMessages.add(generateAnswer(ansType));
			break;
		case NOGOODSERIAL:
			errorMessages.add(generateAnswer(ansType));
			break;
		case NOTV2RABBIT:
			errorMessages.add(generateAnswer(ansType));
			break;
		case TTSNOTSENT:
			errorMessages.add(generateAnswer(ansType));
			break;
		case TTSSENT:
			break;
		case WEBRADIONOTSENT:
			errorMessages.add(generateAnswer(ansType));
			break;
		case WEBRADIOSENT:
			break;
		default:
		
		}
	}
	
	private ApiAnswer generateAnswer(ApiAnswers ans) {
		return new ApiAnswer(ans,errorsBundle.getString(ans.toString()));
	}


	/**
	 * @param is which should normaly be the api URL with action 3 
	 * @return the list of messages
	 * @throws AnswerParsingException 
	 */
	private List<Message> handleInbox(XMLStreamReader reader, LinkedList<ApiAnswer> errorMessages) throws XMLStreamException {
		LinkedList<Message> messages = new LinkedList<Message>();
		while(reader.hasNext()) {
			if(START_ELEMENT == reader.getEventType()) {
				String from = reader.getAttributeValue(null, "from");
				String title = reader.getAttributeValue(null, "title");
				String date = reader.getAttributeValue(null, "date");
				String url = reader.getAttributeValue(null, "url");
				messages.add(new Message(from, title, date, url));
			}
			reader.next();
		}
		return messages;
	}


	/**
	 * @param reader
	 * 		xml stream reader. Should be positionning on the "signature" start tag
	 * @param errorMessages
	 * 		
	 * @return
	 * 		The "signature" inner text return by the API
	 * @throws XMLStreamException
	 */
	private String handleSignature(XMLStreamReader reader, LinkedList<ApiAnswer> errorMessages) throws XMLStreamException{		
		StringBuilder signature = new StringBuilder();
		if(reader.hasNext()) {
			reader.next();
			if(START_ELEMENT == reader.getEventType() && EMBED_SIGNATURE_TAG.equals(reader.getLocalName())) {				
				//hack - we cannot directly extract the inner xml of the <signature /> so
				//we read all attributes and append them in a StringBuilder
				signature.append("<" + EMBED_SIGNATURE_TAG + " ");		
				for(int i=0; i<reader.getAttributeCount(); i++){
					signature.append(reader.getAttributeLocalName(i) + "=\"" + reader.getAttributeValue(i) + "\" ");
				}				
				signature.append("/>");				
			}			
		}
		
		if(signature.length()>0)
			return signature.toString();
		
		return null;
	}	
	
}
