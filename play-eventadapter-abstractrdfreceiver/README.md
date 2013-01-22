Receiving Events from PLAY
==========================
To receive events from PLAY this is needed:

1. listen on a HTTP endpoint for events
2. subscribe that endpoint address for one or more streams of PLAY events
3. unwrap event from its WS-Notification SOAP message
4. unwrap event from its XML `<mt:nativeMessage>` element
5. unescape the event from XML
6. parse the RDF

... details to be added.

... a generic approach for mobile phones is still needed (they don't expose an HTTP endpoint)