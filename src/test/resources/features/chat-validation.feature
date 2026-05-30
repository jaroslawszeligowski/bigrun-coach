Feature: Chat API — walidacja wejścia

  Scenario: Puste pole message jest odrzucone
    Given aplikacja działa
    When wysyłam POST z body {"message": ""}
    Then status odpowiedzi to 400
    And odpowiedź zawiera pole status równe 400
    And odpowiedź zawiera niepuste pole message

  Scenario: Brak pola message jest odrzucony
    Given aplikacja działa
    When wysyłam POST z body {}
    Then status odpowiedzi to 400
    And odpowiedź zawiera pole status równe 400
    And odpowiedź zawiera niepuste pole message

  Scenario: Null w polu message jest odrzucony
    Given aplikacja działa
    When wysyłam POST z body {"message": null}
    Then status odpowiedzi to 400
    And odpowiedź zawiera pole status równe 400
    And odpowiedź zawiera niepuste pole message

  Scenario: Zły Content-Type zwraca 415
    Given aplikacja działa
    When wysyłam POST z Content-Type "text/plain" i body "jakie tempo?"
    Then status odpowiedzi to 415

  Scenario: Metoda GET nie jest obsługiwana
    Given aplikacja działa
    When wysyłam GET na /api/v1/chat
    Then status odpowiedzi to 405

  Scenario: Zniekształcony JSON z wartością bez cudzysłowów zwraca 400
    Given aplikacja działa
    When wysyłam POST z body {"message": "test", "model": xyz}
    Then status odpowiedzi to 400
    And odpowiedź zawiera pole status równe 400

  Scenario: Zniekształcony JSON z brakującą wartością zwraca 400
    Given aplikacja działa
    When wysyłam POST z body {"message": "test", "model": }
    Then status odpowiedzi to 400
    And odpowiedź zawiera pole status równe 400
