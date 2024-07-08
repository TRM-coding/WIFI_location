import pymysql
import torch
from modelscope import AutoModel,AutoTokenizer,snapshot_download
import pymysql
from flask import jsonify
class LLMkernel:
    def __init__(self,llm,tokenizer):
        self.LLM=llm
        self.tokenizer=tokenizer
    
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
        print("正在响应")

        template=f"""你是一个经验丰富的图书检索助手。
        我们的图书馆中有这些书：{booklist}
        现在用户提出这样的检索需求：{content}
        请你根据用户的检索需求，在图书馆中找寻符合用户要求的所有书籍返回给用户
        如果没有找到相关的书籍，请你告诉用户：图书馆中没有这样的书。
        除此之外，不需要你返回其他任何信息"""
        
        result,history=self.LLM.chat(self.tokenizer,template,history=[],temperature=temp)
        return result
    

    def chat(self,json_data):
        prompt=json_data['prompt']
        temp=json_data['temperature']
        temp=float(temp)
        print("正在响应...")
        print(temp)
        result,history=self.LLM.chat(self.tokenizer,prompt,history=[],temperature=temp)
        return result