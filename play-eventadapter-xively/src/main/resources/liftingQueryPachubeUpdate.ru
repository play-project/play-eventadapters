PREFIX : <http://events.event-processing.org/types/>
PREFIX e: <http://events.event-processing.org/ids/>
PREFIX temp: <http://www.linkedopenservices.org/ns/temp-json#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX fn: <http://www.w3.org/2005/xpath-functions#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

DELETE  { ?subject temp:value ?object }
INSERT  { ?subject temp:value_description ?object }
WHERE {
	?subject temp:value ?object .
	FILTER isBlank(?object)
}
