import numpy as np
import json as js
import pymysql

class kNN():
    def __init__(self):
        self.dim=135
        self.k=31
        self.k_=71
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
        # sql='select mac from wifimac2'
        # cursor.execute(sql)
        # results=cursor.fetchall()
        
        sql='show columns from locationdata4'
        cursor.execute(sql)
        results_tp=cursor.fetchall()
        results_tp=results_tp[4:-1]
        self.all_macs=[itme[0] for itme in results_tp]
        # print(self.all_macs)
        # input()

        self.dim=len(self.all_macs)
        sql='select '
        for result in self.all_macs:
            # print(result[0])
            sql+=result+','
        sql=sql[:-1]+' from locationdata4'
        cursor.execute(sql)
        results=cursor.fetchall()
        # 12:69:6c:b9:82:f6
        self.dataSet=results
        self.dataSetSize=len(results)
        
        sql='show columns from locationdata3'
        cursor.execute(sql)
        results_tp=cursor.fetchall()
        results_tp=results_tp[3:-1]
        self.all_macs_=[itme[0] for itme in results_tp]
        
        self.dim_=len(self.all_macs_)
        sql='select '
        for result in self.all_macs_:
            # print(result)
            sql+=result+','
        sql=sql[:-1]+' from locationdata3'
        cursor.execute(sql)
        results=cursor.fetchall()
        # 12:69:6c:b9:82:f6
        self.dataSet_=results
        self.dataSetSize_=len(results)
        
        # sql='select '
        # for result in results_:
        #     sql+='a'+result[0].replace(':','_')+','
        # sql=sql[:-1]+' from locationdata3'
        # cursor.execute(sql)
        # results=cursor.fetchall()
        # self.dataSet_=results
        # self.dataSetSize_=len(results)
        # # print(len(results))
        # cursor.close()
        # conn.close()
    
    def classify(self,input_data):
        # json=js.loads(input_data)
        json=input_data
        wifimacs=json['mac_list'].strip('[]').split(',')
        for wifimac in wifimacs:
            wifimacs[wifimacs.index(wifimac)]=wifimac.strip()
        # print(wifimacs)
        # input()
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
        # for i,wifimac in enumerate(wifimacs,0):
        #     wifimac=wifimac.strip()
        #     # sql='select id from wifimac2 where mac=%s'
        #     # # print("mac:"+'a'+wifimac.replace(':','_'))
        #     # cursor.execute(sql,wifimac)
        #     # # print(wifirssi[i])
        #     # result=cursor.fetchall()
        #     if wifimac in all_macs:

        #     if(not result):
        #         continue
        #     # print(result[0][0])
        #     inX[0][result[0][0]-1]=int(wifirssi[i].strip())
        # print(self.all_macs)
        print()
        for i,wifi_i in enumerate(self.all_macs):
            w_i=wifi_i[1:].replace('_',':')
            # print(w_i)
            # input('next')
            if w_i in wifimacs:
                # print('ss')
                inX[0][i]=wifirssi[wifimacs.index(w_i)]
        # print(inX)


        diffMat=np.tile(inX,(self.dataSetSize,1))-self.dataSet
        sqDiffMat=diffMat**2
        sqDistances=sqDiffMat.sum(axis=1)
        distances=sqDistances**0.5
        sortedDistIndicies=distances.argsort()
        # print(sortedDistIndicies)
        cnt={}
        for i in range(self.k):
            # sql='select locx,locy,locz from locationdata where locid=%s'
            sql='select room from locationdata4 where locid=%s'
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
        # print(cnt)
        cursor.close()
        conn.close()
        return ans
    
    def classify_(self,input_data):
        # json=js.loads(input_data)
        json=input_data
        wifimacs=json['mac_list']
        # for wifimac in wifimacs:
        #     wifimacs[wifimacs.index(wifimac)]=wifimac.strip()
        # print(wifimacs)
        # input()
        # print(json['mac_strength'])
        wifirssi=json['mac_strength']
        
        conn=pymysql.connect(
            host='localhost',
            user='root',
            password='123',
            database='WIFI'
        )
        cursor=conn.cursor()
        inX=np.full((1,self.dim_),-127)
        # for i,wifimac in enumerate(wifimacs,0):
        #     wifimac=wifimac.strip()
        #     # sql='select id from wifimac2 where mac=%s'
        #     # # print("mac:"+'a'+wifimac.replace(':','_'))
        #     # cursor.execute(sql,wifimac)
        #     # # print(wifirssi[i])
        #     # result=cursor.fetchall()
        #     if wifimac in all_macs:

        #     if(not result):
        #         continue
        #     # print(result[0][0])
        #     inX[0][result[0][0]-1]=int(wifirssi[i].strip())
        # print(self.all_macs_)
        # print()
        for i,wifi_i in enumerate(self.all_macs_):
            w_i=wifi_i[1:].replace('_',':')
            # print(w_i)
            # input('next')
            if w_i in wifimacs:
                # print('ss')
                inX[0][i]=wifirssi[wifimacs.index(w_i)]
        # print(inX)


        diffMat=np.tile(inX,(self.dataSetSize_,1))-self.dataSet_
        sqDiffMat=diffMat**2
        sqDistances=sqDiffMat.sum(axis=1)
        distances=sqDistances**0.5
        sortedDistIndicies=distances.argsort()
        # print(sortedDistIndicies)
        cnt={}
        for i in range(self.k_):
            # sql='select locx,locy,locz from locationdata where locid=%s'
            sql='select x,y,z from locationdata3 where locid=%s'
            cursor.execute(sql,sortedDistIndicies[i])
            result=cursor.fetchall()
            cnt[result[0]]=cnt.get(result[0],0)+1
        # ans={'x':result[0][0],'y':result[0][1],'z':result[0][2]}
        maxkey=0
        maxvalue=0
        for key,value in cnt.items():
            if(value>maxvalue):
                maxvalue=value
                maxkey=key
        # ans={'lx':maxkey[0],'ly':maxkey[1],'lz':maxkey[2]}
        # print(cnt)
        print(str(maxkey)+' '+str(maxvalue))
        cursor.close()
        conn.close()
        return maxkey[0],maxkey[1],maxkey[2]