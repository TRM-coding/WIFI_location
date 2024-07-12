from flask import request, jsonify,Flask
import PermissionController
import wifiInfController
import json
import requests
import navigator
import kNN
app=Flask(__name__)

navi=navigator.navigator()
navi.build()


@app.route('/postWifiInfor', methods=['POST'])
def getWifiInfor():
    data = request.get_json()
    print(data)
    return jsonify(data)

@app.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    print(data)
    controller = PermissionController.PermissionController(data)
    result = controller.Login()
    if(result):
        result="1"
    else:
        result="0"
    data = {"result": result}
    
    return jsonify(data)
    


@app.route('/register', methods=['POST'])
def register():
    data = request.get_json()
    controller = PermissionController.PermissionController(data)
    result = controller.Register()
    if(result):
        result="1"
    else:
        result="0"
    data = {"result": result}
    return jsonify(data)

@app.route("/chat",methods=['POST'])
def chat():
    data=request.get_json()
    prompt=data['msg']
    temp=data['temperature']
    url="http://127.0.0.1:5000/kernel_chat"
    json_dt={
        "prompt":prompt,
        "temperature":temp
    }
    json_obj=json.dumps(json_dt)
    headers = {'Content-Type': 'application/json'}
    respond=requests.post(url,data=json_obj,headers=headers) 
    # respond=respond['respond']
    # respond={"respond":respond}
    print(respond.json)
    return jsonify(respond.json())

@app.route("/book",methods=["POST"])
def book():
    data=request.get_json()
    prompt=data['msg']
    temp=data['temperature']
    url="http://127.0.0.1:5000/getbooks"
    json_dt={
        "prompt":prompt,
        "temperature":temp
    }
    json_obj=json.dumps(json_dt)
    headers = {'Content-Type': 'application/json'}
    respond=requests.post(url,data=json_obj,headers=headers) 
    
    return jsonify(respond.json())

@app.route("/navigate",methods=["POST"])
def navigate():
    req=request.get_json()
    # book_id=req['bookid']
    # x=req['sx']
    # y=req['sy']
    # z=req['sz']
    road_list=navi.spfa(req)
    # print(road_list)
    ans={str(i): v for i, v in enumerate(road_list)}
    # print(ans)
    return jsonify(road_list) 
    



@app.route("/location",methods=["POST"])
def location():
    req=request.get_json()
    # req=json.loads(jsonify(req))
    # mac_list=req['mac_list']
    # mac_strength=req['mac_strength']
    knn=kNN.kNN()
    knn.build()
    res=knn.classify(req)
    print(res)
    return jsonify(res)



if __name__=="__main__":
    app.run(host='0.0.0.0', port=8080)
