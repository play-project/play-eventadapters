Sending Events to PLAY
======================
When sending an event to PLAY the sender must...

1. instantiate a [PLAY event](https://github.com/play-project/play-commons/tree/master/play-commons-eventformat)
2. write the event in RDF format
3. wrap it in an XML `<mt:nativeMessage>` element
4. wrap it in a WS-Notification SOAP message
5. send it to the HTTP SOAP endpoint of PLAY

Configure
---------
1. Create a properties file `play-commons-constants.properties` on your classpath for endpoints where events can be received and sent. Adapt the file from the [defaults](https://github.com/play-project/play-commons/blob/master/play-commons-constants/src/main/resources/play-commons-constants-defaults.properties).

Use
---
1. Instatiate and send event ([example](https://github.com/play-project/play-eventadapters/blob/master/play-eventadapter-abstractrdfsender/src/test/java/eu/play_project/play_eventadapter/tests/AbstractSenderTest.java))

Misc
----
For a generic approach for *mobile phones* using Google Cloud Messaging GCM (they don't expose an HTTP endpoint) see this servlet: [play-mobile-sub](https://github.com/play-project/play-telco/tree/master/play-mobile-sub).
