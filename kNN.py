import numpy as np
import json as js
import pymysql

class kNN():
    def __init__(self):
        print('init')
    def classify(self,input_data):
        json=js.loads(input_data)
        wifimacs=json['wifimac']
        wifirssi=json['wifistrength']
        inX=np.full((1,113),-127)
        conn=pymysql.connect(
            host='localhost',
            user='root',
            password='rhj123456',
            database='LP'
        )
        cursor=conn.cursor()
        for i,wifimac in enumerate(wifimacs,0):
            sql='select id from wifimac where mac=%s'
            cursor.execute(sql,wifimac)
            result=cursor.fetchall()
            inX[0][result[0][0]-1]=wifirssi[i]
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
        dataSetSize=len(results)
        diffMat=np.tile(inX,(dataSetSize,1))-results
        sqDiffMat=diffMat**2
        sqDistances=sqDiffMat.sum(axis=1)
        distances=sqDistances**0.5
        sortedDistIndicies=distances.argsort()
        # print(sortedDistIndicies)
        sql='select locx,locy,locz from locationdata where locid=%s'
        cursor.execute(sql,sortedDistIndicies[0]+1)
        result=cursor.fetchall()
        ans={'lx':result[0][0],'ly':result[0][1],'lz':result[0][2]}
        return js.dumps(ans)