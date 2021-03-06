device.screen.on("unlock", function(){

var service = 'http://46.105.181.221:8084/petals/services/NotificationConsumerPortService';
//var service = 'http://requestb.in/1cmgfeg1';
var actionShort = 'Notify';
var actionLong = 'http://docs.oasis-open.org/wsn/br-2/Notify';

var event = '<?xml version="1.0" encoding="UTF-8"?><soap-env:Envelope xmlns:soap-env="http://schemas.xmlsoap.org/soap/envelope/"                   xmlns:xsd="http://www.w3.org/1999/XMLSchema"                   xmlns:xsi="http://www.w3.org/1999/XMLSchema-instance">   <soap-env:Body>      <wsnt:Notify xmlns:wsnt="http://docs.oasis-open.org/wsn/b-2">         <wsnt:NotificationMessage>            <wsnt:SubscriptionReference>               <wsa:Address xmlns:wsa="http://www.w3.org/2005/08/addressing">http://localhost:9998/foo/Endpoint</wsa:Address>               <wsa:ReferenceParameters xmlns:wsa="http://www.w3.org/2005/08/addressing"/>            </wsnt:SubscriptionReference>            <wsnt:Topic xmlns:s="http://streams.event-processing.org/ids/"                        Dialect="http://docs.oasis-open.org/wsn/t-1/TopicExpression/Concrete">s:situationalEvent</wsnt:Topic>            <wsnt:ProducerReference>               <wsa:Address xmlns:wsa="http://www.w3.org/2005/08/addressing">http://localhost:9998/foo/AbstractSender</wsa:Address>               <wsa:ReferenceParameters xmlns:wsa="http://www.w3.org/2005/08/addressing"/>            </wsnt:ProducerReference>            <wsnt:Message>               <mt:nativeMessage xmlns:mt="http://www.event-processing.org/wsn/msgtype/"                                 mt:syntax="application/x-trig">@prefix sioc: &lt;http://rdfs.org/sioc/ns#&gt; . @prefix : &lt;http://events.event-processing.org/types/&gt; . @prefix rdfs: &lt;http://www.w3.org/2000/01/rdf-schema#&gt; . @prefix uctelco: &lt;http://events.event-processing.org/uc/telco/&gt; . @prefix geo: &lt;http://www.w3.org/2003/01/geo/wgs84_pos#&gt; . @prefix e: &lt;http://events.event-processing.org/ids/&gt; . @prefix s: &lt;http://streams.event-processing.org/ids/&gt; . @prefix uccrisis: &lt;http://www.mines-albi.fr/nuclearcrisisevent/&gt; . @prefix xsd: &lt;http://www.w3.org/2001/XMLSchema#&gt; . @prefix rdf: &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#&gt; . @prefix src: &lt;http://sources.event-processing.org/ids/&gt; . @prefix user: &lt;http://graph.facebook.com/schema/user#&gt; . {} e:crisis1707676269016824830  {     &lt;http://events.event-processing.org/ids/crisis1707676269016824830#event&gt;            uccrisis:frequency  "1000" ;            uccrisis:componentSeid                    "someSEID" ;            uccrisis:unit  "MHz" ;            rdf:type  uccrisis:MeasureEvent ;            :endTime  "2012-09-11T22:01:44.968Z"^^&lt;http://www.w3.org/2001/XMLSchema#dateTime&gt; ;            uccrisis:uid  "http://events.event-processing.org/ids/crisis1707676269016824830" ;            uccrisis:situation  "Sit-01" ;            :stream  &lt;http://www.mines-albi.fr/nuclearcrisisevent/situationalEvent#stream&gt; ;            uccrisis:value  "123" ;            uccrisis:componentName                    "Component-101" ;            uccrisis:localisation                    "somewhere" .    }</mt:nativeMessage>            </wsnt:Message>         </wsnt:NotificationMessage>      </wsnt:Notify>  </soap-env:Body></soap-env:Envelope>';

device.ajax({
	  url: service,
	  type: 'POST',
	  data: event,
	  headers: {
		  'Content-Type': 'text/xml; charset=utf-8',
		  'SOAPAction': actionShort
	  }
	},
	function onSuccess(body, textStatus, response){
		//device.notifications.createNotification(textStatus).show();
		console.info(textStatus);
	},
	function onError(textStatus, response){
		//device.notifications.createNotification(textStatus).show();
		console.info(textStatus);
	});
});
