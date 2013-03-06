package eu.play_project.play_eventadapter_pachube.tests;

import static eu.play_project.play_commons.constants.Event.EVENT_ID_SUFFIX;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.abdera.i18n.templates.Template;
import org.apache.commons.io.IOUtils;
import org.event_processing.events.types.PachubeEvent;
import org.junit.Test;
import org.linkedopenservices.json2rdf.JSON2RDF;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;

import eu.play_project.play_commons.constants.Namespace;
import eu.play_project.play_commons.eventtypes.EventHelpers;


/**
 * 
 * @author Steffen Stadtmüller
 * @author Roland Stühmer
 * 
 *Class to illustrate the use of JSON2RDF:
 *uses the tmdb (http://themoviedb.org) JSON API to query for information about a movie
 *and wraps the replied JSON in RDF.
 *Semantics are injected by executing a CONSTRUCT query over the build RDF
 *
 */
public class LiftingQueryTests {
	
	@Test
	public void testPachubeLiftingQuery() throws MalformedURLException, IOException{
		
		
		String liftingQuery = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("liftingQueryPachube.rq"), "UTF-8");
		String json = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("pachube.json"), "UTF-8");
		
		/*
		 * Event object:
		 */
		System.out.println("############### Event headers: #################");
		// Create an event ID used in RDF context and RDF subject
		String eventId = Namespace.EVENTS.getUri() + "e-" + UUID.randomUUID();
		
		PachubeEvent event = new PachubeEvent(
				EventHelpers.createEmptyModel(eventId),
				eventId + EVENT_ID_SUFFIX, true);

		event.setEndTime(Calendar.getInstance());
		
		//print out event headers
		event.getModel().writeTo(System.out, Syntax.Ntriples);
		System.out.println();

		
		/*
		 * Generic RDF:
		 */
		System.out.println("############### Generic RDF: #################");
		//instantiate JSON2RDF transformer
		JSON2RDF myTransformer = new JSON2RDF(false);
		Map<String, Template> map = new HashMap<String, Template>(); //this map was used to build new URIs, but is not longer needed (i.e., stays empty) since SPARQL 1.1 is capable to do this by it self

		// Fetch JSON from a URL:
		//String url="https://api.pachube.com/v2/feeds/34843.json?key=APIKEY";
		//Model eventBody = myTransformer.getJSON2RDF(new URL(url), map); //load JSON as RDF directly in a RDF2go model
		
		// or fetch JSON from a file:
		Model genericRdf = myTransformer.getJSON2RDF(json, map); //load JSON as RDF directly in a RDF2go model
		genericRdf.addModel(event.getModel());
		
		//print out generic model (including headers)
		genericRdf.writeTo(System.out, Syntax.Ntriples);
		System.out.println();
		
		/*
		 * Meaningful RDF:
		 */
		System.out.println("############### Meaningful RDF: #################");
		//lift generic RDF into meaningful RDF as defined by the CONSTRUCT query
		Model meaningfulRdf = EventHelpers.createEmptyModel(eventId);
		
		// to use BIND in queries I must set the query syntax to 1.1
		com.hp.hpl.jena.query.Syntax.defaultSyntax = com.hp.hpl.jena.query.Syntax.syntaxSPARQL_11;
		
		// run query:
		meaningfulRdf.addAll(genericRdf.sparqlConstruct(liftingQuery).iterator());
		
		//print out the meaningful model
		meaningfulRdf.writeTo(System.out, Syntax.Ntriples);
		System.out.println();
		
		/*
		 * Event object:
		 */
		// merge both models (adding payload to the event)
		event.getModel().addModel(meaningfulRdf);
		
		// do something with the event.....

	}

}

