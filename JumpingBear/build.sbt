name := "JumpingBear"

version := "1.0-SNAPSHOT"

resolvers += "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.reactivemongo" %% "reactivemongo" % "0.11.0-SNAPSHOT" exclude("org.scala-stm", "scala-stm_2.10.0"),
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.0-SNAPSHOT" exclude("org.scala-stm", "scala-stm_2.10.0") exclude("play", "play_2.10"),
  "org.json4s" %% "json4s-native" % "3.2.6",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.0"
)

play.Project.playScalaSettings
