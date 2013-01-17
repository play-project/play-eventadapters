package eu.play_project.play_eventadapter_facebook;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class CreateDBServlet extends HttpServlet {
	Logger logger;
 UserDatabase db = new UserDatabase();
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Logger.getAnonymousLogger().info("Invoking doGet.");

		resp.setContentType("text/plain");
		resp.getWriter().println("REDIREKTING to the Serlet FacebookAuthServlet.java for getting token.....");
		try {
			db.doStart();
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		db.selecDB(req, resp);
		
	}
	

}

