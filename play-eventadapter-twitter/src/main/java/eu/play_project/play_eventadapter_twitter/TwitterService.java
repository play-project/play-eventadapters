package eu.play_project.play_eventadapter_twitter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.play_project.play_commons.constants.Stream;

/**
 * 
 * @author Roland Stühmer, FZI
 * 
 */
public class TwitterService extends HttpServlet implements ServletContextListener {

	private static final long serialVersionUID = 1L;
	private static TwitterBackend tb;
	
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("doGet()");

        String body = request.getParameter("body");

        System.out.println("body -> " + body);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	System.out.println("doPost()");

    }

	@Override
    public String getServletInfo() {
        return "Twitter event adapter for PLAY.";
    }

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		Logger.getAnonymousLogger().info("Initializing Twitter streams.");
		try {
			new TwitterProperties();
		} catch (IOException e) {
			Logger.getAnonymousLogger().warning("Could not read configuration file for Twitter adapter.");
		}
		TwitterConfiguration tc = new TwitterConfiguration();
		tc.setKeywords(TwitterProperties.getKeywords());
		tc.setLocationRestriction(TwitterProperties.getLocations());
		tb = new TwitterBackend();
		tb.startStream(tc,
				new TwitterPublisher(Stream.TwitterFeed.getTopicQName()));
	}

	@Override
	public void init() throws ServletException {
		super.init();
	}
	
	@Override
	public void destroy() {
		super.destroy();
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		Logger.getAnonymousLogger().info("Stopping Twitter streams.");
		tb.stopStreams();
	}

}
