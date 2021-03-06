package eu.play_project.play_eventadapter.tests;

import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import junit.framework.Assert;

import org.event_processing.events.types.Event;
import org.event_processing.events.types.UcTelcoCall;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ontoware.rdf2go.model.Model;
import org.ow2.play.governance.platform.user.api.rest.PublishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.play_project.platformservices.eventvalidation.InvalidEventException;
import eu.play_project.platformservices.eventvalidation.Validator;
import eu.play_project.play_commons.constants.Constants;
import eu.play_project.play_commons.constants.Source;
import eu.play_project.play_commons.constants.Stream;
import eu.play_project.play_commons.eventtypes.EventHelpers;
import eu.play_project.play_eventadapter.AbstractReceiverRest;
import eu.play_project.play_eventadapter.AbstractSenderRest;
import eu.play_project.play_eventadapter.NoRdfEventException;

public class SendAndReceiveTest {

	private static HttpServer server;
	private static final String BASE_URI = Constants.getProperties().getProperty("play.platform.endpoint");
	private static final List<Model> eventSink = Collections.synchronizedList(new ArrayList<Model>());
	private static Logger logger = LoggerFactory.getLogger(SendAndReceiveTest.class);

	@BeforeClass
	public static void setupBeforeClass() {
		TestListenerRest listener = new TestListenerRest(eventSink);
		
		final ResourceConfig rc = new ResourceConfig()
				.register(listener)
				.register(MoxyJsonFeature.class);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);

        logger.info("Test server started.");
	}
	
		
	@Test
	public void testRestfulSendAndReceive() throws InvalidEventException {

		/*
		 * (1) Send event
		 */
		AbstractSenderRest rdfSender = new AbstractSenderRest("http://example.com/topic", BASE_URI);
		final String MY = "http://mynamespace.com/";
		
		Event event = EventHelpers.builder("UnitTest", true)
				.type(UcTelcoCall.RDFS_CLASS)
				.stream(Stream.TaxiUCCall)
				.source(Source.UnitTest)
				.addProperty(MY + "myProp", "Hello World")
				.build();
		
		rdfSender.notify(event);
		
        /*
         * (2) Wait for the event to be received:
         */
   		try {
			synchronized (this) {
				this.wait(1000);
			}
		} catch(InterruptedException e) {
			Assert.fail(e.getMessage());
		}
		
		/*
		 * (3) Check if event is receieved
		 */
   		Assert.assertEquals(1, eventSink.size());
		Validator v = new Validator().checkModel(eventSink.get(0));
		assertTrue("The created event did not pass the PLAY sanity checks for events.", v.isValid());
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		server.stop();
        logger.info("Test server stopped.");
	}

	@Path("/") // overwrite the Path from PublishService
	@Singleton
	static class TestListenerRest extends Application implements PublishService {

		private final List<Model> eventSink;
		private final Logger logger = LoggerFactory.getLogger(TestListenerRest.class);
		private final AbstractReceiverRest rdfReceiver = new AbstractReceiverRest() {};

		public TestListenerRest() {  // For JAXB
			this.eventSink = null;
		}
		
		public TestListenerRest(List<Model> eventSink) {
			this.eventSink = eventSink;
	        logger.info("Test listener started.");
		}
		
		@Override
		public Response notify(String resource, String message) {
			logger.info("Test listener received event.");
			try {
				eventSink.add(rdfReceiver.parseRdfRest(message));
			} catch (NoRdfEventException e) {
				logger.error("Test listener encountered error.", e);
				Assert.fail("Test listener encountered error: " + e.getMessage());
			}
			return null;
		}
	}
}

