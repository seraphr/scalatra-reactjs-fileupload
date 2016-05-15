import CommonSettings._

val TestPhase = "test->test"
val ConpilePhase = "compile->compile"
val TestAndCompilePhase = List(TestPhase, ConpilePhase).mkString(";")

// root project
lazy val root = (project in file("."))
  .settings(unidocSettings: _*)
  .settings(
    TaskKey[Unit]("checkScalariform") := {
      val diff = "git diff".!!
      if (diff.nonEmpty) {
        sys.error("Working directory is dirty!\n" + diff)
      }
    },
    publish := {}
  )
  .aggregate(
    commonJVM,
    commonJS,
    view,
    client_main,
    webapi,
    server_main
  )

// sub projects

/// ----- server / client 共通プロジェクト -----
/* 共有のユーティリティ置き場 */
lazy val common = crossProject
  .crossType(CrossType.Pure)
  .in(file("common"))
  .withCrossCommonSettings
  .jsSettings(
    libraryDependencies ++= Seq(
      Dependencies.scalajs.reactjs.value,
      Dependencies.scalajs.scalajsDom.value,
      Dependencies.scalajs.upickle.value
    ),
    jsDependencies ++= Seq(
      Dependencies.js.reactjs,
      Dependencies.js.reactDom,
      Dependencies.js.flux
    )
  )

lazy val commonJVM = common.jvm
lazy val commonJS = common.js


/// ----- client プロジェクト -----

/* js用のview置き場 */
lazy val view = (project in file("client-view"))
  .withJsCommonSettings
  .dependsOn(
    commonJS)

lazy val client_main = (project in file("client-main"))
  .withJsCommonSettings
  .settings(
    Seq(fastOptJS, fullOptJS, packageMinifiedJSDependencies, packageJSDependencies) map { packageJSKey =>
      crossTarget in (Compile, packageJSKey) := (crossTarget in (Compile, packageJSKey)).value / "js"
    }
  )
  .dependsOn(
    view
  )

/// ----- server プロジェクト -----
/* webapi実装置き場 */
lazy val webapi = (project in file("webapi"))
  .withJvmCommonSettings
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.jvm.scalatra,
      Dependencies.jvm.scalatraSwagger,
      Dependencies.jvm.upickle,
      Dependencies.jvm.commonsIO,
      Dependencies.jvm.servletApi % "provided",
      Dependencies.jvm.scalatraTest % "test"
    )
  )
  .dependsOn(
    commonJVM
  )

/* server側エントリーポイント */
lazy val server_main = (project in file("server-main"))
  .withJvmCommonSettings
  .dependsOn(
    webapi,
    commonJVM
  ).settings(
    libraryDependencies ++= Seq(
      Dependencies.jvm.jetty,
      Dependencies.jvm.jettyWebapp,
      Dependencies.jvm.servletApi
    )
  )
  .settings(packAutoSettings)
  .settings(
    packResourceDir += (baseDirectory.value / "src/main/webapp" -> "webapp"),
    packResourceDir += ((crossTarget in client_main).value / "js" -> "webapp/assets/js"),
    pack <<= pack.dependsOn(fastOptJS in(client_main, Compile))
  )

// command alias
addCommandAlias("riformAll", ";compile:scalariformFormat;test:scalariformFormat")
addCommandAlias("riformAndCheck", ";riformAll;checkScalariform")
addCommandAlias("coverageTest", ";clean;coverage;test;coverageReport;coverageOff")
