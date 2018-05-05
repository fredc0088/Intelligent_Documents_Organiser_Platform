import sbt.Keys.testFrameworks
import sbt.addCompilerPlugin
import AssemblyPlugin.assemblySettings

// sbt-assembly
assemblySettings

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

val io_commons = "org.apache.commons" % "commons-io" % "1.3.2"

//noinspection Annotator,Annotator
val paradise = "org.scalamacros" %% "paradise" % "2.1.1"

/* Possible library to implement flat clustering sparse diagram */
//val jFreeCHartForFlatPlot = Seq("org.jfree" % "jfreechart" % "1.5.0", "org.jfree" % "jfreechart-fx" % "1.0.1",
//  "org.jfree" % "jcommon" % "1.0.24")

lazy val root = (project in file("."))
  .settings(
    name := "Documents_Clusterizer",
    version := "1.0.0",
    scalaVersion := "2.12.3",
    organization := "org.Fcocco01",
    autoCompilerPlugins := true,
    scalacOptions := List("-encoding", "utf8", "-Xfatal-warnings", "-deprecation", "-unchecked", "-feature"),
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/releases",
    resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases",
    resolvers += Resolver.url("artifactory",
      url("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns),
      addCompilerPlugin(paradise cross CrossVersion.full),
    libraryDependencies ++= Seq(check, scalactic, testlib, poi, poiDocX, poiDoc, poiSchema, pdfbox, speedTest,
      scalaFX, sclFXML, mockTest, io_commons) // ++ jFreeCHartForFlatPlot
    , testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    parallelExecution in Test := false,
    mainClass in (Compile, run) := Some("org.Fcocco01.DocumentClassifier.Main"),
    unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/ext/jfxrt.jar"))
  )
  .enablePlugins(
      SbtProguard,
      JavaServerAppPackaging,
      DockerPlugin,
      AssemblyPlugin
  )

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}



fork := true

