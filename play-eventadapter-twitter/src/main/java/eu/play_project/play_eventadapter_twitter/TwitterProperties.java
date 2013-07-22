package eu.play_project.play_eventadapter_twitter;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TwitterProperties {


static Properties properties;
	
	public TwitterProperties() throws IOException
	{
		properties = new Properties();
		properties.load(this.getClass().getClassLoader().getResourceAsStream("twitter.properties"));
	}
	
	public TwitterProperties(String string) throws IOException {
		properties = new Properties();
		BufferedInputStream stream = new BufferedInputStream(new FileInputStream(string));
		properties.load(stream);
		
		stream.close();
	}

	// Get Properties
	
	public static String getTwitter1ConsumerKey()
	{
		return properties.getProperty("twitter1.consumerKey");
	}
	public static String getTwitter1ComsumerSecret()
	{
		return properties.getProperty("twitter1.comsumerSecret");
	}
	public static String getTwitter1AccessToken()
	{
		return properties.getProperty("twitter1.accessToken");
	}
	public static String getTwitter1AccessTokenSecret()
	{
		return properties.getProperty("twitter1.accessTokenSecret");
	}

	
	public static boolean hasLocations()
	{
		if (properties.getProperty("LOCATIONS") != null && !properties.getProperty("LOCATIONS").equals("")) {
			return true;
		}
		else {
			return false;
		}
	}
	public static boolean hasKeywords()
	{
		if (properties.getProperty("KEYWORDS").equals("")) return false;
		else return true;
	}
	public static double[][] getLocations()
	{
		if (hasLocations())
		{
			  String[] a = properties.getProperty("LOCATIONS").split(";");
			  double[][] res = new double[a.length][2];
			  
			  for(int i = 0; i < a.length; i++)
			  {
				  String[] intm = a[i].split(",");
				  for(int j = 0; j < intm.length; j++)
				  {
					  res[i][j] = Double.parseDouble(intm[j]);
				  }
			  }

		  return res;
		}
		else return new double[0][0];
	}
	
	public static String[] getKeywords()
	{
		return properties.getProperty("KEYWORDS").split(",");
	}
	
	
}
