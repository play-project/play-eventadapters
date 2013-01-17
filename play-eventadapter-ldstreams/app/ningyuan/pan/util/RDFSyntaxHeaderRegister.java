/**
 * 
 */
package ningyuan.pan.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ningyuan Pan
 *
 */
public class RDFSyntaxHeaderRegister implements AcceptHeaderRegister {
	
	// the list of accepted MIME type
	public static final String [] acceptedRDFSyntaxs = {"application/*", "text/*", "text/plain", "*/*", 
		"application/x-trig", "application/x-turtle", "text/x-nquads"};
	
	// the default preferred RDF syntax
	public static final String DEFAULT_SYNTAX = "application/x-turtle";
	// the default plain type
	public static final String PLAIN = "text/plain";
	
	// save the parsed tokens which belong to the same semantic group
	private StringBuilder RDFSyntax = new StringBuilder();
	
	// save the parsed accepted syntaxes in this priority list
	@SuppressWarnings("unchecked")
	private List<String> [] RDFSyntaxList = new ArrayList[11];
	
	public RDFSyntaxHeaderRegister(){
		for(int i = 0; i < RDFSyntaxList.length; i++){
			RDFSyntaxList[i] = new ArrayList<String>();
		}
	}
	
	/* (non-Javadoc)
	 * @see test.AcceptHeaderRegister#registerType()
	 */
	@Override
	public void registerType(String type) {
		RDFSyntax.append(type.trim()+"/");
	}

	/* (non-Javadoc)
	 * @see test.AcceptHeaderRegister#registerSubtype()
	 */
	@Override
	public void registerSubtype(String stype) {
		RDFSyntax.append(stype.trim());
	}

	/* (non-Javadoc)
	 * @see test.AcceptHeaderRegister#registerParamName()
	 */
	@Override
	public void registerParamName(String para) {}

	/* (non-Javadoc)
	 * @see test.AcceptHeaderRegister#registerParamValue()
	 */
	@Override
	public void registerParamValue(String value) {}

	/* (non-Javadoc)
	 * @see test.AcceptHeaderRegister#registerQuantity()
	 */
	@Override
	public void registerQuality(int q) {
		String acceptedFormat = isAcceptedType(RDFSyntax.toString());
		if(acceptedFormat != null)
			RDFSyntaxList[q].add(acceptedFormat);
		RDFSyntax.delete(0, RDFSyntax.length());
	}
	
	/* (non-Javadoc)
	 * @see test.AcceptHeaderRegister#registerQuantity()
	 */
	@Override
	public void clear() {
		RDFSyntax.delete(0, RDFSyntax.length());
		
		for(int i = RDFSyntaxList.length - 1; i >= 0; i--){
			RDFSyntaxList[i].clear();
		}
	}
	
	/**
	 * Get the most preferred RDF serialization format indicated 
	 * in accept header
	 * @return
	 */
	public String getPreferredRDFSyntax(){
		for(int i = RDFSyntaxList.length - 1; i >= 0; i--){
			if(!RDFSyntaxList[i].isEmpty())
				return RDFSyntaxList[i].get(0);
		}
		return null;
	}
	
	/*
	 * Test whether media type is accepted rdf format.
	 * 
	 * * / * : application/x-trig
	 * application / * : application/x-trig
	 * text / * : text/x-nquads
	 */
	private String isAcceptedType(String type){
		for(int i = 0; i < acceptedRDFSyntaxs.length; i++){
			if(type.equalsIgnoreCase(acceptedRDFSyntaxs[i])){
				switch(i){
					case 0:{
						return DEFAULT_SYNTAX;
					}
					case 1:{
						return acceptedRDFSyntaxs[6];
					}
					case 2:{
						return PLAIN;
					}
					case 3:{
						return PLAIN;
					}
					default:{
						return acceptedRDFSyntaxs[i];
					}
				}
			}
		}
		return null;
	}
}
