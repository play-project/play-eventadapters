package eu.play_project.play_eventadapter;

class Subscription {
	private String resource;
	private String subscriber;
	private String subscription_id;
	private String resource_rul;
	
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public String getSubscriber() {
		return subscriber;
	}
	public void setSubscriber(String subscriber) {
		this.subscriber = subscriber;
	}
	public String getSubscription_id() {
		return subscription_id;
	}
	public void setSubscription_id(String subscription_id) {
		this.subscription_id = subscription_id;
	}
	public String getResource_rul() {
		return resource_rul;
	}
	public void setResource_rul(String resource_rul) {
		this.resource_rul = resource_rul;
	}
}
