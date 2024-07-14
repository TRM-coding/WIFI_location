import json
import pymysql
class PermissionController:
    def __init__(self,json_data):
        self.json_data=json_data

    def Login(self):
        conn=pymysql.connect(
            host='localhost',
            user='root',
            password='123',
            database='WIFI')
        cursor=conn.cursor()
        username=self.json_data['phone']
        password=self.json_data['password']
        sql="select * from User where phone=%s and password=%s"
        cursor.execute(sql,(username,password))
        result=cursor.fetchall()
        cursor.close()
        conn.close()
        print(result)
        if len(result)==0:
            print(0)
            return False
        else:
            print(1)
            return True

    def Register(self):
        conn=pymysql.connect(
            host='localhost',
            user='root',
            password='123',
            database='WIFI')
        cursor=conn.cursor()
        username=self.json_data['phone']
        password=self.json_data['password']
        sql="select * from User where phone=%s"
        cursor.execute(sql,(username))
        result=cursor.fetchall()
        if len(result)!=0:
            cursor.close()
            conn.close()
            return False
        sql="insert into User(phone,password) values(%s,%s)"
        cursor.execute(sql,(username,password))
        conn.commit()
        cursor.close()
        conn.close()
        return True