FROM jboss/wildfly:9.0.2.Final

MAINTAINER Henrique Canto Duarte hcanto@cpqd.com.br

USER root

RUN sed -i "s|core-threads=\"5\" max-threads=\"25\"|core-threads=\"50\" max-threads=\"500\"|g" /opt/jboss/wildfly/standalone/configuration/standalone.xml

USER jboss

ADD application/kerberosintegration.ear /opt/jboss/wildfly/standalone/deployments/
ADD application/cryptointegration.ear /opt/jboss/wildfly/standalone/deployments/
ADD application/serviceregistry.ear /opt/jboss/wildfly/standalone/deployments/
ADD application/loggingapi.ear /opt/jboss/wildfly/standalone/deployments/
ADD application/libjcrypto-2.0.0-SNAPSHOT.jar /opt/jboss/wildfly/modules/system/layers/base/br/com/dojot/libjcrypto/main/
ADD application/module.xml /opt/jboss/wildfly/modules/system/layers/base/br/com/dojot/libjcrypto/main/
ADD application/libjcrypto.so /home/kerberos/

WORKDIR /opt/jboss/wildfly/standalone/deployments

CMD /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0
