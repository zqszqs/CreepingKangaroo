name := "com.stubhub.qe.platform.core"

organization := "com.stubhub.qe.platform"

version := "1.0.0-SNAPSHOT"

//resolvers += "StubHub Repository" at "https://mvnrepository.stubcorp.dev/nexus/content/groups/stubhub-public/"

crossPaths := false

libraryDependencies ++= Seq(
    "org.seleniumhq.selenium" % "selenium-java" % "2.42.2",
    "org.testng" % "testng" % "6.8"
)

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))