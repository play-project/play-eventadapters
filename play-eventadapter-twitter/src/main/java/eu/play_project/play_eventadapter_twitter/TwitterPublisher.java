package eu.play_project.play_eventadapter_twitter;

import static eu.play_project.play_commons.constants.Event.EVENT_ID_SUFFIX;

import java.util.Calendar;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.event_processing.events.types.Event;
import org.event_processing.events.types.TwitterEvent;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import twitter4j.Status;
import eu.play_project.play_commons.constants.Source;
import eu.play_project.play_commons.constants.Stream;
import eu.play_project.play_commons.eventtypes.EventHelpers;
import eu.play_project.play_eventadapter.AbstractSenderRest;


public class TwitterPublisher extends AbstractSenderRest {

	public TwitterPublisher(QName defaultTopic) {
		super(defaultTopic);
		Logger.getAnonymousLogger().info("Initialized event sender with default topic " + defaultTopic);
	}

	public void publish(Status status) {
		notify(createEvent(status));
	}
	
	public Event createEvent(Status status)
	{
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
