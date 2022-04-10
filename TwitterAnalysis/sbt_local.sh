#!/usr/bin/env bash
echo "export dotenv..."
export SBT_ENV_LOCAL=../.env
echo $SBT_ENV_LOCAL
export $(xargs <$SBT_ENV_LOCAL)
echo "load dotenv from $SBT_ENV_LOCAL"
PGHOST="localhost"
echo "starting sbt"
./sbt-dist/bin/sbt -java-home "/opt/homebrew/Cellar/openjdk@11/11.0.14.1" "$@"