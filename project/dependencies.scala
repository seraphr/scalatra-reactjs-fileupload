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
  val jettyVersion = "9.3.8.v20160314"

  object jvm {
    val upickle = "com.lihaoyi" %% "upickle" % upickleVersion
    val commonsIO = "commons-io" % "commons-io" % commonsIoVersion
    val scalatra = "org.scalatra" %% "scalatra" % scalatraVersion
    val scalatraSwagger = "org.scalatra" %% "scalatra-swagger" % scalatraVersion
    val scalatraTest = "org.scalatra" %% "scalatra-scalatest" % scalatraVersion
    val servletApi = "javax.servlet" % "javax.servlet-api" % "3.1.0"
    val jetty = "org.eclipse.jetty" % "jetty-server" % jettyVersion
    val jettyWebapp = "org.eclipse.jetty" % "jetty-webapp" % jettyVersion
  }

  object scalajs {
    val upickle = Def.setting("com.lihaoyi" %%% "upickle" % upickleVersion)
    val scalatest = Def.setting("org.scalatest" %%% "scalatest" % scalatestVersion)
    val scalacheck = Def.setting("org.scalacheck" %%% "scalacheck" % scalacheckVersion)
    val reactjs = Def.setting("com.github.japgolly.scalajs-react" %%% "core" % "0.11.1")
    val scalajsDom = Def.setting("org.scala-js" %%% "scalajs-dom" % "0.9.0")
  }

  object js {
    val reactVersion = "15.0.2"
    val reactjs = ("org.webjars.bower" % "react" % "15.0.2"
      / "react-with-addons.js"
      minified "react-with-addons.min.js"
      commonJSName "React")

    val reactDom = ("org.webjars.bower" % "react" % "15.0.2"
      / "react-dom.js"
      minified "react-dom.min.js"
      dependsOn "react-with-addons.js"
      commonJSName "ReactDOM")

    val flux = ("org.webjars" % "flux" % "2.1.1"
      / "Flux.js"
      minified "Flux.min.js"
      commonJSName "Flux")
  }
}