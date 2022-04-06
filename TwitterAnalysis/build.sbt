import sbt.project

lazy val root = (project in file("."))
	.enablePlugins(PlayScala)
	.settings(
		name := """final_project""",
		organization := "edu.northeastern",
		version := "0.0.1",
		scalaVersion := "2.13.6",
		libraryDependencies ++= Seq(
			guice,
			"org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
			"org.apache.spark" %% "spark-sql" % "3.2.1"
		),
		scalacOptions ++= Seq(
			"-feature",
			"-deprecation",
			"-Xfatal-warnings"
		),
		javacOptions ++= Seq("-source", "11")

	)
