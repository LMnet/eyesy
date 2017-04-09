import chrome.{Background, BrowserAction, ExtensionManifest}
import net.lullabyte.Chrome

name := "eyesy"

version := "0.1.0-SNAPSHOT"

description := "Simple chrome extension to help your eyes"

scalaVersion := "2.12.1"

enablePlugins(ScalaJSPlugin, ChromeSbtPlugin)

libraryDependencies ++= Seq(
  "biz.enef" %%% "slogging" % "0.5.2",
  "net.lullabyte" %%% "scala-js-chrome" % "0.4.0"
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

persistLauncher := true
persistLauncher in Test := false

chromeManifest := new ExtensionManifest {
  val name = Keys.name.value
  val version = VersionNumber(Keys.version.value).numbers.mkString(".")
  override val description = Some(Keys.description.value)

  override val browserAction = Some(BrowserAction(
    icon = Map(
      16  -> "eye_16.png",
      32  -> "eye_32.png",
      64  -> "eye_64.png",
      128 -> "eye_128.png",
      256 -> "eye_256.png"
    ), // TODO: <div>Icons made by <a href="http://www.freepik.com" title="Freepik">Freepik</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
    title = Some(Keys.name.value)
  ))

  val background = Background(
    scripts = Chrome.defaultScripts
  )

}
