package eu.play_project.play_eventadapter_twitter.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Scanner;

import org.event_processing.events.types.Event;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.model.Syntax;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.json.DataObjectFactory;
import eu.play_project.platformservices.eventvalidation.InvalidEventException;
import eu.play_project.platformservices.eventvalidation.Validator;
import eu.play_project.play_commons.constants.Stream;
import eu.play_project.play_eventadapter_twitter.TwitterPublisher;

/**
 * Testing some features of the Twitter Adapter.
 * 
 * @author stuehmer
 *
 * @version $Revision: 34086 $
 *
 */
public class TwitterPublisherTest {

	TwitterPublisher twitterPublisher;
	
	@Before
	public void setup() {
		this.twitterPublisher = new TwitterPublisher(Stream.TwitterFeed.getTopicQName());
		// Only for testing:
		this.twitterPublisher.setNoNetworking(true);
	}
	
	@Test
	public void testCreateEvent() throws TwitterException, InvalidEventException {
		String jsonText = new Scanner(this.getClass().getClassLoader().getResourceAsStream("TestTwitterStatus.json")).useDelimiter("\\A").next();
		
		Status status = DataObjectFactory.createStatus(jsonText);
		
		Event event = this.twitterPublisher.createEvent(status);
		
		System.out.println(event.getModel().serialize(Syntax.Turtle));
		System.out.println();
		
		assertEquals("Check for expected endTime in the event.", javax.xml.bind.DatatypeConverter
				.parseDateTime("2011-06-02T15:06:45.000Z"), event.getEndTime());
		
		// Validate the resulting RDF
		Validator v = new Validator().checkModel(event.getModel().getContextURI(), event.getModel());
		assertTrue("The created event did not pass the PLAY sanity checks for events.", v.isValid());
		
		// Notify this event (but actual networking was turned off above)
		this.twitterPublisher.notify(event);
	}
}
