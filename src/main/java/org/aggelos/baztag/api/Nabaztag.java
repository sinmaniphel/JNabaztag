package org.aggelos.baztag.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class Nabaztag {

	public static final String NABAZTAG_API_URL = "http://api.nabaztag.com/vl/FR/api.jsp";
	public static final String baseFormat = "%1$s?sn=%2$s&token=%3$s";
	
	private String serialNumber;
	
	private String token;
		
	public Nabaztag(String serialNumber, String token) {
		super();
		this.serialNumber = serialNumber;
		this.token = token;
	}
	
	public void execute(NabaztagInstructionSequence sequence) {
		String baseUrl = String.format(this.baseFormat, NABAZTAG_API_URL,serialNumber,token);
		
		String fullUrl = baseUrl+sequence.toParamUrl();
		System.out.println(fullUrl);
		
		try {
			URL url = new URL(fullUrl);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;

			while ((line = reader.readLine()) != null) {
			    System.out.println(line);
			}
			reader.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
