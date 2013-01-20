Sending Events to PLAY
======================
When sending events to PLAY they must be:

1. in RDF format, TriG syntax (see [play-commons-eventformat](https://github.com/play-project/play-commons/tree/master/play-commons-eventformat) and [play-commons-eventtypes](https://github.com/play-project/play-commons/tree/master/play-commons-eventtypes))
2. escaped for XML
3. wrapped in an XML `<mt:nativeMessage>` element
4. wrapped in a WS-Notification SOAP message

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


Event wrapped in a WS-Notification SOAP message
-----------------------------------------------
### Example
```xml
<?xml version='1.0' encoding='UTF-8'?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
  <soapenv:Body>
        <wsnt:Notify xmlns:wsnt="http://docs.oasis-open.org/wsn/b-2">
            <wsnt:NotificationMessage>
                <wsnt:Topic xmlns:dsb="http://www.petalslink.org/dsb/topicsns/"
                    Dialect="http://www.w3.org/TR/1999/REC-xpath-19991116"
                >dsb:TaxiUCIMA</wsnt:Topic>
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
