import requests
import json
import time

url="http://i-2.gpushare.com:40003/book"

prompt="请帮我找找物理学的书"

# json_dt={"phone":"18215872913",
#          "password":"q3Wh7Zkx"
#          }

json_dt={
    "msg":prompt,
    "temperature":0.5
}

json_obj=json.dumps(json_dt)
headers = {'Content-Type': 'application/json'}
since=time.time()
requests.post(url,data=json_obj,headers=headers)                                                                          

# print(response.json())
print(f"响应时间：{time.time()-since}")

# ssh -p 42922 root@i-1.gpushare.com

