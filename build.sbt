lazy val akkaHttpVersion = "10.0.9"
lazy val akkaVersion    = "2.5.3"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.mpopescu",
      scalaVersion    := "2.12.3"
    )),
    resourceDirectory in Compile := baseDirectory.value / "resources",
    resourceDirectory in Test := baseDirectory.value / "testResources",
    name := "todo-akka",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,

      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
      "org.scalatest"     %% "scalatest"         % "3.0.1"         % Test,
      "org.scalamock"     %% "scalamock-scalatest-support" % "3.6.0" % Test
    )
  )
