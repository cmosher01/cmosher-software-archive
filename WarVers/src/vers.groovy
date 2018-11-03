public class vers {
	public static void main(final String... args) {
		def server = confFile()
		server.deployments.deployment.each {
			def name = ""+it.@name
			if (name.indexOf(".war") > 0) {
				def fil = ""+it.content.@sha1
				if (fil.length() > 2){
					println name+"\t"+"data/content/"+fil.substring(0,2)+"/"+fil.substring(2)+"/content"
				}
			}
		}
	}

	private static confFile(){
		return new XmlSlurper().parse("configuration/standalone-full.xml")
	}
}
