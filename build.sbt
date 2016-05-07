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
lazy val commonJVM = common.jvm
lazy val commonJS = common.js


/// ----- client プロジェクト -----
/* js用httpclient実装置き場  http_client_apiが CrossType.Fullになる場合消える*/

/* js用のview置き場 */
lazy val view = (project in file("client-view"))
  .withJsCommonSettings
  .dependsOn(
    commonJS)


/// ----- server プロジェクト -----
/* webapi実装置き場 */
lazy val webapi = (project in file("webapi"))
  .withJvmCommonSettings
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.scalatra,
      Dependencies.scalatraSwagger,
      Dependencies.upickle,
      Dependencies.servletApi % "provided",
      Dependencies.scalatraTest % "test"
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
  )

// command alias
addCommandAlias("riformAll", ";compile:scalariformFormat;test:scalariformFormat")
addCommandAlias("riformAndCheck", ";riformAll;checkScalariform")
addCommandAlias("coverageTest", ";clean;coverage;test;coverageReport;coverageOff")
