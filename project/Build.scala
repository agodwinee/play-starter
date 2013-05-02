import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "starter"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "org.json4s" %% "json4s-jackson" % "3.2.4",
    "org.eclipse.jetty" % "jetty-server" % "7.6.10.v20130312"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    resourceDirectory in Test <<= (baseDirectory) apply  {(baseDir: File) => baseDir / "test-resources"}
  )

}
