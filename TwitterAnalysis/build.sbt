import sbt.project

lazy val root = (project in file("."))
	.enablePlugins(PlayScala)
	.settings(
		name := """final_project""",
		organization := "edu.northeastern",
		version := "0.0.1",
		scalaVersion := "2.12.15",
		libraryDependencies ++= Seq(
			jdbc % Test,
			guice,
			"org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
			"org.apache.spark" %% "spark-sql" % "3.2.1",
			"com.typesafe.play" %% "play-slick" % "5.0.0",
			"org.postgresql" % "postgresql" % "42.3.3",
			"org.apache.spark" %% "spark-mllib" % "3.2.1",
			"com.johnsnowlabs.nlp" %% "spark-nlp-spark32" % "3.4.3",
			"org.projectlombok" % "lombok" % "1.18.22"
		),
		scalacOptions ++= Seq(
			"-feature",
			"-deprecation",
			"-Xfatal-warnings"
		),
		javacOptions ++= Seq("-source", "11")

	)
