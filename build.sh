#!/usr/bin/env bash

export ANT_OPTS="-Dhttp.proxyHost=proxy-src.research.ge.com -Dhttp.proxyPort=8080"

ant deploy
