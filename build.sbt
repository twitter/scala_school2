name := "scaffold"

organization := "com.twitter"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.2"

resolvers += "spray repo" at "http://repo.spray.io"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"     % "2.2.0-RC1",
  "io.spray"          %  "spray-can"      % "1.2-M8",
  "io.spray"          %  "spray-httpx"    % "1.2-M8",
  "io.spray"          %  "spray-routing"  % "1.2-M8",
  "org.pegdown"       %  "pegdown"        % "1.3.0",
  "org.scala-lang"    %  "scala-compiler" % "2.10.2"
)

fork := true

seq(Revolver.settings: _*)

seq(Twirl.settings: _*)

Twirl.twirlImports := Seq("com.twitter.scaffold._")
