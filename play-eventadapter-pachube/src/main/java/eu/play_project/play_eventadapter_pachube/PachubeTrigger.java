package eu.play_project.play_eventadapter_pachube;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pachube.api.APIVersion;
import com.pachube.api.Pachube;
import com.pachube.api.Trigger;
import com.pachube.api.TriggerType;
import com.pachube.commons.impl.TriggerImpl;
import com.pachube.exceptions.PachubeException;
import com.pachube.factory.PachubeFactory;



/**
 * This class subscribes to a list of Pachube feeds. This works by registering
 * our servlet URI with Pachube to recieve WebHooks callbacks whenever Pachube
 * has new events.
 * 
 * The job of this class is routinely done by the servlet itself. This class is
 * merely a standlone tool to manually add subscriptions while the servlet runs.
 * 
 * @author Atanas Kozarev, FZI
 * @author Roland Stühmer, FZI
 * 
 */
public class PachubeTrigger {

	public static void main(String arsg[]) throws MalformedURLException {
		
		Logger logger = Logger.getAnonymousLogger();
		
        PachubeFactory pachubeFactory = new PachubeFactory();
        
        URL pachubeAdapterTriggerUrl = new URL(PachubeServlet.pachubeAdapterTriggerUrl);
        
		Properties properties = new Properties();
		try {
			properties.load(PachubeTrigger.class.getClassLoader().getResourceAsStream("pachube.properties"));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Cannot load Pachube adapter properties file. ", e);
			System.exit(1);
		}

		
		Pachube p = pachubeFactory.createPachube(
				properties.getProperty("pachube.v2.apiKey"), APIVersion.V2);

		try {
			for (Trigger t : Arrays.asList(p.getTriggers())) {
				if (pachubeAdapterTriggerUrl.equals(t.getUrl())) {
					p.deleteTrigger(t.getID());
				}
			}
		} catch (PachubeException e) {
			logger.log(Level.WARNING, "Unsubscribing from old Pachube feeds failed. ", e);
		}
		
		/*
		 *  Loop through a list of feeds to subscribe to Pachube:
		 */
		for (int feedId : PachubeServlet.feedIds) {
			try {
				
				Trigger t = new TriggerImpl();
				t.setEnv_id(feedId);
				t.setStream_id(0);

				t.setType(TriggerType.CHANGE);

				t.setUrl(pachubeAdapterTriggerUrl);

				logger.info("Subscribing to Pachube at feed Id " + feedId);
				p.createTrigger(t);
			} catch (Exception e) {
				logger.log(Level.WARNING, "Subscribing to Pachube feed " + feedId + " failed. ", e);
			}
		}
	}
}
