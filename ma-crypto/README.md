# Crypto

Crypto offers a secure channel so applications can communicate safely between each other

**Crypto REST API**

| name          | path                          | methods  | description |
| ------------- |-------------                  | -----:|--|
| [Health](./api/health.md) | /health/status | GET | Obtain Crypto status information |
| [Crypto](./api/crypto.md) | /crypto/decrypt | POST | Decrypts a message |
| | /crypto/decryptWithCC | POST | Decrypts a message using a Crypto Channel |
| | /crypto/encrypt | POST | Encrypts a message |
| | /crypto/encryptWithCC | POST | Encrypts a message using a Crypto Channel |
| | /crypto/registerCC | POST | Registers a new Crypto Channel |
| | /crypto/unregisterCC | POST | Unregisters a Crypto Channel |
