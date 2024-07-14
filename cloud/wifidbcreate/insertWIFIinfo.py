import pymysql
import random

conn=pymysql.connect(
    host='127.0.0.1',
    user='root',
    passwd='123',
    database='WIFI'
)

cursor=conn.cursor()
sql="SHOW COLUMNS FROM locationdata"
cursor.execute(sql)
colums = [column[0] for column in cursor.fetchall()]
colums =colums[4:]

lclist=['locid','locx','locy','locz']

for i in range (5):
    maclist=random.sample(colums[:10],5)
    sql="insert into locationdata"
    sql=sql+'('
    for k in lclist:
        sql+=k
        sql+=','
    
    for k in maclist:
        sql+=k
        sql+=','
    sql=sql[:-1]
    sql+=') values ('
    
    sql+=str(i+1)
    sql+=f',1,{i+1},4,'
    for k in maclist:
        stren=random.randint(-90,0)
        sql+=str(stren)
        sql+=','
    sql=sql[:-1]
    sql+=')'
    print(sql)
    cursor.execute(sql)
    conn.commit()

cursor.execute("select * from locationdata")
res=cursor.fetchall()
print(res)
cursor.close()
conn.close()

