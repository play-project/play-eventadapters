Receiving Events from PLAY
==========================
To receive events from PLAY this is needed:

1. listen on a HTTP endpoint for events (NOT part of the adapter)
2. subscribe that endpoint address for one or more streams of PLAY events
3. unwrap event from its WS-Notification SOAP message
4. unwrap event from its XML `<mt:nativeMessage>` element
5. unescape the event from XML
6. parse the RDF
7. do something with the event (NOT part of the adapter)

Configure
---------
1. Create a properties file `play-commons-constants.properties` on your classpath for endpoints where events can be received and sent. Adapt the file from the [defaults](https://github.com/play-project/play-commons/blob/master/play-commons-constants/src/main/resources/play-commons-constants-defaults.properties).

Use
---
1. Expose a SOAP endpoint ([example](https://github.com/play-project/play-eventadapters/blob/master/play-eventadapter-abstractrdfsender/src/test/java/eu/play_project/play_eventadapter/tests/SendAndReceiveTest.java))
2. Subscribe your endpoint (example see above)
3. Do something with the event

Misc
----
For a generic approach for *mobile phones* using Google Cloud Messaging GCM (they don't expose an HTTP endpoint) see this servlet: [play-mobile-sub](https://github.com/play-project/play-telco/tree/master/play-mobile-sub).
