import torch
from collections import Counter

class preciseLocation:
    def __init__(self):
        self.modelx=torch.load('./DNN/modelx.pth')
        self.modelx.eval()

        self.modely=torch.load('./DNN/modely.pth')
        self.modely.eval()

        self.modelz=torch.load('./DNN/modelz.pth')
        self.modelz.eval()

    def most_common(self,lis):
        data = Counter(lis)
        return data.most_common(1)[0][0]
    
    def predict(self,data_list):
        print('start_predict')
        x_pre=[]
        y_pre=[]
        z_pre=[]
        for i in data_list:
            resx=self.modelx.predict(i)
            x_pre.append(resx)
            resy=self.modely.predict(i)
            y_pre.append(resy)
            resz=self.modelz.predict(i)
            z_pre.append(resz)
        print(x_pre)
        print(y_pre)
        print(z_pre)
        x_predict=self.most_common(x_pre)
        y_predict=self.most_common(y_pre)
        z_predict=self.most_common(z_pre)
        print("res")
        print('x'+str(x_predict)+" y:"+str(y_predict)+" z:"+str(z_predict))
        return x_predict,y_predict,z_predict

