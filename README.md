    ...........................................................................
    ........:$$$7..............................................................
    .....==7$$$I~~...........MMMMMMMMM....DMM..........MM,........MM7......MM..
    ...,?+Z$$$?=~,,:.........MMM,,,?MMM+..MMM.........,MMMM,......7MM,....MMM..
    ..:+?$ZZZ$+==:,:~........MMM.....MMM..MMM.........,MMDMMM:.....,MMI..MMM...
    ..++7ZZZZ?+++====,.......MMM....~MMM..MMM.........,MM??DMMM:....?MM,MMM....
    ..?+OZZZ7~~~~OOI=:.......MMMMMMMMMM...MMM.........,MM?II?MMM~....DMMMM.....
    ..+7OOOZ?+==+7Z$Z:.......MMM$$$I,.....MMM.........,MM??8MMM~......NMM......
    ..:OOOOO==~~~+OZ+........MMM..........MMM.........,MMDMMM~........NMM......
    ..,8OOOO+===+$$?,........MMM..........MMM.,,,,,...,MMMM:..........NMM......
    ,,+8OOOZIIIIII=,,,,,,,,,,MMM,,,,,,,,,,NMMMMMMMMM=,,MM:,,,,........8MM......
    ,,,:O8OO~+~:,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
    ,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
    ,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
                                                      ASCII Art: GlassGiant.com

PLAY Event Adapters
===================
This repository contains a collection of various event adapters developed to produce
events in RDF format for project PLAY.

Most adapters are Tomcat servlets.

Configure
---------
To send events to PLAY using the RESTful event adapters a PLAY API Token is needed:

* You can configure it by editing the properties file on the classpath, usually `/src/main/resources/play-eventadapter.properties`
* Alternatively Maven will insert your API Token during building if you set the token in your `$HOME/.m2/settings.xml` `<properties>` as follows:

```xml
	<settings>
		...
		<profiles>
		<profile>
			<id>default-profile</id>
			<activation>
				<activeByDefault>TRUE</activeByDefault>
			</activation>
			<properties>
				<play.platform.api.token>xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</play.platform.api.token>
			</properties>
		</profiles>
		...
	</settings>
```

SDK
---
To send or receive your own events, see the subprojects *play-eventadapter-abstractrdf...*


Issues
------
For issues and bug reporting, please go to https://github.com/play-project/play/issues?labels=&page=1&state=open

Other
-----
[Reports](http://play-project.github.com/play-eventadapters/site/1.0-SNAPSHOT/project-reports.html) |
[JavaDoc](http://play-project.github.com/play-eventadapters/site/1.0-SNAPSHOT/apidocs/index.html) |
[DOAP](http://play-project.github.com/play-eventadapters/site/1.0-SNAPSHOT/doap.rdf) |
[License](LICENSE.txt)
