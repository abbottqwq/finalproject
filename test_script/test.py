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
        actions = ["testconnect", "testspark", "testdatabase"]
        for action in actions:
            self.assertIn("Success", req(action))  # add assertion here


if __name__ == '__main__':
    unittest.main()