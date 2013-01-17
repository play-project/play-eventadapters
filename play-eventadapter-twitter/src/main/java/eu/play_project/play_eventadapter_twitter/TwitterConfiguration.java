package eu.play_project.play_eventadapter_twitter;


public class TwitterConfiguration {

	private double[][] locations;
	private String[] keywords;
	private boolean sample;

	
	public void setLocationRestriction(double[][] locations)
	{
		this.locations = locations;
	}
	
	public void setKeywords(String[] keywords)
	{
		this.keywords = keywords;
	}
	
	public void setSample(boolean sample)
	{
		this.sample = sample;
	}
	public double[][] getLocations()
	{
		return locations;
	}
	public String[] getKeywords()
	{
		return keywords;
	}
	public boolean getSample()
	{
		return sample;
	}
}

