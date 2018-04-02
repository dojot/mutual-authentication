**Protocol**
----

[/protocol/requestAP](#requestAP)

[/protocol/requestAS](#requestAS)

<a name="requestAP"></a>
* **URL**

  /protocol/requestAP

* **Method:**

  `POST`

* **Data Params**

  **Media type:** application/json

    | Parameter | Type | Required
    | --- | --- | --- |
    | request | String | Yes |
    | transactionId| String | Yes |
    | sessionId| String | Yes |

* **Success Response:**

  * **Code:** 200
  * **Content:**

    ```json
    {
      "kerberosReply":"...",
      "description":"CS-CCS-000"
    }
    ```

* **Sample Call:**

    ``` shell
    POST /protocol/requestAP
    Content-Type: application/json
    Accept: application/json


    {
      "request" : "...",
      "transactionId" : "...",
      "sessionId" : "...",
    }  
    ```
----

* **URL**

  <a name="requestAS"></a>/protocol/requestAS

* **Method:**

  `POST`

* **Data Params**

  **Media type:** application/json

    | Parameter | Type | Required
    | --- | --- | --- |
    | request | String | Yes |
    | transactionId| String | Yes |
    | sessionId| String | Yes |

* **Success Response:**

  * **Code:** 200
  * **Content:**

    ```json
    {
      "kerberosReply":"...",
      "description":"CS-CCS-000"
    }
    ```

* **Sample Call:**

    ```shell
    POST /protocol/requestAS
    Content-Type: application/json
    Accept: application/json


    {
      "request" : "...",
      "transactionId" : "...",
      "sessionId" : "...",
    }  
    ```
