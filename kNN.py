import numpy as np
import json as js
import pymysql

class kNN():
    def __init__(self):
        print('init')

    def build(self):
        conn=pymysql.connect(
            host='localhost',
            user='root',
            password='123',
            database='WIFI'
        )
        cursor=conn.cursor()
        #
        sql='select mac from wifimac'
        cursor.execute(sql)
        results=cursor.fetchall()
        sql='select '
        for result in results:
            sql+='a'+result[0].replace(':','_')+','
        sql=sql[:-1]+' from locationdata'
        cursor.execute(sql)
        results=cursor.fetchall()
        self.dataSet=results
        self.dataSetSize=len(results)
        print(len(results))
        cursor.close()
        conn.close()
    
    def classify(self,input_data):
        # json=js.loads(input_data)
        json=input_data
        wifimacs=json['mac_list']

        wifirssi=json['mac_strength']
        conn=pymysql.connect(
            host='localhost',
            user='root',
            password='123',
            database='WIFI'
        )
        cursor=conn.cursor()
        inX=np.full((1,113),-127)
        for i,wifimac in enumerate(wifimacs,0):
            sql='select id from wifimac where mac=%s'
            print("mac:"+'a'+wifimac.replace(':','_'))
            cursor.execute(sql,wifimac)
            result=cursor.fetchall()
            inX[0][result[0][0]-1]=wifirssi[i]
        diffMat=np.tile(inX,(self.dataSetSize,1))-self.dataSet
        sqDiffMat=diffMat**2
        sqDistances=sqDiffMat.sum(axis=1)
        distances=sqDistances**0.5
        sortedDistIndicies=distances.argsort()
        # print(sortedDistIndicies)
        sql='select locx,locy,locz from locationdata where locid=%s'
        cursor.execute(sql,sortedDistIndicies[0]+1)
        result=cursor.fetchall()
        ans={'x':result[0][0],'y':result[0][1],'z':result[0][2]}
        cursor.close()
        conn.close()
        return ans