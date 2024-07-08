import requests
import json
import time

url="http://i-2.gpushare.com:40003/chat"

prompt="今天海淀天气怎么样？"

# json_dt={"phone":"18215872913",
#          "password":"q3Wh7Zkx"
#          }

json_dt={
    "msg":prompt,
    "temperature":0.9
}

# dict={
#     "sx":1,
#     "sy":1,
#     "sz":4,
#     "bookid":3}

json_obj=json.dumps(json_dt)
headers = {'Content-Type': 'application/json'}
since=time.time()
req=requests.post(url,data=json_obj,headers=headers)                                                                          

print(req.json())
print(f"响应时间：{time.time()-since}")

# ssh -p 42922 root@i-1.gpushare.com

