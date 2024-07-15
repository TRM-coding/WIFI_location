import pymysql
import json as js
from collections import defaultdict
from ast import literal_eval

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
        sql='select * from edges'
        cursor.execute(sql)
        results=cursor.fetchall()
        cursor.close()
        conn.close()
        self.dic=defaultdict(list)
        # self.posdic={}
        for result in results:
            self.dic[result[0]].append(result[1])
            self.dic[result[1]].append(result[0])
            # self.posdic[result[0]]=(result[1],result[2],result[3],result[4])
        # print(self.dic)
            
    def spfa(self,input_data):
        # json=js.loads(input_data)
        json=input_data
        print(json)
        s=(int(json['room']))
        conn=pymysql.connect(
            host='localhost',
            user='root',
            password='123',
            database='WIFI'
        )
        cursor=conn.cursor()
        # sql='select bookx,booky,bookz from books where bookid=%s'
        sql='select roomid from books where ID=%s'
        cursor.execute(sql,(json['book_id']))
        results=cursor.fetchall()
        # print(results)
        # results= literal_eval(results[0][0])

        cursor.close()
        conn.close()
        t=int(results[0][0])
        # dir=[[1,0],[-1,0],[0,-1],[0,1]]
        q=[]
        vis={}
        dist={}
        pre={}
        q.append(s)
        vis[s]=True
        dist[s]=0
        pre[s]=-1
        while(q):
            now=q[0]
            # print(now)
            q=q[1:]
            vis[now]=False
            for nxt in self.dic[now]:
                if(dist.get(nxt,1e9)>dist[now]+1):
                    dist[nxt]=dist[now]+1
                    pre[nxt]=now
                    if(vis.get(nxt,False)):
                        continue
                    vis[nxt]=True
                    q.append(nxt)
            # for i in range(4):
            #     newx=now[0]+dir[i][0]
            #     newy=now[1]+dir[i][1]
            #     new=(newx,newy,now[2])
            #     if(self.dic.get(new,-1)==-1):
            #         continue
            #     if(dist.get(new,1e9)>dist[now]+1):
            #         dist[new]=dist[now]+1
            #         pre[new]=now
            #         if(vis.get(new,False)):
            #             continue
            #         vis[new]=True
            #         q.append(new)
            # if(self.dic[now]):
            #     for i in range(1,7):
            #         new=(now[0],now[1],i)
            #         if(self.dic.get(new,-1)==-1):
            #             continue
            #         if(dist.get(new,1e9)>dist[now]+1):
            #             dist[new]=dist[now]+1
            #             pre[new]=now
            #             if(vis.get(new,False)):
            #                 continue
            #             vis[new]=True
            #             q.append(new)
        road=[]
        if(t==s):
            print("t==s")
            return road
        t=pre[t]
        while(t!=s):
            road.append(t)
            t=pre[t]
        # road.append(s)
        road.reverse()
        print(road)
        # ans={str(i): v for i, v in enumerate(road)}
        return road
