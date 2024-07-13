import json
import pymysql

conn=pymysql.connect(
    host='localhost',
    user='root',
    password='123',
    database='WIFI'
)
cursor=conn.cursor()
# sql='insert into edges values(415,0),(414,2),(413,3),(409,7),(406,8),(408,8)'
# sql='select locid from locationdata2'
# sql='show columns from locationdata2'
# cursor.execute(sql)
# results=cursor.fetchall()
# print(results)
# cursor.close()
# conn.commit()
# conn.close()
wifimac_dict={}
counter=562
# # sql='create table wifimac(mac varchar(20),id int,primary key(id))'
# # cursor.execute(sql)
# # print(sql)
# sql='insert into wifimac values'

with open('./wifidbcreate/corridor.json', 'r', encoding='utf-8') as file:
    for line_number, line in enumerate(file, 1):
        try:
            json_object = json.loads(line)
            sql='insert into locationdata2(locid,'
            # print(json_object['wifimac'])
            for wifimac in json_object['wifimac']:
                if(wifimac_dict.get(wifimac,0)):
                    continue
                wifimac_dict[wifimac]=1
                sql+='a'+wifimac.replace(':','_')+','
                # sql+="('"+wifimac+"',"+str(counter)+'),'
                # cursor.execute(sql,(wifimac,str(counter)))
                # print(counter)
                counter+=1
            # sql=sql[:-1]
            sql+='room) values ('+str(counter)+','+str(json_object['lx'])+','+str(json_object['ly'])+','+str(json_object['lz'])+','
            for rssi in json_object['wifistrength']:
                sql+=str(rssi)+','
            sql=sql[:-1]+str(json_object['lx'])+')'
            print(sql)
            cursor.execute(sql)
            counter+=1
        except json.JSONDecodeError as e:
            # 如果解析失败，打印错误信息
            print(f"行 {line_number} 解析错误: {e}")
# print(len(wifimac_dict))
# sql+='primary key(locid))'
# sql='select * from locationdata2'
# print(sql)
# cursor.execute(sql)
results=cursor.fetchall()
print(results)
# conn.commit()

cursor.close()
conn.close()