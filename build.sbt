name := "scaffold"

organization := "com.twitter"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.2"

resolvers += "spray repo" at "http://repo.spray.io"

resolvers += "Local Maven repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

libraryDependencies ++= Seq(
  "com.typesafe.akka"   %% "akka-actor"         % "2.2.0-RC1" % "compile",
  "io.spray"            %  "spray-caching"      % "1.2-M8"    % "compile",
  "io.spray"            %  "spray-can"          % "1.2-M8"    % "compile",
  "io.spray"            %  "spray-httpx"        % "1.2-M8"    % "compile",
  "io.spray"            %% "spray-json"         % "1.2.5"     % "compile",
  "io.spray"            %  "spray-routing"      % "1.2-M8"    % "compile",
  "org.pegdown"         %  "pegdown"            % "1.4.0"     % "compile",
  "org.scala-lang"      %  "scala-compiler"     % "2.10.2"    % "compile",
  "org.webjars"         %  "bootstrap"          % "2.3.2"     % "runtime",
  "org.webjars"         %  "codemirror"         % "3.14"      % "runtime",
  "org.webjars"         %  "html5shiv"          % "3.6.2"     % "runtime",
  "org.webjars"         %  "jquery"             % "2.0.2"     % "runtime",
  "com.atlassian.levee" %  "atlassian-levee"    % "1.0-SNAPSHOT" % "compile"
)

fork := true

seq(Revolver.settings: _*)

seq(Twirl.settings: _*)

Twirl.twirlImports := Seq("com.twitter.scaffold.Document", "Document._")
