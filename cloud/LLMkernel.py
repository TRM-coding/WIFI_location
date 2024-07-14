import pymysql
import torch
from modelscope import AutoModel,AutoTokenizer,snapshot_download
import pymysql
from flask import jsonify



class LLMkernel:
    def __init__(self,llm,tokenizer):
        self.LLM=llm
        self.tokenizer=tokenizer
        # self.history_chat=history_chat
    
    def getBooks(self,json_data):
        content=json_data['prompt']
        temp=json_data['temperature']
        temp=float(temp)
        conn=pymysql.connect(
            host="localhost",
            user="root",
            passwd="123",
            database="WIFI"
        )
        cursor=conn.cursor()
        sql="select Title from books"
        cursor.execute(sql)
        booklist=cursor.fetchall()
        booklist=str(booklist)
        cursor.execute("select ID from books")
        bookid=cursor.fetchall()
        bookid=str(bookid)
        cursor.execute("select roomid from books")
        booklc=cursor.fetchall()
        booklc=[item[0] for item in booklc]
        booklc=str(booklc)
        print(booklc)
        print("正在响应")

        template=f"""你是一个经验丰富的图书检索助手。
        我们的图书馆中有这些书：{booklist},和这些书对应的id：{bookid},和这些书按照出现顺序对应的房间号：{booklc}
        现在用户提出这样的检索需求：{content}
        请你根据用户的检索需求，在图书馆中找寻符合用户要求的所有书籍用下面这种格式返回给用户：
        [
            {{
                "id": 1,
                "name": "Market A",
                "room":401
            }},
            {{
                "id": 2,
                "name": "Market B",
                "room":401
            }},
            {{
                "id": 3,
                "name": "Market C",
                "room":401
            }},
            {{
                "id": 4,
                "name": "Market D",
                "room":401
            }},
        ]
        请注意，请你严格按照列表中的顺序查找书的对应房间号。并且不要输出换行符
        如果没有找到相关的书籍，请你返回一个空的列表。
        除此之外，不需要你返回其他任何信息"""
        
        result,history=self.LLM.chat(self.tokenizer,template,history=[],temperature=temp)
        return result
    

    def chat(self,json_data):
        prompt=json_data['prompt']
        temp=json_data['temperature']
        temp=float(temp)
        print("正在响应...")
        print(temp)
        result,history=self.LLM.chat(self.tokenizer,prompt,temperature=temp)
        return result