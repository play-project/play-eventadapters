package eu.play_project.play_eventadapter_facebook;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.event_processing.events.types.FacebookStatusFeedEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.ebmwebsourcing.easycommons.xml.XMLHelper;

import eu.play_project.play_commons.constants.Source;
import eu.play_project.play_commons.constants.Stream;
import eu.play_project.play_commons.eventtypes.EventHelpers;
import eu.play_project.play_eventadapter.AbstractSender;


public class FacebookRealtimeServlet extends HttpServlet {

	private static final long serialVersionUID = 3521837119178997113L;

	static UserDatabase udb = new UserDatabase();
	private static String VERIFY_TOKEN = FacebookUtil.getVerifyToken();
	private static AbstractSender eventSender = new AbstractSender(Stream.FacebookStatusFeed.getTopicQName()) {};

	@Override
	public void init() throws ServletException {


		super.init();
	}

	@Override
	public void doGet(final HttpServletRequest req,	final HttpServletResponse resp) throws IOException {
		Logger.getAnonymousLogger().info("Invoking doGet.");
		if ("subscribe".equals(req.getParameter("hub.mode"))
				&& VERIFY_TOKEN.equals(req
						.getParameter("hub.verify_token"))) {
			resp.getOutputStream().print(req.getParameter("hub.challenge"));
		}
	}

	@Override
	public void doPost(final HttpServletRequest req,
			final HttpServletResponse resp) throws IOException {
		Logger.getAnonymousLogger().info("Invoking doPost.");

		final BufferedReader reader = req.getReader();

		String line = reader.readLine();
		Logger.getAnonymousLogger().info("JSON Notification: " + line);
		try {

			final JSONObject change = new JSONObject(line);
			if (!"user".equals(change.getString("object"))) {
				return;
			}

			final JSONArray entries = change.getJSONArray("entry");

			for (int e = 0; e < entries.length(); ++e) {
				final JSONObject entry = entries.getJSONObject(e);

				String facebookUserId = entry.getString("uid");
				String facebookTime = entry.getString("time");

				String token = udb.getTokenbyID(facebookUserId);
				Logger.getAnonymousLogger().info("Token is " + token);

				final JSONObject facebookUserInfo = new JSONObject(getUserInfo(facebookUserId, token));
				Logger.getAnonymousLogger().info("Userinfo JSON is " + facebookUserInfo);

				final JSONArray facebookStatusArray = new JSONArray(getStatus(token));
				Logger.getAnonymousLogger().info("Statusupdate JSON is " + facebookStatusArray);

				Set<FacebookStatusFeedEvent> events = createEventModels(facebookTime, facebookUserId, facebookUserInfo, facebookStatusArray);

				for (FacebookStatusFeedEvent event : events) {
					Document payload = XMLHelper
							.createDocumentFromString(EventHelpers.serialize(event));

					eventSender.notify(payload);
				}

			}
		} catch (JSONException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE,
					"Error in doPost Notify: ", e);
		} catch (SAXException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE,
					"Error in doPost: ", e);
		} catch (ParseException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE,
					"Error in reading Facebook JSON data: ", e);
		}
	}

	/**
	 * Return PLAY event models for FacebookStatusFeedEvents. This method can return more than one event
	 * because a Facebook user might have created several status updates since the last Facebook
	 * Real-Time update which we received.
	 * 
	 * @param time Time stamp from Facebook
	 * @param uid User ID from Facebook
	 * @param facebookUserInfo JSON input from Facebook
	 * @param facebookStatusArray  JSON input from Facebook
	 * @return A set of event models for FacebookStatusFeedEvents
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static Set<FacebookStatusFeedEvent> createEventModels(String time, String uid, JSONObject facebookUserInfo, JSONArray facebookStatusArray) throws JSONException, ParseException {

		String name = "";
		String location= "";
		Set<FacebookStatusFeedEvent> results = new HashSet<FacebookStatusFeedEvent>();

		name = facebookUserInfo.getString("name");
		Logger.getAnonymousLogger().info("Name: " + name);

		try {
			String locat = facebookUserInfo.getString("location");
			JSONObject loc = new JSONObject(locat);
			location = loc.getString("name");
			Logger.getAnonymousLogger()
			.info("Location: " + location);
		} catch (JSONException e1) {
			Logger.getAnonymousLogger()
			.info("Location: no location was specified for this Facebook user.");
		}

		for (int i = 0; i < facebookStatusArray.length(); ++i) {
			final JSONObject statusUpdate = facebookStatusArray.getJSONObject(i);
			String statusUpdateTime = statusUpdate.getString("time");
			if (time.equals(statusUpdateTime)) {
				String epochString = statusUpdateTime;
				long epoch = Long.parseLong(epochString);

				Date messageTime = new Date(epoch * 1000);
				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd:HH:mm:ss.SSS");
				String datetime = df.format(messageTime);
				Date date = df.parse(datetime);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);

				String messageStatus = statusUpdate.getString("message");

				Logger.getAnonymousLogger().info(
						"Message: '" + messageStatus
						+ "' is sent from Facebook User: '"
						+ name + "' in location: '" + location
						+ "' at the TIME: '" + datetime + "'.");
				/*
				 * Create event
				 */
				// Create an event ID used in RDF context and RDF subject
				String eventId = EventHelpers.createRandomEventId("facebook");

				FacebookStatusFeedEvent event = new FacebookStatusFeedEvent(
						// set the RDF context part
						EventHelpers.createEmptyModel(eventId),
						// set the RDF subject
						eventId + "#event",
						// automatically write the rdf:type statement
						true);
				event.setFacebookName(name);
				event.setStatus(messageStatus);
				event.setFacebookLocation(location);
				event.setEndTime(cal);
				event.setFacebookLink(new URIImpl("http://graph.facebook.com/" + uid + "#"));
				event.setFacebookId(uid);
				event.setStream(new URIImpl(Stream.FacebookStatusFeed.getUri()));
				event.setSource(new URIImpl(Source.FacebookAdapter.toString()));

				results.add(event);
			}
		}
		return results;

	}

	private static String getUserInfo(String ID, String TOKEN) {
		String sub = null;
		try {
			URL url = new URL("https://graph.facebook.com/" + ID
					+ "?fields=name,location&access_token=" + TOKEN);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String check = reader.readLine();
			sub = check.substring(0);

		} catch (MalformedURLException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "Exception while retrieveing Facebook user info: ", e);
		} catch (IOException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "Exception while retrieveing Facebook user info: ", e);
		}

		return sub;
	}

	private static String getStatus(String TOKEN) {
		String sub = null;
		try {
			URL url = new URL(
					"https://api.facebook.com/method/status.get?access_token="
							+ TOKEN + "&format=json");

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String check = reader.readLine();
			sub = check.substring(0);

		} catch (MalformedURLException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "Exception while retrieveing Facebook status: ", e);
		} catch (IOException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "Exception while retrieveing Facebook status: ", e);
		}

		return sub;
	}

}
