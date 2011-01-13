/**
 * 
 */
package org.aggelos.baztag.api.inst;

/**
 * <p>An instruction that's aim is to retrieve an information about the Nabaztag.</p>
 * <p>
 * 	All infos available through the API are listed here: http://doc.nabaztag.com/api/home.html#getinfo
 * </p>
 * @author tvibes
 *
 */
public class RetrieveInfoInstruction extends AbstractInstruction {
	
	public static final RetrieveInfoInstruction TO_PREVIEW_TTS = new RetrieveInfoInstruction(1);
	public static final RetrieveInfoInstruction FRIENDS_LIST = new RetrieveInfoInstruction(2);
	public static final RetrieveInfoInstruction INBOX = new RetrieveInfoInstruction(3);
	public static final RetrieveInfoInstruction TIMEZONE = new RetrieveInfoInstruction(4);
	public static final RetrieveInfoInstruction SIGNATURE = new RetrieveInfoInstruction(5);
	public static final RetrieveInfoInstruction BLACKLIST = new RetrieveInfoInstruction(6);
	public static final RetrieveInfoInstruction SLEEPING_STATUS = new RetrieveInfoInstruction(7);
	public static final RetrieveInfoInstruction VERSION = new RetrieveInfoInstruction(8);
	public static final RetrieveInfoInstruction SUPPORTED_LANGUAGES = new RetrieveInfoInstruction(9);
	public static final RetrieveInfoInstruction NAME = new RetrieveInfoInstruction(10);
	public static final RetrieveInfoInstruction SELECTED_LANGUAGE = new RetrieveInfoInstruction(11);
	public static final RetrieveInfoInstruction TO_PREVIEW_MESSAGE = new RetrieveInfoInstruction(12);
	
	
	private int typeInfo = 0;

	/**
	 * 
	 */
	public RetrieveInfoInstruction(int typeInfo) {
		this.typeInfo = typeInfo;
		this.paramName = "action";
	}

	/* (non-Javadoc)
	 * @see org.aggelos.baztag.api.NabaztagInstruction#getParamValue()
	 */
	@Override
	public String getParamValue() {		
		return "" + typeInfo;
	}

}
