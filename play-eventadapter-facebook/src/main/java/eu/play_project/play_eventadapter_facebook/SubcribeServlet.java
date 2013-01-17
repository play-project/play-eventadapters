package eu.play_project.play_eventadapter_facebook;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SubcribeServlet extends HttpServlet {
		
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Logger.getAnonymousLogger().info("Invoking doGet.");

		resp.setContentType("text/plain");
		resp.getWriter().println("REDIRECTING for subscribing and getting access token.....");
		resp.sendRedirect(FacebookUtil.getLoginRedirectURL());
	}
	

}
