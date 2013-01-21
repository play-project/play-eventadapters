Sending Events to PLAY
======================
When sending an event to PLAY the sender must...

1. write the event in RDF format, TriG syntax (see [play-commons-eventformat](https://github.com/play-project/play-commons/tree/master/play-commons-eventformat) and [play-commons-eventtypes](https://github.com/play-project/play-commons/tree/master/play-commons-eventtypes))
2. escape the event for XML
3. wrap it in an XML `<mt:nativeMessage>` element
4. wrap it in a WS-Notification SOAP message
5. send it to the HTTP endpoint of PLAY

Event in RDF format, TriG syntax
--------------------------------
### Example
```
@PREFIX :    <http://events.event-processing.org/types/> .
@PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> .

<http://events.event-processing.org/ids/e1> {
    <http://events.event-processing.org/ids/e1#event>
        a       :TempEvent ;
        :endTime "2011-08-24T14:40:59.837"^^xsd:dateTime ;
        :stream <http://streams.event-processing.org/ids/WeatherStream#stream> .
}
```

Event escaped for XML
---------------------
### Example
```xml
@PREFIX :    &lt;http://events.event-processing.org/types/&gt; .
@PREFIX xsd: &lt;http://www.w3.org/2001/XMLSchema#&gt; .

&lt;http://events.event-processing.org/ids/e1&gt; {
    &lt;http://events.event-processing.org/ids/e1#event&gt;
        a       :TempEvent ;
        :endTime &quot;2011-08-24T14:40:59.837&quot;^^xsd:dateTime ;
        :stream &lt;http://streams.event-processing.org/ids/WeatherStream#stream&gt; .
}
```

Event wrapped in an XML `<mt:nativeMessage>` element
----------------------------------------------------
### Example
```xml
<mt:nativeMessage xmlns:mt="http://www.event-processing.org/wsn/msgtype/" mt:syntax="application/x-trig">
@PREFIX :    &lt;http://events.event-processing.org/types/&gt; .
@PREFIX xsd: &lt;http://www.w3.org/2001/XMLSchema#&gt; .

&lt;http://events.event-processing.org/ids/e1&gt; {
    &lt;http://events.event-processing.org/ids/e1#event&gt;
        a       :TempEvent ;
        :endTime &quot;2011-08-24T14:40:59.837&quot;^^xsd:dateTime ;
        :stream &lt;http://streams.event-processing.org/ids/WeatherStream#stream&gt; .
}
</mt:nativeMessage>
```

* Java constants for the nativeMessage element can be accessed here: [code](https://github.com/play-project/play-commons/blob/master/play-commons-constants/src/main/java/eu/play_project/play_commons/constants/Event.java)
* complete Java methods for the wrapping can be accessed here: [code](https://github.com/play-project/play-commons/blob/master/play-commons-eventformat/src/main/java/eu/play_project/play_commons/eventformat/EventFormatHelpers.java)


Event wrapped in a WS-Notification SOAP message
-----------------------------------------------
### Example
```xml
<?xml version='1.0' encoding='UTF-8'?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
  <soapenv:Body>
        <wsnt:Notify xmlns:wsnt="http://docs.oasis-open.org/wsn/b-2">
            <wsnt:NotificationMessage>
                <wsnt:Topic xmlns:s="http://streams.event-processing.org/ids/"
                    Dialect="http://www.w3.org/TR/1999/REC-xpath-19991116"
                >s:WeatherStream</wsnt:Topic>
                <wsnt:Message>
                    <mt:nativeMessage xmlns:mt="http://www.event-processing.org/wsn/msgtype/"
                        mt:syntax="application/x-trig"
                    >
@PREFIX :    &lt;http://events.event-processing.org/types/&gt; .
@PREFIX xsd: &lt;http://www.w3.org/2001/XMLSchema#&gt; .

&lt;http://events.event-processing.org/ids/e1&gt; {
    &lt;http://events.event-processing.org/ids/e1#event&gt;
        a       :TempEvent ;
        :endTime &quot;2011-08-24T14:40:59.837&quot;^^xsd:dateTime ;
        :stream &lt;http://streams.event-processing.org/ids/WeatherStream#stream&gt; .
}
                    </mt:nativeMessage>
                </wsnt:Message>
            </wsnt:NotificationMessage>
        </wsnt:Notify>
    </soapenv:Body>
</soapenv:Envelope>
```

* the SOAP message contains a `wsnt:Notify` element (WS-Notification standard)
* `s:WeatherStream` (including its namespace declaration) is a duplication of the stream ID without the `#stream` suffix 

Sending the event to the HTTP endpoint of PLAY
----------------------------------------------
The SOAP WS-Notification message must be sent to the PLAY Distributed Service Bus via HTTP.

The HTTP endpoint should be used from Java property `dsb.notify.endpoint` from [play-commons-constants.properties](https://github.com/play-project/play-commons/blob/master/play-commons-constants/src/main/resources/play-commons-constants.properties)

You can use one of the following:

### PLAY Abstract RDF Sender
```
		<dependency>
			<groupId>org.ow2.play</groupId>
			<artifactId>play-eventadapter-abstractrdfsender</artifactId>
			<version>1.0-SNAPSHOT</version>
		</dependency>
```
Example usage: [code](play-eventadapter-abstractrdfsender/src/test/java/eu/play_project/play_eventadapter/tests/AbstractSenderTest.java)

### PLAY HTTP Client [Github](https://github.com/PetalsLinkLabs/petals-dsb/tree/master/modules/dsb-notification-commons)
```
		<dependency>
			<groupId>org.petalslink.dsb</groupId>
			<artifactId>dsb-notification-commons</artifactId>
			<version>1.0-SNAPSHOT</version>
		</dependency>
		<dependency>
			<groupId>org.petalslink.dsb</groupId>
			<artifactId>dsb-notification-httpclient</artifactId>
			<version>1.0-SNAPSHOT</version>
		</dependency> 
```
Example usage: [code](/play-eventadapter-abstractrdfsender/src/main/java/eu/play_project/play_eventadapter/AbstractSender.java)

### PLAY lightweight HTTP Client [Github](https://github.com/PetalsLinkLabs/petals-dsb/tree/master/modules/dsb-notification-lightweight)

For the use on resource-constrained devices this artefact has less dependencies.
```
		<dependency>
			<groupId>org.ow2.petals.dsb</groupId>
			<artifactId>dsb-notification-lightweight</artifactId>
		</dependency> 
```

### your own HTTP client
