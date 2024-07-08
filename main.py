import navigator
import json
import kNN
    
if __name__=='__main__':
    dict={"sx":1,"sy":1,"sz":4,"bookid":3}
    # dict={"wifimac":["12:69:6c:d6:9d:7b","12:69:6c:d6:9d:7c","12:69:6c:d6:a4:c8","12:69:6c:d6:a4:c7","12:69:6c:d4:3c:56","12:69:6c:d6:a5:48","12:69:6c:d6:a2:f7","12:69:6c:d4:3c:57","12:69:6c:d6:9e:1f","12:69:6c:d6:a3:e4","12:69:6c:d6:8d:97","12:69:6c:b9:73:0c","12:69:6c:bd:e1:2b","12:69:6c:d6:9e:20","12:69:6c:d4:38:3f"],"wifistrength":[-44,-53,-56,-56,-63,-64,-67,-68,-70,-71,-72,-73,-76,-78,-78]}
    input_data=json.dumps(dict)
    navi=navigator.navigator()
    navi.build()
    result=navi.spfa(input_data)
    print(result)
    
    # knn=kNN.kNN()
    # result=knn.classify(input_data)
    # print(result)