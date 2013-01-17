package eu.play_project.play_eventadapter_facebook;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FacebookUtil {
	
	private static Properties properties = new Properties();
	 
	static {
		try {
			properties.load(FacebookUtil.class.getClassLoader().getResourceAsStream("facebook.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    private static final String API_KEY = properties.getProperty("facebook.apiKey");
    private static final String SECRET = properties.getProperty("facebook.secret");
    private static final String APPLICATION_ID = properties.getProperty("facebook.applicationId");
    private static final String VERIFY_TOKEN = properties.getProperty("facebook.verifyToken");
    
    private static final String URL = properties.getProperty("facebook.applicationUrl");

    
    private static final String FB_GRAPH_URL = "https://graph.facebook.com/";
    private static final String FB_OAUTH_URL = FB_GRAPH_URL + "oauth/";
    private static final String FB_FRIENDS_URL = FB_GRAPH_URL + "me/friends?";

    
    private static final String REDIRECT_URL = URL + "facebookAuth";
    private static final String CALLBACK_URL = URL + "facebookRealtime";

    public static String getApplicationId() {
        return APPLICATION_ID;
    }

    public static String getAPIKey() {
        return API_KEY;
    }

    public static String getSecret() {
        return SECRET;
    }
    public static String getOauthURL() {
        return FB_OAUTH_URL;
    }
    public static String getCallbackURL() {
        return CALLBACK_URL;
    }
    public static String getVerifyToken() {
        return VERIFY_TOKEN;
    }


    public static String getFriendsUrl(final String authToken) {
        return FB_FRIENDS_URL + authToken;
    }
    
	// set this to the list of extended permissions you want
	private static final String perms = "publish_stream,email,user_status,offline_access,user_location";


	public static String getLoginRedirectURL() {
		return "https://graph.facebook.com/oauth/authorize?client_id="
				+ APPLICATION_ID + "&display=page&redirect_uri=" + REDIRECT_URL
				+ "&scope=" + perms;
	}

	public static String getAuthURL(String authCode) {
		return "https://graph.facebook.com/oauth/access_token?client_id="
				+ APPLICATION_ID + "&redirect_uri=" + REDIRECT_URL
				+ "&client_secret=" + SECRET + "&code=" + authCode;
	}
	
	public static String getAppToken(String uri){		
		 String token = null;	
		try {
           URL url = new URL(uri);
           BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
           String line = reader.readLine();
           token = line.substring(13);
		 } catch (MalformedURLException e) {
				Logger.getAnonymousLogger().warning(e.getMessage());
	        } catch (IOException e) {
				Logger.getAnonymousLogger().warning(e.getMessage());
	        }

			return token;
		
	}
	
	public static String getAppToken(String FACEBOOK_API_KEY, String FACEBOOK_APPLICATION_SECRET){		
		 String token = null;	
		try {
          URL url = new URL(FB_OAUTH_URL + "access_token?client_id=" + FACEBOOK_API_KEY + "&client_secret=" + FACEBOOK_APPLICATION_SECRET + "&grant_type=client_credentials");
          BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
          token = reader.readLine();
          
		} catch (MalformedURLException e) {
			Logger.getAnonymousLogger().warning(e.getMessage());
		} catch (IOException e) {
			Logger.getAnonymousLogger().warning(e.getMessage());
		}

		return token;

	}
	
	public static String checkSubcribe(String FACEBOOK_API_KEY, String FACEBOOK_TOKEN) {
		String sub = null;
		try {
			URL url = new URL("https://graph.facebook.com/" + FACEBOOK_API_KEY + "/subscriptions?access_token=" + FACEBOOK_TOKEN);

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
	public void subcribe(String FACEBOOK_TOKEN){
	   
		try {

//	    String token = client.auth_createToken();
//      resp.getWriter().println(token);
	 	String data = URLEncoder.encode("object", "UTF-8") + "=" + URLEncoder.encode("user", "UTF-8");
		data += "&" + URLEncoder.encode("fields", "UTF-8") + "=" + URLEncoder.encode("feed", "UTF-8");
		data += "&" + URLEncoder.encode("callback_url", "UTF-8") + "="	+ URLEncoder.encode(CALLBACK_URL, "UTF-8");
		data += "&" + URLEncoder.encode("verify_token", "UTF-8") + "="	+ URLEncoder.encode(VERIFY_TOKEN, "UTF-8");
		data += "&" + URLEncoder.encode("access_token", "UTF-8") + "="	+ URLEncoder.encode(FACEBOOK_TOKEN,"UTF-8");  

			URL urlnew = new URL("https://graph.facebook.com/" + API_KEY
					+ "/subscriptions");
			HttpURLConnection connection = (HttpURLConnection) urlnew
					.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");

			OutputStreamWriter writer = new OutputStreamWriter(
					connection.getOutputStream());
			writer.write(data);
			writer.close();
			
			//for testing the subscribe result
			//String sub = FB_OAUTH_URL + "access_token?client_id=" + API_KEY
			//		+ "&client_secret=" + SECRET
			//		+ "&grant_type=client_credentials";
			// resp.getWriter().println(String.format("Hast just subcribe following Data: '%s'",
			// data));
			// Runtime.getRuntime().exec("explorer \"" + sub + "\"");

			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// OK
			} else {
				Logger.getAnonymousLogger().warning("Server returned HTTP error code: " + connection.getResponseCode());
			}
		} catch (IOException e) {
			Logger.getAnonymousLogger().warning(e.getMessage());
		}
	}
	public void deleteSubcribe(String FACEBOOK_API_KEY){    
		URL urlnew;
		try {

			urlnew = new URL("https://graph.facebook.com/" + FACEBOOK_API_KEY
					+ "/subscriptions");
			HttpURLConnection connection = (HttpURLConnection) urlnew
					.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("DELETE");
			OutputStreamWriter writer = new OutputStreamWriter(
					connection.getOutputStream());
			writer.write("");
			writer.close();
		} catch (MalformedURLException e) {
			Logger.getAnonymousLogger().warning(e.getMessage());
		} catch (IOException e) {
			Logger.getAnonymousLogger().warning(e.getMessage());
		}		
	}
	public void modifySubcribe(HttpServletRequest req, HttpServletResponse resp){
		
	}

}