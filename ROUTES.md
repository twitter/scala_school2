# Routes

* **/assets/{rest}**

  * **GET**: serve static resources

* **/**

  * **GET**: render index (twirl)

* **/{rest}**

  * **GET**: render lesson (twirl + markdown)

* **/interpreter**

  * **POST**: Create a new sub-resource representing a session.

      Should respond with 201 CREATED, or 403 FORBIDDEN if somebody's spamming.
* **/interpreter/{id}**

  * **POST**: send code to be interpreted.

      Should respond with 200 OK normally, 400 BAD REQUEST if the code is broken, 403 FORBIDDEN if the user does something malicious like `System.exit`, or 404 NOT FOUND if the session id doesn't exist.

  * **DELETE**: kill the session.

      Should return 204 NO CONTENT normally, 404 NOT FOUND if the session id doesn't exist.

* **/interpreter/{id}/completions/{string}**
  * **GET**: submit a partial string identifier for autocompletions.

      Should respond with 200 OK (media type application/json) containing an array of completions, or 404 NOT FOUND if the session id doesn't exist.
