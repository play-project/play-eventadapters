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


/**
 * @author Ningyuan
 *
 */
public class RestReceiverTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args){
		
		DSBReceiverRest receiver = new DSBReceiverRest("http://app.event-processing.org:8080/play/api/v1/platform/subscriptions");
		InputStreamReader in = new InputStreamReader(new BufferedInputStream(System.in));
		BufferedReader bin = new BufferedReader(in);
		List<String> subIds = new ArrayList<String>();
		
		try{
			System.out.print("> ");
			String s = bin.readLine();
			
			while(!s.equalsIgnoreCase("exit")){
				if(s.equalsIgnoreCase("sub")){
					String id = receiver.subscribe("http://streams.event-processing.org/ids/PersonalStream1", "http://requestb.in/x3i1mlx3");
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
