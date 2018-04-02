#!/bin/sh

cd ..
chmod +x CryptoIntegrationCacheServer.jar
java -jar -Xdebug -Dhazelcast.logging.type=log4j -Dlog4j.configurationFile=config/log4j2.xml CryptoIntegrationCacheServer.jar
