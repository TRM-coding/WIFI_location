import json
import time
import requests

# url="http://i-2.gpushare.com:40003/chat"
url="http://i-2.gpushare.com:40003/location"

prompt="今天海淀天气怎么样？"

# json_dt={"phone":"18215872913",
#          "password":"q3Wh7Zkx"
#          }
# json_dt={
#     "msg":prompt,
#     "temperature":0.9
# }


mac_dt={
    "mac_list":["12:69:6c:d6:98:0c","12:69:6c:bd:e1:2b","12:69:6c:bd:e1:2a","12:69:6c:d6:8c:04","12:69:6c:d6:98:0b","12:69:6c:d6:a5:9c","12:69:6c:d6:98:ac","12:69:6c:d6:8c:03","12:69:6c:d6:98:ab","12:69:6c:d4:39:e6","12:69:6c:d6:9d:7b","12:69:6c:d6:8c:bf","12:69:6c:d6:9e:20"],
    "mac_strength":[-55,-55,-64,-71,-72,-72,-75,-75,-81,-83,-88,-89,-95]
}

# dict={
#     "sx":1,
#     "sy":1,
#     "sz":4,
#     "bookid":3}

# json_obj=json.dumps(json_dt)
json_obj=json.dumps(mac_dt)
headers = {'Content-Type': 'application/json'}
since=time.time()
req=requests.post(url,data=json_obj,headers=headers)                                                                          
print(req.json())
print(f" 响应时间：{time.time()-since}")