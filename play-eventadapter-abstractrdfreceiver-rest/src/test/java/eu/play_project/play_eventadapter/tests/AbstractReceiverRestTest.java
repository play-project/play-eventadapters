package eu.play_project.play_eventadapter.tests;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.play_project.play_commons.constants.Constants;
import eu.play_project.play_commons.constants.Stream;
import eu.play_project.play_eventadapter.AbstractReceiverRest;
import eu.play_project.play_eventadapter.NoRdfEventException;

public class AbstractReceiverRestTest {
	
	private static AbstractReceiverRest eventConsumer;
	
	@Before
	public void setup() {
		eventConsumer = new AbstractReceiverRest(){};
	}
	
	@Test
	public void testConfigurationProperties() {
		Assert.assertNotNull(Constants.getProperties("play-eventadapter.properties").getProperty(
				"play.platform.api.token"));
		Assert.assertNotNull(Constants.getProperties().getProperty(
				"play.platform.endpoint"));
	}
	
	/**
	 * Manual test
	 */
	public static void main(String[] args) {
		eventConsumer = new AbstractReceiverRest(){};
		
		/*
		 * test subscribe and unsubscribe
		 */
		String id = eventConsumer.subscribe(Stream.TaxiUCESRRecomDcep.getTopicUri(), "http://host:port/foo/bar");
		assert(!id.equals(""));
		eventConsumer.unsubscribe(id);
		assert(true);


		/*
		 * test GetTopics
		 */
		String id1 = eventConsumer.subscribe(Stream.PersonalStream1.getTopicUri(), "http://host:port/foo/bar");
		assert(!id1.equals(""));
		
		String id2 = eventConsumer.subscribe(Stream.TwitterFeed.getTopicUri(), "http://host:port/foo/bar");
		assert(!id2.equals(""));
		
		List<String> topics = eventConsumer.getTopics();
		assert(topics.size()>=2);
		assert(topics.get(0).equalsIgnoreCase(Stream.PersonalStream1.getTopicUri()+"#stream")
						|| topics.get(0).equalsIgnoreCase(Stream.TaxiUCCall.getTopicUri()+"#stream"));
		assert(topics.get(1).equalsIgnoreCase(Stream.PersonalStream1.getTopicUri()+"#stream")
				|| topics.get(1).equalsIgnoreCase(Stream.TaxiUCCall.getTopicUri()+"#stream"));
		
		eventConsumer.unsubscribe(id1);
		eventConsumer.unsubscribe(id2);
		assert(true);
	}
	
	@Test(expected = NoRdfEventException.class)
	public void testParseRdf() throws NoRdfEventException {
		eventConsumer.parseRdfRest("");
	}
}
