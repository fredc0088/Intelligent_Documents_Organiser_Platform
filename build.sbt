import sbt.Keys.testFrameworks
import sbt.addCompilerPlugin

val check = "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"

val scalactic = "org.scalactic" %% "scalactic" % "3.0.5"

val testlib = "org.scalatest" %% "scalatest" % "3.0.5" % "test"

val mockTest = "org.scalamock" %% "scalamock" % "4.1.0" % "test"

val poi = "org.apache.poi" % "poi" % "3.17"

val poiDocX = "org.apache.poi" % "poi-ooxml" % "3.17"

val poiSchema = "org.apache.poi" % "ooxml-schemas" % "1.3"

val poiDoc = "org.apache.poi" % "poi-scratchpad" % "3.17"

val pdfbox = "org.apache.pdfbox" % "pdfbox" % "2.0.8"

val speedTest = "com.storm-enroute" %% "scalameter-core" % "0.9"

val scalaFX = "org.scalafx" %% "scalafx" % "8.0.144-R12"

val sclFXML = "org.scalafx" %% "scalafxml-core-sfx8" % "0.4"

val paradise = "org.scalamacros" %% "paradise" % "2.1.1"

lazy val root = (project in file("."))
  .settings(
    name := "Documents_Clusterizer",
    version := "0.0.1",
    scalaVersion := "2.12.3",
    organization := name.value,
    autoCompilerPlugins := true,
    scalacOptions := List("-encoding", "utf8", "-Xfatal-warnings", "-deprecation", "-unchecked", "-feature"),
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/releases",
    addCompilerPlugin(paradise cross CrossVersion.full),
    libraryDependencies ++= Seq(check, scalactic, testlib, poi, poiDocX, poiDoc, poiSchema, pdfbox, speedTest,
      scalaFX, sclFXML, mockTest),
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    parallelExecution in Test := false,
    mainClass in Compile := Some("org.Fcocco01.DocumentClassifier.Main")
  )
  .enablePlugins(
      SbtProguard,
      JavaServerAppPackaging,
      DockerPlugin//,
//      ensime
  )
