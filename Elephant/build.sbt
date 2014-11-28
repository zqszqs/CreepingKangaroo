name := "michaelwork.elephant.core"

organization := "michaelwork.elephant"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.2"

resolvers += "StubHub Repository" at "https://mvnrepository.stubcorp.dev/nexus/content/groups/stubhub-public/"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

crossPaths := false

libraryDependencies ++= Seq(
    "org.seleniumhq.selenium" % "selenium-java" % "2.44.0",
    "org.testng" % "testng" % "6.8",
    "org.apache.httpcomponents" % "httpclient" % "4.3.5",
    "org.apache.commons" % "commons-csv" % "1.0",
    "com.typesafe.play" %% "play-json" % "2.3.0",
    "net.sourceforge.jexcelapi" % "jxl" % "2.6.12",
    "com.google.code.gson" % "gson" % "2.3",
    "io.appium" % "java-client" % "1.3.0"
)

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))
