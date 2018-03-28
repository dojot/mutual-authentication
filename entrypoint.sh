#!/bin/bash

cat > /opt/jboss/wildfly/standalone/configuration/node.config << EOF
hostname=kerberos
port=8080
node=kerberos
version=A
EOF

cat > /opt/jboss/wildfly/standalone/configuration/kafka.brokers << EOF
kafkabootstrap=${KAFKA_ADDRESS:-kafka}:9092
EOF

cat > /opt/jboss/wildfly/standalone/configuration/redis.sentinels << EOF
master.name=redis-cs-cluster
sentinels=${REDIS_ADDRESS:ma-redis}:16379
EOF

/opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0
