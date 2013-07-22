package eu.play_project.play_eventadapter_twitter;

import static eu.play_project.play_commons.constants.Event.EVENT_ID_SUFFIX;

import java.io.File;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.event_processing.events.types.Event;
import org.event_processing.events.types.TwitterEvent;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.rdf2go.RepositoryModelSet;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import twitter4j.Status;
import eu.play_project.play_commons.constants.Source;
import eu.play_project.play_commons.constants.Stream;
import eu.play_project.play_commons.eventtypes.EventHelpers;
import eu.play_project.play_eventadapter.AbstractSenderRest;


public class TwitterPublisher extends AbstractSenderRest {

	private SailRepository sesameRepository;
	private RepositoryModelSet sesame;

	public TwitterPublisher(QName defaultTopic) {
		super(defaultTopic);
		Logger.getAnonymousLogger().info("Initialized event sender with default topic " + defaultTopic);
	}
	
    public void init() {
		try {
			File path = new File(System.getProperty("java.io.tmpdir"));

			File dataDir = new File(path
					+ "/play-eventadapter-twitter/sesame/");
			
			Logger.getAnonymousLogger().log(Level.INFO,
					"Creating event history at path: " + dataDir);
			
			sesameRepository = new SailRepository(new MemoryStore(dataDir));
			sesameRepository.initialize();
			
			sesame = new RepositoryModelSet(sesameRepository);
			sesame.open();
		} catch (RepositoryException e) {
			Logger.getAnonymousLogger().log(Level.WARNING,
					"Problem while initializing Sesame storage.", e);
		}
    }
    
    public void destroy() {
		if (sesameRepository != null) {
			try {
				sesame.close();
				sesameRepository.shutDown();
			} catch (RepositoryException e) {
				Logger.getAnonymousLogger().log(Level.WARNING,
						"Problem while shutting down Sesame storage.", e);
			}
		}
 	
    }
    
	public void publish(Status status) {
		
		Event event = createEvent(status);
		
		/*
		 * Create a log of events for testing purposes
		 */
		sesame.addModel(event.getModel());

		notify(event);
	}
	
	public Event createEvent(Status status)	{
		// Create an event ID used in RDF context and RDF subject
		String eventId = EventHelpers.createRandomEventId("twitter");

		TwitterEvent event = new TwitterEvent(
				// set the RDF context part
				EventHelpers.createEmptyModel(eventId),
				// set the RDF subject
				eventId + EVENT_ID_SUFFIX,
				// automatically write the rdf:type statement
				true);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(status.getCreatedAt());
		if (status.getURLEntities() != null)
		{
			for(int i = 0; i < status.getURLEntities().length; i++)
				event.addLinksto(new URIImpl(status.getURLEntities()[i].getURL().toString()));
		}
		if (status.getHashtagEntities() != null)
		{
			for(int i = 0; i < status.getHashtagEntities().length; i++) event.addTwitterHashTag(status.getHashtagEntities()[i].getText().toString());
		}
		if (status.getUserMentionEntities() != null)
		{
			for(int i = 0; i < status.getUserMentionEntities().length; i++) event.addTwitterUserMention(status.getUserMentionEntities()[i].getScreenName().toString());
		}
		
		event.setEndTime(cal);
		event.setStream(new URIImpl(Stream.TwitterFeed.getUri()));
		event.setSource(new URIImpl(Source.TwitterAdapter.toString()));
		event.setTwitterFriendsCount(status.getUser().getFriendsCount());
		event.setTwitterFollowersCount(status.getUser().getFollowersCount());
		event.setTwitterIsRetweet(status.isRetweet());
		event.setContent(status.getText());
		event.setTwitterScreenName(status.getUser().getScreenName());
		event.setTwitterName(status.getUser().getName());
		if (status.getGeoLocation() != null) {
			EventHelpers.setLocationToEvent(event, status.getGeoLocation().getLatitude(), status.getGeoLocation().getLongitude());
		}
		
		Logger.getAnonymousLogger().info(event.getModel().serialize(Syntax.Turtle));
		return event;
	}
	

}
