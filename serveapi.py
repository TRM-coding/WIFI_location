from flask import request, jsonify,Flask
import PermissionController
import wifiInfController
import json
import requests
import navigator
import kNN
import preciseLocation
import pymysql
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
    ans=[{"room":v} for v in road_list]
    # print(ans)
    return jsonify(ans) 
    



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

@app.route("/precise_location",methods=['POST'])
def precise_location():
    pre=preciseLocation.preciseLocation()
    req=request.get_json()
    print (req[0])
    conn=pymysql.connect(
            host='localhost',
            user='root',
            password='123',
            database='WIFI'
    )
    cursor=conn.cursor()
    input=[]
    for jsoni in req:
        wifimacs=jsoni['mac_list']
        wifirssi=jsoni['mac_strength']
        sql="SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'locationdata3' AND table_schema = 'WIFI'"
        cursor.execute(sql)
        dim=cursor.fetchall()[0][0]-4
        input_tmp=[-127 for i in range(dim)]
        sql="show columns from locationdata3"
        cursor.execute(sql)
        macs=cursor.fetchall()
        mac_tp=macs[3:-1]
        macs_tmp = [item[0] for item in mac_tp]
        for k in macs_tmp:
            print(type(k))
        macs=[item[1:].replace('_',':') for item in macs_tmp]
        print(macs)
        print(wifimacs)
        for i in range (dim):
            if macs[i] in wifimacs:
                print(i)
                input_tmp[i]=wifirssi[wifimacs.index(macs[i])]
        input.append(input_tmp)

    print(input)
    x,y,z=pre.predict(input)
    dic={'lx':x,
    'ly':y,
    'lz':z
    }
    return jsonify(dic)





if __name__=="__main__":
    app.run(host='0.0.0.0', port=8080)
