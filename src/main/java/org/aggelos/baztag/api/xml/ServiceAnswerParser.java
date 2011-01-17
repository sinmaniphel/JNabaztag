package org.aggelos.baztag.api.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.KeyStore.Builder;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.aggelos.baztag.api.Message;
import org.aggelos.baztag.api.Nabaztag;
import org.aggelos.baztag.api.NabaztagVersion;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

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
	private static final String FRIEND_TYPE_TAG = "friend";
	private static final String RABBIT_SLEEP_TYPE_TAG = "rabbitSleep";
	private static final String RABBIT_VERSION_TYPE_TAG = "rabbitVersion";
	private static final String RABBIT_NAME_TYPE_TAG = "rabbitName";
	private static final String LEFT_POSITION_TYPE_TAG = "leftposition";
	private static final String RIGHT_POSITION_TYPE_TAG = "rightposition";
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
	public LinkedList<ApiAnswer> parse(InputStream answer) throws XMLStreamException  {
		/*
		 * Going for a StAX implementation, far easier, serves many purposes, while being fast and efficient
		 * May pose compatibility issues
		 */
		XMLStreamReader reader = inputFactory.createXMLStreamReader(answer);
		LinkedList<ApiAnswer> errorMessages = new LinkedList<ApiAnswer>();
		while(reader.hasNext()) {
			if(START_ELEMENT == reader.getEventType()) {
				// the answer XML has no namespace so QName == localName
				if(START_TAG.equals(reader.getLocalName())) {
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
	public List<Message> parseMessages(InputStream openStream) throws AnswerParsingException {
		try {
			XMLStreamReader reader = inputFactory.createXMLStreamReader(openStream);
			
			/*
			 * According to doc, the content is like follows
			 * <?xml version="1.0" encoding="UTF-8"?>
				<rsp>
				<listreceivedmsg nb="1"/>
				<msg from="toto" title="my message" date="today 11:59" url="broad/001/948.mp3"/>
				</rsp>
			 *Let's simplify and handle only the msg tags
			 */
			LinkedList<Message> messages = new LinkedList<Message>();
			while(reader.hasNext()) {
				if(START_ELEMENT == reader.getEventType() && "msg".equals(reader.getLocalName())) {
					String from = reader.getAttributeValue(null, "from");
					String title = reader.getAttributeValue(null, "title");
					String date = reader.getAttributeValue(null, "date");
					String url = reader.getAttributeValue(null, "url");
					messages.add(new Message(from, title, date, url));
				}
				reader.next();
			}
			reader.close();
			return messages;
		} catch (XMLStreamException e) {
			// encapsulate the error, throw back;
			throw new AnswerParsingException(e);
		}
	}


	/**
	 * @param is which should normaly be the api URL with action 5
	 * @return the signature of the nabaztag, a HTML content
	 * @throws AnswerParsingException 
	 */
	public String parseSignature(InputStream openStream) throws AnswerParsingException {
			
			/*
			 * Spec is
			 * <?xml version="1.0" encoding="UTF-8"?>
				<rsp>
				<signature>XXXXX</signature>
				</rsp>
			 * Not straightforward since the signature is HTML content
			 * Therefore using DOM
			 */
		
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document sigDoc = builder.parse(openStream);
			
			// going straight to signature
			Node sigNode = sigDoc.getDocumentElement().getFirstChild();
			
			Document target = builder.newDocument();
			target.adoptNode(sigNode);
			target.appendChild(sigNode);
			
			DOMSource domSource = new DOMSource(target);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			writer.flush();
			String stringResult = writer.toString(); 
			writer.close();
			
			return stringResult.replace("<signature>", "").replace("</signature>", "").replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>", "");
		} catch (DOMException e) {
			// encapsulate the error, throw back;
			throw new AnswerParsingException(e);
		} catch (TransformerConfigurationException e) {
			// encapsulate the error, throw back;
			throw new AnswerParsingException(e);
		} catch (ParserConfigurationException e) {
			// encapsulate the error, throw back;
			throw new AnswerParsingException(e);
		} catch (SAXException e) {
			// encapsulate the error, throw back;
			throw new AnswerParsingException(e);
		} catch (IOException e) {
			// encapsulate the error, throw back;
			throw new AnswerParsingException(e);
		} catch (TransformerFactoryConfigurationError e) {
			// encapsulate the error, throw back;
			throw new AnswerParsingException(e);
		} catch (TransformerException e) {
			// encapsulate the error, throw back;
			throw new AnswerParsingException(e);
		}
		
	}	
	
}
