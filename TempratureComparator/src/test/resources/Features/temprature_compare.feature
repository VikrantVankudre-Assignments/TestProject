@smoke
Feature: Weather temprature comparator feature

  Scenario Outline: Temprature comparison
    Given user is on Accu Weather page
    And user enters "<cityDetails>" in searchbox
    And user captures the temprature details on the UI page
    And it should be approximately match with the API temprature details

    Examples: 
      | cityDetails              |
      | San Jose, California, US |
