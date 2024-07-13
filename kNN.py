import numpy as np
import json as js
import pymysql

class kNN():
    def __init__(self):
        self.dim=135
        self.k=31
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
        sql='select mac from wifimac2'
        cursor.execute(sql)
        results=cursor.fetchall()
        self.dim=len(results)
        sql='select '
        for result in results:
            sql+='a'+result[0].replace(':','_')+','
        sql=sql[:-1]+' from locationdata2'
        cursor.execute(sql)
        results=cursor.fetchall()
        self.dataSet=results
        self.dataSetSize=len(results)
        
        sql='select '
        for result in results:
            sql+='a'+result[0].replace(':','_')+','
        sql=sql[:-1]+' from locationdata3'
        cursor.execute(sql)
        results=cursor.fetchall()
        self.dataSet_=results
        self.dataSetSize_=len(results)
        # print(len(results))
        cursor.close()
        conn.close()
    
    def classify(self,input_data):
        # json=js.loads(input_data)
        json=input_data
        wifimacs=json['mac_list'].strip('[]').split(',')
        # print(json['mac_strength'])
        wifirssi=json['mac_strength'].strip('[]').split(',')
        
        conn=pymysql.connect(
            host='localhost',
            user='root',
            password='123',
            database='WIFI'
        )
        cursor=conn.cursor()
        inX=np.full((1,self.dim),-127)
        for i,wifimac in enumerate(wifimacs,0):
            wifimac=wifimac.strip()
            sql='select id from wifimac2 where mac=%s'
            # print("mac:"+'a'+wifimac.replace(':','_'))
            cursor.execute(sql,wifimac)
            # print(wifirssi[i])
            result=cursor.fetchall()
            if(not result):
                continue
            # print(result[0][0])
            inX[0][result[0][0]-1]=int(wifirssi[i].strip())
        diffMat=np.tile(inX,(self.dataSetSize,1))-self.dataSet
        sqDiffMat=diffMat**2
        sqDistances=sqDiffMat.sum(axis=1)
        distances=sqDistances**0.5
        sortedDistIndicies=distances.argsort()
        # print(sortedDistIndicies)
        cnt={}
        for i in range(self.k):
            # sql='select locx,locy,locz from locationdata where locid=%s'
            sql='select room from locationdata2 where locid=%s'
            cursor.execute(sql,sortedDistIndicies[i])
            result=cursor.fetchall()
            cnt[result[0][0]]=cnt.get(result[0][0],0)+1
        # ans={'x':result[0][0],'y':result[0][1],'z':result[0][2]}
        maxkey=0
        maxvalue=0
        for key,value in cnt.items():
            if(value>maxvalue):
                maxvalue=value
                maxkey=key
        ans={'room':maxkey}
        print(cnt)
        cursor.close()
        conn.close()
        return ans
    
    def classify_(self,input_data):
        # json=js.loads(input_data)
        json=input_data
        wifimacs=json['mac_list'].strip('[]').split(',')
        # print(json['mac_strength'])
        wifirssi=json['mac_strength'].strip('[]').split(',')
        
        conn=pymysql.connect(
            host='localhost',
            user='root',
            password='123',
            database='WIFI'
        )
        cursor=conn.cursor()
        inX=np.full((1,self.dim),-127)
        for i,wifimac in enumerate(wifimacs,0):
            wifimac=wifimac.strip()
            sql='select id from wifimac2 where mac=%s'
            # print("mac:"+'a'+wifimac.replace(':','_'))
            cursor.execute(sql,wifimac)
            # print(wifirssi[i])
            result=cursor.fetchall()
            if(not result):
                continue
            # print(result[0][0])
            inX[0][result[0][0]-1]=int(wifirssi[i].strip())
        diffMat=np.tile(inX,(self.dataSetSize_,1))-self.dataSet_
        sqDiffMat=diffMat**2
        sqDistances=sqDiffMat.sum(axis=1)
        distances=sqDistances**0.5
        sortedDistIndicies=distances.argsort()
        # print(sortedDistIndicies)
        cnt={}
        for i in range(self.k):
            # sql='select locx,locy,locz from locationdata where locid=%s'
            sql='select room from locationdata3 where locid=%s'
            cursor.execute(sql,sortedDistIndicies[i])
            result=cursor.fetchall()
            cnt[result[0][0]]=cnt.get(result[0][0],0)+1
        # ans={'x':result[0][0],'y':result[0][1],'z':result[0][2]}
        maxkey=0
        maxvalue=0
        for key,value in cnt.items():
            if(value>maxvalue):
                maxvalue=value
                maxkey=key
        ans={'location':maxkey}
        print(cnt)
        cursor.close()
        conn.close()
        return ans