name := "eyesy"

version := "0.1.0-SNAPSHOT"

description := "Simple chrome extension to help your eyes"

scalaVersion := "2.12.4"

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

enablePlugins(ScalaJSPlugin)

val circeVersion = "0.9.0"
val scalaJsPreactVersion = "0.2.2-SNAPSHOT"

libraryDependencies ++= Seq(
  "biz.enef" %%% "slogging" % "0.6.0",
  "net.lullabyte" %%% "scala-js-chrome" % "0.5.0",
  "com.github.lmnet" %%% "scala-js-preact-core" % scalaJsPreactVersion,
  "com.github.lmnet" %%% "scala-js-preact-dsl-tags" % scalaJsPreactVersion,
  "io.circe" %%% "circe-core" % circeVersion,
  "io.circe" %%% "circe-generic" % circeVersion,
  "io.circe" %%% "circe-parser" % circeVersion
)

addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full)
scalacOptions += "-P:scalajs:sjsDefinedByDefault"

scalaJSUseMainModuleInitializer := true

// TODO: <div>Icons made by <a href="http://www.freepik.com" title="Freepik">Freepik</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
