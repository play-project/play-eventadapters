/**
 * 
 */
package eu.play_project.play_eventadapter.tests;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import eu.play_project.play_commons.constants.Constants;
import eu.play_project.play_commons.constants.Stream;
import eu.play_project.play_eventadapter.AbstractReceiverRest;


/**
 * @author Ningyuan
 *
 */
public class AbstractReceiverRestCmdLine {

	/**
	 * Manual test.
	 */
	public static void main(String[] args){
		
		AbstractReceiverRest receiver = new AbstractReceiverRest() {};
		InputStreamReader in = new InputStreamReader(new BufferedInputStream(System.in));
		BufferedReader bin = new BufferedReader(in);
		List<String> subIds = new ArrayList<String>();
		
		try{
			System.out.print("> ");
			String s = bin.readLine();
			
			while(!s.equalsIgnoreCase("exit")){
				if(s.equalsIgnoreCase("sub")){
					System.out.println("Service: " + receiver.getSubscribeEndpoint());
					System.out.println("Token: " + Constants.getProperties("play-eventadapter.properties").getProperty("play.platform.api.token"));
					String id = receiver.subscribe(Stream.PersonalStream1.getTopicUri(), "http://requestb.in/x3i1mlx3");
					if(id != null && !id.equals("")){
						subIds.add(id);
						System.out.println("subed: "+id);
					}
				}
				else if(s.equalsIgnoreCase("unsub")){
					for(String id : subIds){
						receiver.unsubscribe(id);
						System.out.println("unsubed: "+id);
					}
					subIds.clear();
				}
				System.out.print("> ");
				s = bin.readLine();
			}
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		finally{
			for(String id : subIds){
				receiver.unsubscribe(id);
			}
		}
		
	}

}
