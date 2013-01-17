package eu.play_project.play_eventadapter_facebook;

import static eu.play_project.play_commons.constants.Namespace.EVENTS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Scanner;
import java.util.Set;

import javax.servlet.ServletException;

import org.event_processing.events.types.FacebookStatusFeedEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDF;

import eu.play_project.platformservices.eventvalidation.InvalidEventException;
import eu.play_project.platformservices.eventvalidation.Validator;
import eu.play_project.play_commons.constants.Stream;
import eu.play_project.play_commons.eventtypes.EventHelpers;
import eu.play_project.play_eventadapter.AbstractSender;

public class FacebookEventParseTest {
	
	private static AbstractSender eventSender = new AbstractSender(Stream.FacebookStatusFeed.getTopicQName()) {}; 

	@Before
	public void before() throws ServletException {
		// Only for testing:
		eventSender.setNoNetworking(true);
	}

	@Test
	public void testFacebookEventCreation() throws JSONException, ParseException, InvalidEventException {
		// Initialize mock test data
		String facebookTime = "1329733944"; // Must match the time of the status update in JSON
		String facebookUid = "100000058455726";
		JSONObject facebookUserInfo = new JSONObject(new Scanner(Test.class.getClassLoader().getResourceAsStream("FacebookEventParseTest-userinfo.json")).useDelimiter("\\A").next());
		JSONArray facebookStatusArray = new JSONArray(new Scanner(Test.class.getClassLoader().getResourceAsStream("FacebookEventParseTest-statusarray.json")).useDelimiter("\\A").next());

		Set<FacebookStatusFeedEvent> events = FacebookRealtimeServlet.createEventModels(facebookTime, facebookUid, facebookUserInfo, facebookStatusArray);
		
		assertFalse("No events were created.", events.isEmpty());
				
		for (FacebookStatusFeedEvent event : events) {
			Model m  = event.getModel();

			System.out.println(EventHelpers.serialize(m));
			System.out.println();

			assertTrue("The created event model does not contain the right namespace in its ID.", m.getContextURI().toString().contains(EVENTS.getUri()));
			assertFalse("The created event model was emtpty!", m.isEmpty());
			assertTrue("The created event did not have to correct rdf:type declaration.", m.contains(Variable.ANY, RDF.type, FacebookStatusFeedEvent.RDFS_CLASS));
			assertTrue("The created event model does not contain the right Facebook user ID '" + FacebookStatusFeedEvent.FACEBOOKID + "'.", m.contains(Variable.ANY, FacebookStatusFeedEvent.FACEBOOKID, facebookUid));
			
			// Validate the resulting RDF
			Validator v = new Validator().checkModel(event.getModel().getContextURI(), event.getModel());
			assertTrue("The created event did not pass the PLAY sanity checks for events.", v.isValid());

			// Notify this event (but actual networking was turned off above)
			eventSender.notify(m);
		}
		
	}

}
