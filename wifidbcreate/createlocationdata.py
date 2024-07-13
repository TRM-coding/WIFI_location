import os
import json
import pymysql

def getmacs(data_dir):
    macs=[]
    for file in os.listdir(data_dir):
        if file.endswith('.json'):
            with open(os.path.join(data_dir,file),'r') as f:
                for line in f:
                    data=json.loads(line)
                    wifimac=data['wifimac']
                    macs.extend(wifimac)
    macs=list(set(macs))
    return macs

def create_locationdata(macs):
    sql="create table locationdata4 (locid int,x int,y int,z int,"
    for maci in macs:
        mactmp='a'+maci
        mactmp=mactmp.replace(':','_')
        sql+=mactmp
        sql+=' int default -127,'
    sql=sql[:-1]
    sql+=',room int not null)'
    conn=pymysql.connect(
        host="localhost",
        user='root',
        passwd='123',
        db='WIFI'
    )
    print(sql)
    cur=conn.cursor()
    cur.execute(sql)
    conn.commit()

def insert_locationdata(data_dir):
    id=0
    for file in os.listdir(data_dir):
        if file.endswith(".json"):
            with open(os.path.join(data_dir,file),'r')as f:
                for line in f:
                    id=id+1
                    data=json.loads(line)
                    maclist=data['wifimac']
                    strlist=data['wifistrength']
                    sql='insert into locationdata4 (locid,x,y,z,'
                    for maci in maclist:
                        mactmp='a'+maci
                        mactmp=mactmp.replace(':','_')
                        sql+=mactmp
                        sql+=','

                    x=data['lx']
                    y=data['ly']
                    z=data['lz']
                    sql+=f'room) values ({id},{x},{y},{z},'
                    for stri in strlist:
                        sql+=str(stri)
                        sql+=','
                    filename, ext = os.path.splitext(file)
                    if filename!='444':
                        sql+=f'{filename})'
                    else:
                        sql+=f'{x})'
                    conn=pymysql.connect(
                        host="localhost",
                        user='root',
                        passwd='123',
                        db='WIFI'
                    )   
                    print(sql)
                    cur=conn.cursor()
                    cur.execute(sql)
                    conn.commit()

def insert_wifimac(maclist):
    for id,maci in enumerate(maclist,1):
        sql=f"insert into wifimac2 (mac,id) values('{maci}',{id})"
        conn=pymysql.connect(
            host="localhost",
            user='root',
            passwd='123',
            db='WIFI'
        )   
        print(sql)
        cur=conn.cursor()
        cur.execute(sql)
        conn.commit()

    


if __name__ =='__main__':
    maclist=getmacs('./wifidbcreate/datas')
    # print(maclist)
    create_locationdata(maclist)
    insert_locationdata('./wifidbcreate/datas')
    # insert_wifimac(maclist)
    print(len(maclist))


# ALTER TABLE locationdata2 ADD COLUMN locid INT AUTO_INCREMENT PRIMARY KEY;

# sql='show columns from locationdata4'
# cursor.excute(sql)
# res=cursor.fetchall()
# res=res[4:-1]