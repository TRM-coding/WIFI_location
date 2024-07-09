import json
import pymysql
class dataloader:
    def __init__(self,data_dir):
        self.data_dir=data_dir
        self.datas=[]
        conn=pymysql.connect(
            host='localhost',
            user='root',
            passwd='123'
        )
        cursor=conn.cursor()
        sql='select * from locationdata2'
        cursor.execute(sql)
        datas=cursor.fetchall()
        
