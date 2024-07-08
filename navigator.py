import pymysql
import json as js

class navigator:
    def __init__(self):
        print('init')
        # print(self.json)
    
    def build(self):
        conn=pymysql.connect(
            host='localhost',
            user='root',
            password='123',
            database='WIFI'
        )
        cursor=conn.cursor()
        sql='select * from points'
        cursor.execute(sql)
        results=cursor.fetchall()
        cursor.close()
        conn.close()
        self.dic={}
        # self.posdic={}
        for result in results:
            self.dic[(result[1],result[2],result[3])]=result[4]
            # self.posdic[result[0]]=(result[1],result[2],result[3],result[4])
            
    def spfa(self,input_data):
        # json=js.loads(input_data)
        json=input_data
        s=(json['sx'],json['sy'],json['sz'])
        conn=pymysql.connect(
            host='localhost',
            user='root',
            password='123',
            database='WIFI'
        )
        cursor=conn.cursor()
        # sql='select bookx,booky,bookz from books where bookid=%s'
        sql='select Location from books where ID=%s'
        cursor.execute(sql,(json['bookid']))
        results=cursor.fetchall()
        print(results)
        from ast import literal_eval
        results= literal_eval(results[0][0])

        cursor.close()
        conn.close()
        t=results
        dir=[[1,0],[-1,0],[0,-1],[0,1]]
        q=[]
        vis={}
        dist={}
        pre={}
        q.append(s)
        vis[s]=True
        dist[s]=0
        pre[s]=(-1,-1,-1)
        while(q):
            now=q[0]
            # print(now)
            q.pop()
            vis[now]=False
            for i in range(4):
                newx=now[0]+dir[i][0]
                newy=now[1]+dir[i][1]
                new=(newx,newy,now[2])
                if(self.dic.get(new,-1)==-1):
                    continue
                if(dist.get(new,1e9)>dist[now]+1):
                    dist[new]=dist[now]+1
                    pre[new]=now
                    if(vis.get(new,False)):
                        continue
                    vis[new]=True
                    q.append(new)
            if(self.dic[now]):
                for i in range(1,7):
                    new=(now[0],now[1],i)
                    if(self.dic.get(new,-1)==-1):
                        continue
                    if(dist.get(new,1e9)>dist[now]+1):
                        dist[new]=dist[now]+1
                        pre[new]=now
                        if(vis.get(new,False)):
                            continue
                        vis[new]=True
                        q.append(new)
        road=[]
        while(t!=s):
            road.append(t)
            t=pre[t]
        road.append(s)
        road.reverse()
        # ans={str(i): v for i, v in enumerate(road)}
        return road