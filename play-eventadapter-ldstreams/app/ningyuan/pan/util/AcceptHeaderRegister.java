/**
 * 
 */
package ningyuan.pan.util;

/**
 * @author Ningyuan Pan
 *
 */
public interface AcceptHeaderRegister {
	
	/**
	 * Handles the token string of type parsed from accept header
	 * @param type parsed type token
	 */
	public void registerType(String type);
	
	/**
	 * Handles the token string of subtype parsed from accept header
	 * @param stype parsed subtype token
	 */
	public void registerSubtype(String stype);
	
	/**
	 * Handles the token string of parameter name parsed from accept header
	 * @param para parsed parameter name
	 */
	public void registerParamName(String para);
	
	/**
	 * Handles the token string of parameter value parsed from accept header
	 * @param value parsed parameter value
	 */
	public void registerParamValue(String value);
	
	/**
	 * Handles the token string of quality parsed from accept header
	 * @param q parsed quality multiple 10 [0, 10]
	 */
	public void registerQuality(int q);
	
	/**
	 * Clear the register
	 */
	public void clear();
	
}
