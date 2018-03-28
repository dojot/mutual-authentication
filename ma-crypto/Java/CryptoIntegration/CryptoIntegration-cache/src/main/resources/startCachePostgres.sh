#!/bin/sh

cd ..
/usr/lib/jvm/java-8-oracle/bin/java -jar -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4001,suspend=n -Dcpqd-components-jpa-config-url=jdbc:postgresql://vmh75.cpqd.com.br:5432/saf -Dcpqd-components-jpa-config-driver=org.postgresql.Driver -Dcpqd-components-jpa-config-user=safc_des -Dcpqd-components-jpa-config-password=-2a7ace9f404cc768207a6df87216de44 -Dsaf-commons-jpa-config-url=jdbc:postgresql://vmh75.cpqd.com.br:5432/saf -Dsaf-commons-jpa-config-driver=org.postgresql.Driver -Dsaf-commons-jpa-config-user=safc_des -Dsaf-commons-jpa-config-password=-2a7ace9f404cc768207a6df87216de44 -Dsaf-treatment-jpa-config-url=jdbc:postgresql://vmh75.cpqd.com.br:5432/saf -Dsaf-treatment-jpa-config-driver=org.postgresql.Driver -Dsaf-treatment-jpa-config-user=saft_des -Dsaf-treatment-jpa-config-password=-64b9a3b617230861207a6df87216de44 -Dhazelcast.logging.type=log4j -Dlog4j.configurationFile=config/log4j2.xml HeuristicHazelcastServer.jar






