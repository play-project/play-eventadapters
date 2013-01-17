package eu.play_project.play_eventadapter_twitter;

import java.io.IOException;

import eu.play_project.play_commons.constants.Stream;

public class TwitterCli {

	/**
	 * This class can be used to run Twitter adapter from the command line. See
	 * also {@linkplain TwitterService} on how to run Twitter adapter from
	 * Tomcat.
	 * 
	 * @param args no arguments are currently necessary.
	 * @throws IOException if configuration file cannot be read.
	 */
	public static void main(String[] args) throws IOException {
		new TwitterProperties();
		TwitterConfiguration tc = new TwitterConfiguration();
		tc.setKeywords(TwitterProperties.getKeywords());
		tc.setLocationRestriction(TwitterProperties.getLocations());
		TwitterBackend tb = new TwitterBackend();
		tb.startStream(tc,
				new TwitterPublisher(Stream.TwitterFeed.getTopicQName()));
	}

}
