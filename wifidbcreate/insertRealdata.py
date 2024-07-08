import json
import pymysql

conn=pymysql.connect(
    host="localhost",
    user="root",
    passwd="123",
    database='WIFI'
)

cursor=conn.cursor()

with open('401-415.json','r',encoding='utf-8') as file:
    for id,line in enumerate(file):
        data=json.loads(line)
        lx=data['lx']
        ly=data['ly']
        lz=data['lz']
        wifimac=data['wifimac']
        wifistrength=data['wifistrength']
        sql='insert into locationdata(locid,locx,locy,locz,'
        for macs in wifimac:
            tmp='a'+macs
            tmp=tmp.replace(':','_')
            sql+=tmp
            sql+=','
        sql=sql[:-1]
        sql+=f') values ({id},{lx},{ly},{lz},'
        for strs in wifistrength:
            sql+=str(strs)
            sql+=','
        sql=sql[:-1]
        sql+=')'
        print(sql)
        cursor.execute(sql)
        conn.commit()
        sql=""
        sql=f"insert into points(pno,posx,posy,posz,flag) values ({id},{lx},{ly},{lz},0)"
        print(sql)
        # cursor.execute(sql)
        # conn.commit()
