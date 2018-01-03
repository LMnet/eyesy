name := "eyesy"

version := "0.1.0-SNAPSHOT"

description := "Simple chrome extension to help your eyes"

scalaVersion := "2.12.4"

enablePlugins(ScalaJSPlugin)

libraryDependencies ++= Seq(
  "biz.enef" %%% "slogging" % "0.6.0",
  "net.lullabyte" %%% "scala-js-chrome" % "0.5.0"
)

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-Ywarn-unused",
  "-Ywarn-unused-import",
  "-Ywarn-numeric-widen",
  "-Xlint:missing-interpolator"
)

scalaJSUseMainModuleInitializer := true

// TODO: <div>Icons made by <a href="http://www.freepik.com" title="Freepik">Freepik</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
