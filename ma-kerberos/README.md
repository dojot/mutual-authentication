# Kerberos

Kerberos is a mutual authentication protocol that uses tickets to allow entities communicating over a non-secure network.

**Kerberos REST API**

| name          | path                          | methods  | description |
| ------------- |-------------                  | -----:|--|
| [Health](./api/health.md) |   /health/status              | GET | Obtain Kerberos status information |
|               | /protocol/requestAS           | POST | First part of Kerberos handshake |
| [Protocol](./api/protocol.md) | /protocol/requestAP         | POST | Second part of Kerberos handshake |
| [Registry](./api/registry.md) | /registry/registerComponent | POST | Registers a new application|
|           | /registry/registerSession     | POST | Registers a new session|
|               | /registry/unregisterComponent | POST | Removes application from registry |
|               | /registry/unregisterSession   | POST | Removes session from registry |
