package org.aggelos.baztag.api.xml;

public class ApiAnswer {
	
	private ApiAnswers answertype;
	private String message;
	public ApiAnswers getAnswertype() {
		return answertype;
	}
	
	public String getMessage() {
		return message;
	}
	
	
	public ApiAnswer(ApiAnswers answertype, String message) {
		super();
		this.answertype = answertype;
		this.message = message;
	}
	
	
	
	
	
}
