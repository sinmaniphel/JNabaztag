/**
 * 
 */
package org.aggelos.baztag.api.inst.streaming;

import java.net.URL;

import org.aggelos.baztag.api.StreamInstruction;

/**
 * @author tvibes
 *
 */
public class OnlineStreamInstruction implements StreamInstruction {
	
	private URL streamUrl = null;
	
	public OnlineStreamInstruction(URL url){
		this.streamUrl = url;
	}

	/*
	 * (non-Javadoc)
	 * @see org.aggelos.baztag.api.StreamInstruction#getStreamLocation()
	 */
	@Override
	public String getStreamLocation() {
		if(streamUrl!=null){
			String res = streamUrl.getProtocol() + "://" + streamUrl.getHost();
			if(streamUrl.getPort()>0)
				res+= ":" + streamUrl.getPort();			
			res+= streamUrl.getPath();
			return res;
			
		}
		
		return null;
	}

}
