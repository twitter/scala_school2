name := "scaffold"

organization := "com.twitter"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.4"

resolvers += "spray repo" at "https://repo.spray.io"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"     % "2.2.0-RC1" % "compile",
  "io.spray"          %  "spray-caching"  % "1.2-M8"    % "compile",
  "io.spray"          %  "spray-can"      % "1.2-M8"    % "compile",
  "io.spray"          %  "spray-httpx"    % "1.2-M8"    % "compile",
  "io.spray"          %% "spray-json"     % "1.2.5"     % "compile",
  "io.spray"          %  "spray-routing"  % "1.2-M8"    % "compile",
  "org.pegdown"       %  "pegdown"        % "1.4.0"     % "compile",
  "org.scala-lang"    %  "scala-compiler" % "2.10.4"    % "compile",
  "org.webjars"       %  "bootstrap"      % "2.3.2"     % "runtime",
  "org.webjars"       %  "codemirror"     % "3.14"      % "runtime",
  "org.webjars"       %  "html5shiv"      % "3.6.2"     % "runtime",
  "org.webjars"       %  "jquery"         % "2.0.2"     % "runtime",
  "com.typesafe.akka" %% "akka-testkit"   % "2.2.0-RC1" % "test",
  "io.spray"          %  "spray-testkit"  % "1.2-M8"    % "test",
  "org.scalatest"     %% "scalatest"      % "1.9.1"     % "test"
)

fork := true

seq(Revolver.settings: _*)

seq(Twirl.settings: _*)

seq(com.typesafe.sbt.SbtStartScript.startScriptForClassesSettings: _*)

Twirl.twirlImports := Seq("com.twitter.scaffold.Document", "Document._")
