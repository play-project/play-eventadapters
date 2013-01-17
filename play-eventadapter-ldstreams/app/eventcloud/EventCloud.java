package eventcloud;

import java.io.IOException;

import org.objectweb.proactive.core.config.CentralPAPropertyRepository;

import play.Logger;
import eu.play_project.play_commons.constants.Constants;
import fr.inria.eventcloud.EventCloudsRegistry;
import fr.inria.eventcloud.EventCloudsRegistryImpl;
import fr.inria.eventcloud.factories.EventCloudsRegistryFactory;


public class EventCloud {
	static {
		CentralPAPropertyRepository.JAVA_SECURITY_POLICY
				.setValue("proactive.java.policy");

		CentralPAPropertyRepository.GCM_PROVIDER
				.setValue("org.objectweb.proactive.core.component.Fractive");

		CentralPAPropertyRepository.PA_COMMUNICATION_PROTOCOL.setValue("pnp");
		CentralPAPropertyRepository.PA_RMI_PORT.setValue(1100);
		
		// Useless, needs to be set on command line
		System.setProperty("proactive.pnp.port", "9006");
		System.setProperty("proactive.http.port", "9007");
		
		System.setProperty("java.security.policy", "proactive.java.policy");
	}
	
	private static EventCloud singleton;
	private final static String registryName = Constants.getProperties().getProperty("eventcloud.registry");
	
	//private final static String eventCloudRegistry = Constants.getProperties().getProperty("eventcloud.unstable.registry");
	
	private EventCloudsRegistry registry;
	
	private EventCloud() {
		try {
			//registry = EventCloudsRegistryImpl.lookup(registryName);
			registry = EventCloudsRegistryFactory.lookupEventCloudsRegistry(registryName);
			Logger.info("Connected to event cloud registry: "+ registryName);
		} catch (IOException e) {
			Logger.error("Problem with connecting eventcloudsregistry");	
		}
	}
	
	/**
	 * Return singleton event cloud with its registry.
	 * @return
	 */
	public static EventCloud getInstance() {
		if (singleton == null) {
			singleton = new EventCloud();
		}
		return singleton;
	}
	
	public static String getNameofRegistry(){
		return registryName;
	}
	
	/**
	 * Return the registry with registry name, could be null if exception comes 
	 * out, when connecting to event cloud registry.
	 * @return
	 */
	public EventCloudsRegistry getRegistry(){
		return registry;
	}
}
