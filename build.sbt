val akkaVersion = "2.5.19"
val akkaHttpVersion = "10.1.8"

name := "todo-akka"
version := "1.0"
scalaVersion := "2.12.3"

libraryDependencies += "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
libraryDependencies += "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream"          % akkaVersion

libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit"   % akkaHttpVersion % Test
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion     % Test
libraryDependencies += "org.scalatest"     %% "scalatest"         % "3.0.1"         % Test
libraryDependencies += "org.scalamock"     %% "scalamock-scalatest-support" % "3.6.0" % Test

resourceDirectory in Compile := baseDirectory.value / "resources"
resourceDirectory in Test := baseDirectory.value / "testResources"
