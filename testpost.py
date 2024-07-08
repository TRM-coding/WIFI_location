import requests
import json
import time

url="http://i-2.gpushare.com:40003/location"

prompt="今天海淀天气怎么样？"

# json_dt={"phone":"18215872913",
#          "password":"q3Wh7Zkx"
#          }

json_dt={
    "msg":prompt,
    "temperature":0.9
}


mac_dt={
    "mac_list":['12:69:6c:bd:e1:2b','12:69:6c:d6:98:0b','12:69:6c:d6:98:0c'],
    "mac_strength":[-14,-1,-100]
}

# dict={
#     "sx":1,
#     "sy":1,
#     "sz":4,
#     "bookid":3}

json_obj=json.dumps(mac_dt)
headers = {'Content-Type': 'application/json'}
since=time.time()
req=requests.post(url,data=json_obj,headers=headers)                                                                          

print(req.json())
print(f"响应时间：{time.time()-since}")

# ssh -p 42922 root@i-1.gpushare.com

