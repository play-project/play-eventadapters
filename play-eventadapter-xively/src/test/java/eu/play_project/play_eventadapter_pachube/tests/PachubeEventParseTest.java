package eu.play_project.play_eventadapter_pachube.tests;

import static eu.play_project.play_commons.constants.Namespace.EVENTS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;

import org.apache.commons.io.IOUtils;
import org.event_processing.events.types.PachubeEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.util.ModelUtils;
import org.ontoware.rdf2go.vocabulary.RDF;

import eu.play_project.platformservices.eventvalidation.InvalidEventException;
import eu.play_project.platformservices.eventvalidation.Validator;
import eu.play_project.play_commons.constants.Stream;
import eu.play_project.play_eventadapter.AbstractSenderRest;
import eu.play_project.play_eventadapter_pachube.PachubeServlet;

public class PachubeEventParseTest {

	private static AbstractSenderRest eventSender = new AbstractSenderRest(Stream.PachubeFeed.getTopicQName()) {};
	
	@Before
	public void before() throws ServletException {
		PachubeServlet.initSesame();
		// Only for testing:
		eventSender.setNoNetworking(true);
	}

	@After
	public void after() {
		PachubeServlet.destroySesame();
	}
	
	@Test
	public void testPachubeEventCreation() throws InvalidEventException, IOException {

		String jsonText = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("pachube.json"), StandardCharsets.UTF_8);

		Model m  = PachubeServlet.createEventModel(jsonText);
		
		System.out.println(m.serialize(Syntax.Turtle));
		System.out.println();
		
		assertTrue("The created event model does not contain the right namespace in its ID.", m.getContextURI().toString().contains(EVENTS.getUri()));
		assertFalse("The created event model was emtpty!", m.isEmpty());
		assertTrue("The created event did not have to correct rdf:type declaration.", m.contains(Variable.ANY, RDF.type, PachubeEvent.RDFS_CLASS));
		
		assertTrue("Property NOT to be lifted.", m.contains(Variable.ANY, new URIImpl("http://www.linkedopenservices.org/ns/temp-json#value"), Variable.ANY));
		assertTrue("Property to be lifted.", m.contains(Variable.ANY, new URIImpl("http://www.linkedopenservices.org/ns/temp-json#value_description"), Variable.ANY));

		// Validate the resulting RDF
		ModelUtils.deanonymize(m);
		Validator v = new Validator().checkModel(m.getContextURI(), m);
		assertTrue("The created event did not pass the PLAY sanity checks for events.", v.isValid());
		
		
		// Notify this event (but actual networking was turned off above)
		eventSender.notify(m);
	}

}
