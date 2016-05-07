import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin._

// see https://github.com/scala-js/scala-js/issues/1331
import autoImport._

object Dependencies {
  val commonsIoVersion = "2.4"
  val upickleVersion = "0.3.9"
  val scalatestVersion = "3.0.0-M15"
  val scalacheckVersion = "1.12.5"
  val scalatraVersion = "2.4.0"

  val upickle = "com.lihaoyi" %% "upickle" % upickleVersion
  val commons_io = "commons-io" % "commons-io" % commonsIoVersion
  val scalatra = "org.scalatra" %% "scalatra" % scalatraVersion
  val scalatraSwagger = "org.scalatra" %% "scalatra-swagger" % scalatraVersion
  val scalatraTest = "org.scalatra" %% "scalatra-scalatest" % scalatraVersion
  val servletApi =  "javax.servlet" % "javax.servlet-api" % "3.1.0"
  object js {
    val upickle = Def.setting("com.lihaoyi" %%% "upickle" % upickleVersion)
    val scalatest = Def.setting("org.scalatest" %%% "scalatest" % scalatestVersion)
    val scalacheck = Def.setting("org.scalacheck" %%% "scalacheck" % scalacheckVersion)
  }
}