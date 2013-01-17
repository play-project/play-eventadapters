//device.screen.on("unlock", function(){
device.modeOfTransport.on('changed', function(signal){

var service = 'http://46.105.181.221:8084/petals/services/NotificationConsumerPortService';
//var service = 'http://requestb.in/1cmgfeg1';
var actionShort = 'Notify';
var uniqueId = 'esr1340688541673999872';
var topicLocalPart = 'TaxiUCGeoLocation';
var myPhoneNumber = '491799041747';
var myMail = 'roland.stuehmer@fzi.de';

var event = '<?xml version="1.0" encoding="UTF-8"?><soap-env:Envelope xmlns:soap-env="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/1999/XMLSchema" xmlns:xsi="http://www.w3.org/1999/XMLSchema-instance"> <soap-env:Body> <wsnt:Notify xmlns:wsnt="http://docs.oasis-open.org/wsn/b-2"> <wsnt:NotificationMessage> <wsnt:SubscriptionReference> <wsa:Address xmlns:wsa="http://www.w3.org/2005/08/addressing">http://localhost:9998/foo/Endpoint</wsa:Address> <wsa:ReferenceParameters xmlns:wsa="http://www.w3.org/2005/08/addressing"/> </wsnt:SubscriptionReference> <wsnt:Topic xmlns:s="http://streams.event-processing.org/ids/" Dialect="http://docs.oasis-open.org/wsn/t-1/TopicExpression/Concrete">s:' + topicLocalPart + '</wsnt:Topic><wsnt:ProducerReference><wsa:Address xmlns:wsa="http://www.w3.org/2005/08/addressing">http://localhost:9998/foo/AbstractSender</wsa:Address> <wsa:ReferenceParameters xmlns:wsa="http://www.w3.org/2005/08/addressing"/></wsnt:ProducerReference><wsnt:Message>';
event += '<mt:nativeMessage xmlns:mt="http://www.event-processing.org/wsn/msgtype/" mt:syntax="application/x-trig">@prefix sioc: &lt;http://rdfs.org/sioc/ns#&gt; . @prefix : &lt;http://events.event-processing.org/types/&gt; . @prefix uctelco: &lt;http://events.event-processing.org/uc/telco/&gt; . @prefix geo: &lt;http://www.w3.org/2003/01/geo/wgs84_pos#&gt; . @prefix e: &lt;http://events.event-processing.org/ids/&gt; . @prefix xsd: &lt;http://www.w3.org/2001/XMLSchema#&gt; .     @prefix rdf: &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#&gt; . @prefix user: &lt;http://graph.facebook.com/schema/user#&gt; . ';
event += '{} ';
	
event += 'e:'+uniqueId+'  {';
event += '	&lt;blank://0&gt;  geo:long  &quot;'+device.location.lastLocation.location.longitude+'&quot;^^&lt;http://www.w3.org/2001/XMLSchema#double&gt; ; ';
event += '			geo:lat  &quot;'+device.location.lastLocation.location.latitude+'&quot;^^&lt;http://www.w3.org/2001/XMLSchema#double&gt; ; ';
event += '			rdf:type  geo:Point . ';

event += '	&lt;http://events.event-processing.org/ids/'+uniqueId+'#event&gt; ';
event += '			uctelco:uniqueId  &quot;'+uniqueId+'&quot; ; ';
event += '			:source  &lt;http://sources.event-processing.org/ids/OnX#source&gt; ; ';
event += '			uctelco:phoneNumber &quot;'+myPhoneNumber+'&quot; ; ';
event += '			uctelco:modeOfTransport &quot;'+device.modeOfTransport.current+'&quot; ; ';
event += '			uctelco:mailAddress &lt;mailto:'+myMail+'&gt; ; ';
event += '			:location  &lt;blank://0&gt; ; ';
event += '			:stream  &lt;http://streams.event-processing.org/ids/'+topicLocalPart+'#stream&gt; ; ';
event += '			:endTime  &quot;2012-12-07T14:16:01.862Z&quot;^^&lt;http://www.w3.org/2001/XMLSchema#dateTime&gt; ; ';
event += '			rdf:type  :UcTelcoGeoLocation . ';
event += '} ';
event += '</mt:nativeMessage></wsnt:Message> </wsnt:NotificationMessage></wsnt:Notify></soap-env:Body></soap-env:Envelope>';

//device.notifications.createNotification("New MOT: " + device.modeOfTransport.current).show();
console.info(event);

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
