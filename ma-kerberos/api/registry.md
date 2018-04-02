**Registry**
----

[/registry/registerComponent](#registerComponent)

[/registry/registerSession](#registerSession)

[/registry/unregisterComponent](#unregisterComponent)

[/registry/unregisterSession](#unregisterSession)


<a name="registerComponent"></a>
* **URL**

  /registry/registerComponent  

* **Method:**

  `POST`

* **Data Params**

  **Media type:** application/json

  | Parameter | Type | Required |
  | --- | --- | --- |
  | id | String | Yes |
  | key | String | Yes |

* **Success Response:**

  * **Code:** 200

  * **Content:** `{ "result" : "true" }`

* **Sample Call:**

    ``` shell
    POST /registry/registerComponent
    Content-Type: application/json
    Accept: application/json

    {
      "id" : "...",
      "key" : "..."
    }
    ```

---
<a name="registerSession"></a>
* **URL**

  /registry/registerSession

* **Method:**

  `POST`

* **Data Params**

  **Media type:** application/json

  | Parameter | Type | Required |
  | --- | --- | --- |
  | sessionId | String | Yes |
  | transactionId | String | Yes |

* **Success Response:**

  * **Code:** 200

  * **Content:** `{ "result" : "true" }`

* **Sample Call:**

    ``` shell
    POST /registry/registerSession
    Content-Type: application/json
    Accept: application/json

    {
      "sessionid" : "...",
      "transactionId" : "..."
    }
    ```
---
<a name="unregisterComponent"></a>
* **URL**

  /registry/unregisterComponent  

* **Method:**

  `POST`

* **Data Params**

  **Media type:** application/json

  | Parameter | Type | Required |
  | --- | --- | --- |
  | id | String | Yes |
  | key | String | Yes |

* **Success Response:**

  * **Code:** 200

  * **Content:** `{ "result" : "true" }`

* **Sample Call:**

    ``` shell
    POST /registry/unregisterComponent
    Content-Type: application/json
    Accept: application/json
            
    {
      "id" : "...",
      "key" : "..."
    }
    ```

---
<a name="unregisterSession"></a>
* **URL**

  /registry/unregisterSession

* **Method:**

  `POST`

* **Data Params**

  **Media type:** application/json

  | Parameter | Type | Required |
  | --- | --- | --- |
  | sessionId | String | Yes |
  | transactionId | String | Yes |

* **Success Response:**

  * **Code:** 200

  * **Content:** `{ "result" : "true" }`

* **Sample Call:**

    ``` shell
    POST /registry/unregisterSession
    Content-Type: application/json
    Accept: application/json

    {
      "sessionid" : "...",
      "transactionId" : "..."
    }
    ```
