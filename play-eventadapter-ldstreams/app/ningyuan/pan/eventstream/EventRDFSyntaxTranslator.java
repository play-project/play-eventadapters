/**
 * 
 */
package ningyuan.pan.eventstream;

import java.util.HashMap;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;

import fr.inria.eventcloud.api.CompoundEvent;


/**
 * @author Ningyuan Pan
 *
 */
public class EventRDFSyntaxTranslator {
	
	private Model event;
	
	private HashMap<String, String> syntaxs = new HashMap<String, String>();
	
	EventRDFSyntaxTranslator(){}
	
	EventRDFSyntaxTranslator(Model event) {
		this.event = event;
	}
	
	/*
	 * Buffered at HTTPStreamObserver side. Used by HTTPBufferStream
	 */
	synchronized String getEventInSyntax(String s){
		String ret = syntaxs.get(s);
		if(ret == null){
			ret = event.serialize(Syntax.forMimeType(s));
			syntaxs.put(s, ret);
		}
		return ret;
	}
	
	//********************************************************************
	/*
	 * Buffered at Event dispatcher side. Used by RDFHTTPStream + FetchWorker
	 * (It is not used now, because of uncatchable sub/unsub exception from 
	 *   event cloud)
	 */
	void setNextEvent(Model e){
		event = e;
		syntaxs.clear();
	}
	
	String RDFinSyntax(String syn){
		String ret = syntaxs.get(syn);
		if(ret == null){
	
			ret = event.serialize(Syntax.forMimeType(syn));
			syntaxs.put(syn, ret);
		}
		return ret;
	}
	//********************************************************************
}
