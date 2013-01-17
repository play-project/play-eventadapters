package eu.play_project.play_eventadapter.tests;

import javax.xml.namespace.QName;

import eu.play_project.play_commons.constants.Stream;
import eu.play_project.play_eventadapter.AbstractSender;

public class SituationalEventSender extends AbstractSender {
	
	public SituationalEventSender() {
		// The topic can be set here, or changed at runtim using the setter or specified individually with each notify() 
		super(Stream.SituationalEventStream.getTopicQName());
	}
	
	public SituationalEventSender(QName defaultTopic) {
		super(defaultTopic);
	}
}
