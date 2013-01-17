package eu.play_project.play_eventadapter_facebook;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;


@SuppressWarnings("serial")
public class FacebookAuthServlet extends HttpServlet  {
	public static String token;
	public static UserDatabase db = new UserDatabase();
	String ID = null;
	
	@Override
	public void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws IOException {
		Logger.getAnonymousLogger().info("Invoking doGet.");

		String code = req.getParameter("code");
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		String authURL = FacebookUtil.getAuthURL(code);
		out.println("THANK YOU, YOU HAVE SUBSCRIBED!! \n ");

		token = getAccesstoken(authURL);
		try {
			JSONObject ob = new JSONObject(getIDbyToken(token));
			ID = ob.getString("id");
		} catch (JSONException e) {
			Logger.getAnonymousLogger().warning(e.getMessage());
		}
			
		Boolean check = db.checkID(ID);		
		if(check == true){
			db.updateDB(ID, token);
			out.println("You had aready been subscribed before, your new Token is '" + token + "' and your ID '" + ID
					+ "' has been stored to the database");
		}
		else{
		db.insertDB(ID, token);	
		
		out.println("YOUR ACCESS_TOKEN is: " + token + " and your ID '" + ID
				+ "' has been stored to the database");
		}
		Logger.getAnonymousLogger().info(
				"YOUR ACCESS_TOKEN is: " + token + " and your ID '" + ID
						+ "' has been stored to the database");
	}

	public static String getAccesstoken(String uri) {
		String token = null;
		try {
			URL url = new URL(uri);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String line = reader.readLine();
			token = line.substring(13);
		} catch (MalformedURLException e) {
			Logger.getAnonymousLogger().warning(e.getMessage());
		} catch (IOException e) {
			Logger.getAnonymousLogger().warning(e.getMessage());
		}

		return token;

	}
	
	
	public static String getIDbyToken(String TOKEN) {
		String sub = null;
		try {
			URL url = new URL(
					"https://graph.facebook.com/me?fields=id&access_token="
							+ TOKEN);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String check = reader.readLine();
			sub = check.substring(0);

		} catch (MalformedURLException e) {
			Logger.getAnonymousLogger().warning(e.getMessage());
		} catch (IOException e) {
			Logger.getAnonymousLogger().warning(e.getMessage());
		}

		return sub;
	}	

}
	
	
	
	
	
	
	
	