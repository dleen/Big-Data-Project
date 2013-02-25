name := "Big Data Project"

version := "1.0"

scalaVersion := "2.10.0"

scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies  ++= Seq(
            )

resolvers ++= Seq(
            // other resolvers here
            // if you want to use snapshot builds (currently 0.2-SNAPSHOT), use this.
            "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
            "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
            "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
            "Sonatype tools" at "https://oss.sonatype.org/content/groups/scala-tools/"
            )

mainClass in (Compile, run) := Some("main.scala.Hogwild.BigDataProcessor")
//mainClass in (Compile, run) := Some("Main")

javaOptions += "-Xmx2048M"
