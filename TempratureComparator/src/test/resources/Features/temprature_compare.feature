@smoke
Feature: Weather temprature comparator feature

  Scenario Outline: Temprature comparison
    Given user is on Accu Weather page
    When user enters "<cityDetails>" in searchbox
    And user captures the temprature details on the UI page
    And it should be approximately match with the API "<city>" temprature details

    Examples: 
      | cityDetails              | city       |
      | San Jose, California, US | San Jose   |
      | Texas City, Texas, US    | Texas City |
