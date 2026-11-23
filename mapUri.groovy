/*
 * Maps a URI to other URIs with the BridgeDb URI web service, which can be
 * started with start-uri-server.sh. See URI-WEBSERVICE.md.
 *
 * Usage: groovy mapUri.groovy [URI] [web service URL]
 * By default, it maps http://www.wikidata.org/entity/Q47512 (acetic acid)
 * with the web service at http://localhost:8183.
 */
import groovy.xml.XmlSlurper

def uri = args.length > 0 ? args[0] : "http://www.wikidata.org/entity/Q47512"
def service = args.length > 1 ? args[1] : "http://localhost:8183"

def request = URI.create("${service}/map?Uri=${URLEncoder.encode(uri, 'UTF-8')}").toURL()
def connection = request.openConnection()
connection.setRequestProperty("Accept", "application/xml")

def status
try {
    status = connection.responseCode
} catch (ConnectException exception) {
    System.err.println "Cannot reach the web service at ${service}: start it with ./start-uri-server.sh"
    System.exit(1)
}
switch (status) {
    case 200:
        break
    case 204:
        println "No mappings found for ${uri}"
        return
    default:
        System.err.println "The web service at ${service} answered with HTTP ${status}"
        System.exit(1)
}

def mappings = new XmlSlurper().parse(connection.inputStream)
def targets = new TreeSet<String>()
mappings.mapping.each { mapping ->
    mapping.targetUri.each { targets << it.text() }
}
targets.remove(uri)

if (targets.isEmpty()) {
    println "No mappings found for ${uri}"
    return
}
println "${uri} maps to ${targets.size()} URIs:"
targets.each { println "  ${it}" }
