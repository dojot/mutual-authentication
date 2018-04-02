**Crypto**
----
[/crypto/decrypt](#decrypt)

[/crypto/decryptWithCC](#decryptWithCC)

[/crypto/encrypt](#encrypt)

[/crypto/encryptWithCC](#encryptWithCC)

[/crypto/registerCC](#registerCC)

[/crypto/unregisterCC](#unregisterCC)

<a name="decrypt"></a>
* **URL**

  /crypto/decrypt

* **Method:**

  `POST`

* **Data Params**

    **Media type:** application/json

    | Parameter | Type | Required |
    | --- | --- | --- |
    | data | String | Yes |
    | tagSize | Integer | Yes |
    | key | String | Yes |
    | iv | String | Yes |

* **Sample Call:**

    ``` shell
    POST /crypto/decrypt
    Content-Type: application/json
    Accept: application/json

    {
      "data" : "...",
      "tagSize" : 12345,
      "key" : "...",
      "iv" : "..."
    }
    ```

---

<a name="decryptWithCC"></a>
* **URL**

  /crypto/decryptWithCC

* **Method:**

  `POST`

* **Data Params**

    **Media type:** application/json

    | Parameter | Type | Required |
    | --- | --- | --- |
    | sessionId | String | Yes |
    | Data | String | Yes |

* **Sample Call:**

    ``` shell
    POST /crypto/decryptWithCC
    Content-Type: application/json
    Accept: application/json

    {
        "sessionId" : "...",
        "data" : "..."
    }
    ```
---

<a name="encrypt"></a>
* **URL**

  /crypto/encrypt

* **Method:**

  `POST`

* **Data Params**

    **Media type:** application/json

    | Parameter | Type | Required |
    | --- | --- | --- |
    | data | String | Yes |
    | tagSize | Integer | Yes |
    | key | String | Yes |
    | iv | String | Yes |

* **Sample Call:**

    ``` shell
    POST /crypto/encrypt
    Content-Type: application/json
    Accept: application/json

    {
      "data" : "...",
      "tagSize" : 12345,
      "key" : "...",
      "iv" : "..."
    }
    ```
---

<a name="encryptWithCC"></a>
* **URL**

  /crypto/encryptWithCC

* **Method:**

  `POST`

* **Data Params**

    **Media type:** application/json

    | Parameter | Type | Required |
    | --- | --- | --- |
    | sessionId | String | Yes |
    | data | String | Yes |

* **Sample Call:**

    ``` shell
    POST /crypto/encryptWithCC
    Content-Type: application/json
    Accept: application/json

    {  
      "sessionId" : "...",
      "data" : "..."
    }
    ```
---

<a name="registerCC"></a>
* **URL**

  /crypto/registerCC

* **Method:**

  `POST`

* **Data Params**

    **Media type:** application/json

    | Parameter | Type | Required |
    | --- | --- | --- |
    | provider | String | Yes |
    | transactionId | Integer | Yes |
    | lifespan | Integer | Yes |
    | ivServerToComponent | String | Yes |
    | keyServerToComponent | String | Yes |
    | sessionId | String | Yes |
    | keyComponentToServer | String | Yes |
    | ivComponentToServer | String | Yes |
    | tagLen | Integer | Yes |

* **Sample Call:**

    ``` shell
    POST /crypto/registerCC
    Content-Type: application/json
    Accept: application/json

    {
        "provider" : "...",
        "transactionId" : "...",
        "lifespan" : 12345,
        "ivServerToComponent" : "...",
        "keyServerToComponent" : "...",
        "sessionId" : "...",
        "keyComponentToServer" : "...",
        "ivComponentToServer" : "...",
        "tagLen" : 12345
    }
    ```
---

<a name="unregisterCC"></a>
* **URL**

  /crypto/unregisterCC

* **Method:**

  `POST`

* **Data Params**

    **Media type:** application/json

    | Parameter | Type | Required |
    | --- | --- | --- |
    | provider | String | Yes |
    | transactionId | Integer | Yes |
    | lifespan | Integer | Yes |
    | ivServerToComponent | String | Yes |
    | keyServerToComponent | String | Yes |
    | sessionId | String | Yes |
    | keyComponentToServer | String | Yes |
    | ivComponentToServer | String | Yes |
    | tagLen | Integer | Yes ||

* **Sample Call:**

    ``` shell
    POST /crypto/decrypt
    Content-Type: application/json
    Accept: application/json

    {
        "provider" : "...",
        "transactionId" : "...",
        "lifespan" : 12345,
        "ivServerToComponent" : "...",
        "keyServerToComponent" : "...",
        "sessionId" : "...",
        "keyComponentToServer" : "...",
        "ivComponentToServer" : "...",
        "tagLen" : 12345
    }
    ```
