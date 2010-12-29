package org.aggelos.baztag.api.xml;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.ResourceBundle;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import static javax.xml.stream.XMLStreamConstants.*;

/**
 * The purpose of this class is to handle the answer of the API which will usually be 
 * @author Daniel
 *
 */

public class ServiceAnswerParser {
	
	private XMLInputFactory inputFactory;
	private static final String START_TAG = "rsp";
	private static final String MESSAGE_TYPE_TAG = "message";
	private static final String MESSAGE_COMMENT_TAG = "comment";
	
	private static final ResourceBundle errorsBundle = ResourceBundle.getBundle("apierrors");
	
	public ServiceAnswerParser() {
		inputFactory = XMLInputFactory.newInstance();
	}

	
	/**
	 * Will parse the stream returned by the API to find errors
	 * @param answer 
	 * @return a list of errors
	 * @throws XMLStreamException
	 */
	public LinkedList<ApiAnswer> parse(InputStream answer) throws XMLStreamException  {
		/*
		 * Going for a StAX implementation, far easier, serves many purposes, while being fast and efficient
		 * May pose compatibility issues
		 */
		XMLStreamReader reader = inputFactory.createXMLStreamReader(answer);
		LinkedList<ApiAnswer> errorMessages = new LinkedList<ApiAnswer>();
		boolean foundRsp = false;
		while(reader.hasNext()) {
			if(START_ELEMENT == reader.getEventType()) {
				// the answer XML has no namespace so QName == localName
				if(START_TAG.equals(reader.getLocalName())) {
					foundRsp = true;
					handleRsp(reader, errorMessages);
				}
			}
			reader.next();
		}
		reader.close();
		return errorMessages;
		
		
	}

	private void handleRsp(XMLStreamReader reader, LinkedList<ApiAnswer> errorMessages) throws XMLStreamException {
		while(reader.hasNext()) {
			if(START_ELEMENT == reader.getEventType()) {
				// the answer XML has no namespace so QName == localName
				if(MESSAGE_TYPE_TAG.equals(reader.getLocalName())) {
					handleMessage(reader, errorMessages);
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
	
}
