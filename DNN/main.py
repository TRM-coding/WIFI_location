import DNN
import dataloader
import torch
from torch import nn


if __name__ =='__main__':
    dr=dataloader.dataloader(0.95)
    train_input,train_lable=dr.train_load()

    lr=0.000000001
    ep=5000

    modelx=DNN.DNN()
    lable_x=[tp[0] for tp in train_lable]
    modelx.load_train_data(train_input,lable_x)
    modelx.init()
    modelx.train()
    modelx.Train(ep,lr)
    modelx.eval()

    modely=DNN.DNN()
    lable_y=[tp[1] for tp in train_lable]
    modely.load_train_data(train_input,lable_y)
    modely.init()
    modely.train()
    modely.Train(ep,lr)
    modely.eval()

    modelz=DNN.DNN()
    lable_z=[tp[2] for tp in train_lable]
    modelz.load_train_data(train_input,lable_z)
    modelz.init()
    modelz.train()
    modelz.Train(ep,lr)
    modelz.eval()

    eval_input,eval_label=dr.test_load()
    


    print("start_predict")

    for i in range(len(eval_input)):
        input()
        print(eval_input[i])
        resx = nn.functional.softmax(modelx(torch.tensor(eval_input[i],dtype=torch.float32).to(modelx.device)),dim=-1)
        resy = nn.functional.softmax(modely(torch.tensor(eval_input[i],dtype=torch.float32).to(modely.device)),dim=-1)
        resz = nn.functional.softmax(modelz(torch.tensor(eval_input[i],dtype=torch.float32).to(modelz.device)),dim=-1)

        # 获取预测类别
        pred_class_x = torch.argmax(resx).item()
        pred_class_y = torch.argmax(resy).item()
        pred_class_z = torch.argmax(resz).item()

        print(f"Predicted class for x: {pred_class_x}")
        print(f"Predicted class for y: {pred_class_y}")
        print(f"Predicted class for z: {pred_class_z}")

        print(eval_label[i])
