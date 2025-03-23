import unittest
import cjson
import json
import ujson
from random import randint
import time


class cjsonTest(unittest.TestCase):
    def test_cjson_dumps_integers(self):
        dict = {"first": 12, "second": 23, "third": 34}
        dictStr = '{"first":12,"second":23,"third":34}'
        print(cjson.dumps(dict))
        self.assertEqual(cjson.dumps(dict), dictStr)
    
    def test_cjson_dumps_strings(self):
        dict = {"first": "a", "second": "b", "third": "c"}
        dictStr = '{"first":"a","second":"b","third":"c"}'
        print(cjson.dumps(dict))
        self.assertEqual(cjson.dumps(dict), dictStr)

    def test_cjson_dumps_mixed(self):
        dict = {"first": 12, "second": "b", "third": 34}
        dictStr = '{"first":12,"second":"b","third":34}'
        print(cjson.dumps(dict))
        self.assertEqual(cjson.dumps(dict), dictStr)

    def test_cjson_loads_integers(self):
        dict = {"first": 12, "second": 23, "third": 34}
        dictStr = '{"first":12,"second":23,"third":34}'
        res = cjson.loads(dictStr)
        print(res)
        self.assertEqual(res, dict)

    def test_cjson_loads_strings(self):
        dict = {"first": "abc", "second": "def", "third": "ghj"}
        dictStr = '{"first":"abc","second":"def","third":"ghj"}'
        res = cjson.loads(dictStr)
        print(res)
        self.assertEqual(res, dict)

    def test_cjson_loads_mixed(self):
        dict = {"first": 12, "second": "ba", "third": 34}
        dictStr = '{"first":12,"second":"ba","third":34}'
        res = cjson.loads(dictStr)
        print(res)
        self.assertEqual(res, dict)

    def test_performance(self):
        words = ["lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing"]
        dict = {}
        for i in range(1, 10000):
            dict[words[randint(0, 6)]] = randint(0, 500)
        cjson_start_time = time.time()
        cjson.dumps(dict)
        print("cjson results:", time.time() - cjson_start_time)
        json_start_time = time.time()
        json.dumps(dict)
        print("json results:", time.time() - json_start_time)
        ujson_start_time = time.time()
        ujson.dumps(dict)
        print("ujson results:", time.time() - ujson_start_time)
        self.assertEqual(1, 1)
        

if __name__ == '__main__':
    unittest.main()
