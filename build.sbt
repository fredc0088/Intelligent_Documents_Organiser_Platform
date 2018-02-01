

// val pattern = "^(2.12)*".r

// def check(x: String) = x match {
//   case pattern => "org.scalacheck" % "scalacheck" % "1.13.4" % "test"
//   case _ => "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
// }

// resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"

// def scalactic(x: String) = x match {
//   case pattern => "org.scalactic" % "scalactic" % "3.0.4"
//   case _ => "org.scalactic" %% "scalactic" % "3.0.4"
// }

// def testlib(x: String) = x match {
//   case pattern => "org.scalatest" % "scalatest" % "3.0.4" % "test"
//   case _ => "org.scalatest" %% "scalatest" % "3.0.4" % "test"
// }

val check = "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"

val scalactic = "org.scalactic" %% "scalactic" % "3.0.4"

val testlib = "org.scalatest" %% "scalatest" % "3.0.4" % "test"

lazy val root = (project in file("."))
  .settings(
    name := "Intelligent documents classicator platform",
    version := "0.0.1",
    scalaVersion := "2.12.3",
    organization := name.value,
    scalacOptions := List("-encoding", "utf8", "-Xfatal-warnings", "-deprecation", "-unchecked", "-feature"),
    //libraryDependencies ++= Seq(check(scalaVersion.value), scalactic(scalaVersion.value), testlib(scalaVersion.value))
    libraryDependencies ++= Seq(check, scalactic, testlib)
  )
