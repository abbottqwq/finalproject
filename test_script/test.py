from time import sleep
import unittest
import requests
import config


def req(action: str):
    json = requests.get(config.URL + action).json()
    print(action + ": " + str(json))
    return json


class MyTestCase(unittest.TestCase):
    def test_something(self):
        actions = ["testconnect", "testspark", "testdatabase", "testdatabase"]
        for action in actions:
            self.assertNotIn("Error", req(action))  # add assertion here


if __name__ == '__main__':
    unittest.main()
