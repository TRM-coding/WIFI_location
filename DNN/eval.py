import torch
from torch import nn
import json
import pymysql
jsondatas=[]
lables=[]
torch.cuda.empty_cache()
with open('./DNN/test.json','r')as file:
    for line in file:
        datai=json.loads(line)
        mac=datai['wifimac']
        stre=datai['wifistrength']
        jsondatas.append([mac,stre])
        x=datai['lx']
        y=datai['ly']
        z=datai['lz']
        print((x,y,z))
        lables.append((x,y,z))

print(len(lables))

conn=pymysql.connect(
    host='localhost',
    user='root',
    passwd='123',
    database='WIFI'
)

cursor=conn.cursor()

sql="SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'locationdata3' AND table_schema = 'WIFI'"
cursor.execute(sql)
dim=cursor.fetchall()[0][0]-4

sql="show columns from locationdata3"
cursor.execute(sql)
macs=cursor.fetchall()
mac_tp=macs[3:-1]
macs_tmp = [item[0] for item in mac_tp]
for k in macs_tmp:
    print(type(k))
    macs=[item[1:].replace('_',':') for item in macs_tmp]

print("start_eval")

modelx=torch.load('./DNN/modelx.pth')
modely=torch.load('./DNN/modely.pth')
modelz=torch.load('./DNN/modelz.pth')

for i,datai in enumerate(jsondatas):
    input_tmp=[-127 for i in range(dim)]
    inputmacs=datai[0]
    inputstr=datai[1]
    for j,maci in enumerate(macs):
        if maci in inputmacs:
            input_tmp[j]=inputstr[inputmacs.index(maci)]
    pre_x=modelx.predict(input_tmp)
    pre_y=modely.predict(input_tmp)
    pre_z=modelz.predict(input_tmp)
    print(f"pre_x:{pre_x} pre_y:{pre_y} pre_z:{pre_z}")
    print(f"lable:{lables[i]}")
    input()


