import torch
from modelscope import AutoModel,AutoTokenizer,snapshot_download
import pymysql

from flask import Flask,request,jsonify
from LLMkernel import LLMkernel

app=Flask(__name__)
model_dir=snapshot_download("ZhipuAI/glm-4-9b-chat",cache_dir='./glm4')
with torch.no_grad():
    LLM=AutoModel.from_pretrained(model_dir,trust_remote_code=True).half().cuda()
tokenizer=AutoTokenizer.from_pretrained(model_dir,trust_remote_code=True)
LLM=LLM.eval()

@app.route("/kernel_chat",methods=["POST"])
def predict():
    json_data=request.get_json()
    print(json_data)
    kernel=LLMkernel(LLM,tokenizer)
    result=kernel.chat(json_data)
    print(result)
    # result=str(result)
    result={"respond":result}
    print(result)
    return jsonify(result)


@app.route("/getbooks",methods=["POST"])
def getBooks():
    json_data=request.get_json()
    print(json_data)
    kernel=LLMkernel(LLM,tokenizer)
    result=kernel.getBooks(json_data)
    result={"respond":result}
    print(result)
    return jsonify(result)


if __name__=="__main__":
    app.run(host="0.0.0.0",port=5000)

# if __name__=="__main__":
#     predict()
#     print(torch.__version__)
# /root/.cache/modelscope/hub/._____temp/ZhipuAI/