import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.cross.CrossProject
import sbt._
import sbt.Keys._

import scalariform.formatter.preferences.{DoubleIndentClassDeclaration, AlignSingleLineCaseStatements}

object CommonSettings {
  val Organization = "jp.seraphr"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.11.8"


  val RiformSettings = (
    ScalariformKeys.preferences := (ScalariformKeys.preferences.value
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 60))
      .setPreference(DoubleIndentClassDeclaration, false))

  val CommonDependencies = Def.settings(
    libraryDependencies ++= Seq(
      Dependencies.js.scalatest.value % "test",
      Dependencies.js.scalacheck.value % "test"
    )
  )

  val SlowTest = config("slow").extend(Test)
  val SlowTestSettings = inConfig(SlowTest) {
    Defaults.testTasks ++ Seq(
      testOptions in SlowTest := Seq(
        Tests.Argument(TestFrameworks.ScalaTest, "-oS")
      )
    )
  }


  val CommonSettings = Def.settings(
    incOptions := incOptions.value.withNameHashing(true),
    updateOptions := updateOptions.value.withCachedResolution(true),
    organization := Organization,
    version := Version,
    scalaVersion := ScalaVersion,
    name := name.value.replace("_", "-"),
    testOptions in Test := Seq(
      Tests.Argument(TestFrameworks.ScalaTest, "-oS"),
      Tests.Argument(TestFrameworks.ScalaTest, "-l", "org.scalatest.tags.Slow")
    ),
    testFrameworks -= TestFrameworks.ScalaCheck,
    scalacOptions in(Compile, doc) ++= Seq("-groups", "-implicits", "-diagrams"),
    scalacOptions ++= Seq(
      "-encoding", "UTF-8",
      "-feature", "-deprecation", "-unchecked", "-Xlint:_",
      "-Ywarn-unused",
      "-Ywarn-unused-import",
      "-Ywarn-dead-code"
    )
  ) ++ CommonDependencies ++ RiformSettings

  val JvmCommonSettings = Def.settings(
    testOptions in Test ++= Seq(
      Tests.Argument(TestFrameworks.ScalaTest, "-u", "target/junit")
    )
  )
  val JsCommonSettings = Def.settings(
    //  scalaJSUseRhino in Global := false
  )

  implicit class ProjectOpts(val p: Project) extends AnyVal{
    def withCommonSettings: Project = {
      p.settings(CommonSettings: _*)
        .configs(SlowTest)
        .settings(SlowTestSettings)
    }

    def withJsCommonSettings: Project = {
      p.enablePlugins(ScalaJSPlugin)
        .withCommonSettings
        .settings(JsCommonSettings: _*)
    }

    def withJvmCommonSettings: Project = {
      p.withCommonSettings
        .settings(JvmCommonSettings)
    }
  }

  implicit class CrossProjectOpts(val p: CrossProject) extends AnyVal {
    def withCrossCommonSettings: CrossProject = {
      p.settings(CommonSettings: _*)
        .jvmSettings(JvmCommonSettings: _*)
        .jsSettings(JsCommonSettings: _*)
    }
  }
}