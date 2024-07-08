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
    print(respond)
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
    print(road_list)
    ans={str(i): v for i, v in enumerate(road_list)}
    return jsonify(ans) 
    



@app.route("/location",methods=["POST"])
def location():
    req=request.get_json()
    mac_list=req['mac_list']
    mac_strength=req['mac_strength']
    knn=kNN






# @app.route('getRoad', methods=['POST'])
# def getRoad():
#     data = request.get_json()
#     controller = RoadCalculater.RoadCalculater(data)
#     result = controller.get_road()
#     json_data={i:v for i,v in enumerate(result)}
#     return jsonify(json_data)

# @app.route('addWIFIinf', methods=['POST'])
# def addWIFIinf():
#     data = request.get_json()
#     controller = wifiInfController.wifiInfController()
#     result = controller.insertLocalArgs(data)
#     return jsonify(result)


if __name__=="__main__":
    app.run(host='0.0.0.0', port=8080)