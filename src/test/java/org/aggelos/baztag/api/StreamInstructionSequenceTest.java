package org.aggelos.baztag.api;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import org.aggelos.baztag.api.inst.streaming.OnlineStreamInstruction;
import org.aggelos.baztag.api.inst.streaming.StreamInstructionSequence;
import org.junit.Before;
import org.junit.Test;

public class StreamInstructionSequenceTest {
	
	@Test
	public void testToParamUrl() {
		String bfm_webradio = "http://213.205.96.91:9100/";
		String fip_webradio = "http://mp3.live.tv-radio.com/fip/all/fip-32k.mp3";
		
		StreamInstructionSequence seq = new StreamInstructionSequence();
		try {
			seq.add(new OnlineStreamInstruction(new URL(bfm_webradio)));
			assertEquals("wrong playlist url!", "&urlList=" + bfm_webradio, seq.toParamUrl());
			seq.add(new OnlineStreamInstruction(new URL(fip_webradio)));
			assertEquals("wrong playlist url!", "&urlList=" + bfm_webradio + "|" + fip_webradio, seq.toParamUrl());
		} catch (MalformedURLException e) {
			fail(e.getMessage());
		}
	}

}
