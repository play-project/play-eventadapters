package eu.play_project.play_eventadapter_twitter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterBackend {

	private final ArrayList<TwitterStream> twitterStreams = new ArrayList<TwitterStream>();
	TwitterStream twitterStream;

	/**
	 * Start a Twitter stream.
	 * 
	 * @param tc
	 * @param p
	 * @return A {@linkplain TwitterStream} object to be used with
	 *         {@linkplain TwitterBackend#stopStream(TwitterStream)} later
	 */
	public TwitterStream startStream(TwitterConfiguration tc,
			final TwitterPublisher p) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setHttpRetryCount(10);

		String consumerKey = TwitterProperties.getTwitter1ConsumerKey();
		String comsumerSecret = TwitterProperties.getTwitter1ComsumerSecret();
		String accessToken = TwitterProperties.getTwitter1AccessToken();
		String accessTokenSecret = TwitterProperties.getTwitter1AccessTokenSecret();

		twitterStream = new TwitterStreamFactory().getInstance();
		twitterStream.setOAuthConsumer(consumerKey, comsumerSecret);

		twitterStream.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret));

		StatusListener listener = new StatusListener() {
			@Override
			public void onStatus(Status status) {
				p.publish(status);
			}

			@Override
			public void onDeletionNotice(
					StatusDeletionNotice statusDeletionNotice) {
				Logger.getAnonymousLogger().info(
						"Got a status deletion notice id:"
								+ statusDeletionNotice.getStatusId());
			}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				Logger.getAnonymousLogger().info(
						"Got track limitation notice:"
								+ numberOfLimitedStatuses);
			}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
				Logger.getAnonymousLogger().info(
						"Got scrub_geo event userId:" + userId
								+ " upToStatusId:" + upToStatusId);
			}

			@Override
			public void onException(Exception ex) {
				Logger.getAnonymousLogger().log(Level.WARNING, "Twitter EXP:",
						ex);
			}

			@Override
			public void onStallWarning(StallWarning warning) {
				Logger.getAnonymousLogger().log(Level.WARNING, "Twitter Stall Warning:",
						warning);
				}
		};

		if (!TwitterProperties.hasKeywords()
				&& !TwitterProperties.hasLocations()) {

			twitterStream.addListener(listener);
			twitterStream.sample();
		} else {
			FilterQuery query = new FilterQuery();
			if (TwitterProperties.hasLocations()) {
				query.locations(tc.getLocations());
			}
			if (TwitterProperties.hasKeywords()) {
				query.track(tc.getKeywords());
			}
			twitterStream.addListener(listener);
			twitterStream.filter(query);
		}

		this.twitterStreams.add(twitterStream);
		return twitterStream;
	}

	public void stopStreams() {
		Iterator<TwitterStream> it = this.twitterStreams.iterator();
		while (it.hasNext()) {
			it.next().cleanUp();
			it.remove();
		}
	}

	public void stopStream(TwitterStream twitterStream) {
		twitterStream.cleanUp();
		this.twitterStreams.remove(twitterStream);
	}

	@Override
	protected void finalize() throws Throwable {
		this.stopStreams();
		super.finalize();
	}

}
