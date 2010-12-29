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
	
	
	/**
	 * @param is which should normaly be the api URL with action 2 
	 * @return the list of friends
	 * @throws AnswerParsingException 
	 */
	public List<String> parseFriends(InputStream is) throws AnswerParsingException {
		try {
			XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
			
			/*
			 * The format is the following 
			 * 
			 * <?xml version="1.0" encoding="UTF-8"?>
				<rsp>
				<listfriend nb="1"/>
				<friend name="toto"/>
				</rsp>
			 * We'll simplify, considering that the rsp tag is just well a placeholder, 
			 * and focus directly on the list of friends
			 */
			LinkedList<String> friends = new LinkedList<String>();
			
			while(reader.hasNext()) {
				if(START_ELEMENT == reader.getEventType() && "friend".equals(reader.getLocalName())) {
					friends.add(reader.getAttributeValue(null, "name"));
				}
				reader.next();
			}
			reader.close();
			return friends;
		} catch (XMLStreamException e) {
			// encapsulate the error, throw back;
			throw new AnswerParsingException(e);
		}
		
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

	/**
	 * @param is which should normaly be the api URL with action 7
	 * @return the awake state of the Nabaztag
	 * @throws AnswerParsingException 
	 */
	public boolean parseAwake(InputStream openStream) throws AnswerParsingException {
		try {
			XMLStreamReader reader = inputFactory.createXMLStreamReader(openStream);
			
			/*
			 * Spec is
			 * <?xml version="1.0" encoding="UTF-8"?>
				<rsp>
				<rabbitSleep>YES</rabbitSleep>
				</rsp>
			 * pretty straightforward
			 */
			
			while(reader.hasNext()) {
				if(START_ELEMENT == reader.getEventType() && "rabbitSleep".equals(reader.getLocalName())) {
					String sig = reader.getElementText();
					reader.close();
					return !"YES".equals(sig);
				}
				reader.next();
			}
			throw new AnswerParsingException("Did not find sleep state while parsing the signature URL");
		} catch (XMLStreamException e) {
			// encapsulate the error, throw back;
			throw new AnswerParsingException(e);
		}
	}


	/**
	 * @param is which should normaly be the api URL with action 8
	 * @return the version of the Nabaztag
	 * @throws AnswerParsingException 
	 */
	public NabaztagVersion parseVersion(InputStream openStream) throws AnswerParsingException {
		try {
			XMLStreamReader reader = inputFactory.createXMLStreamReader(openStream);
			
			/*
			 * Spec is
			 * <?xml version="1.0" encoding="UTF-8"?>
				<rsp>
				<rabbitVersion>V1</rabbitVersion>
				</rsp>
			 * pretty straightforward
			 */
			
			while(reader.hasNext()) {
				if(START_ELEMENT == reader.getEventType() && "rabbitVersion".equals(reader.getLocalName())) {
					String sig = reader.getElementText();
					reader.close();
					return NabaztagVersion.valueOf(sig);
				}
				reader.next();
			}
			throw new AnswerParsingException("Did not find a version while parsing the signature URL");
		} catch (XMLStreamException e) {
			// encapsulate the error, throw back;
			throw new AnswerParsingException(e);
		}
	}


	/**
	 * @param is which should normaly be the api URL with action 10
	 * @return the name of the Nabaztag
	 * @throws AnswerParsingException 
	 */
	public String parseName(InputStream openStream) throws AnswerParsingException {
		try {
			XMLStreamReader reader = inputFactory.createXMLStreamReader(openStream);
			
			/*
			 * Spec is
			 * <?xml version="1.0" encoding="UTF-8"?>
				<rsp>
				<rabbitName>nabmaster</rabbitName>
				</rsp>
			 * pretty straightforward
			 */
			
			while(reader.hasNext()) {
				if(START_ELEMENT == reader.getEventType() && "rabbitName".equals(reader.getLocalName())) {
					String sig = reader.getElementText();
					reader.close();
					return sig;
				}
				reader.next();
			}
			throw new AnswerParsingException("Did not find a name while parsing the signature URL");
		} catch (XMLStreamException e) {
			// encapsulate the error, throw back;
			throw new AnswerParsingException(e);
		}
	}
	
	/**
	 * @param is which should normaly be the api URL the earpos param
	 * @return the position of both ears, left first
	 * @throws AnswerParsingException 
	 */
	public int[] parseEarPosition(InputStream openStream) throws AnswerParsingException {
		try {
			XMLStreamReader reader = inputFactory.createXMLStreamReader(openStream);
			
			/*
			 * Spec is
			 *<?xml version="1.0" encoding="UTF-8"?>
				<rsp>
				<message>POSITIONEAR</message>
				<leftposition>8</leftposition>
				<rightposition>10</rightposition>
				</rsp>
			 * pretty straightforward
			 */
			int leftear = -1;
			int rightear = -1;
			while(reader.hasNext()) {
				if(START_ELEMENT == reader.getEventType() && "leftposition".equals(reader.getLocalName())) {
					String sig = reader.getElementText();
					leftear = Integer.parseInt(sig);
					
				}
				if(START_ELEMENT == reader.getEventType() && "rightposition".equals(reader.getLocalName())) {
					String sig = reader.getElementText();
					rightear = Integer.parseInt(sig);
					
				}
				reader.next();
			}
			reader.close();
			if(leftear == -1 || rightear == -1 ) {
				throw new AnswerParsingException("Could not find a position for both ears");
			}
			int[] ret = new int[2];
			ret[0] = leftear;
			ret[1] = rightear;
			return ret;
		} catch (XMLStreamException e) {
			// encapsulate the error, throw back;
			throw new AnswerParsingException(e);
		}
	}
	
	
	
}
