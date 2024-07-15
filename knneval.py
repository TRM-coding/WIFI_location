import torch
from torch import nn
import json
import pymysql
import matplotlib.pyplot as plt
import numpy as np
import matplotlib.cm as cm
import random
import kNN
jsondatas=[]
lables=[]
torch.cuda.empty_cache()
with open('./DNN/test.json','r')as file:
    for line in file:
        datai=json.loads(line)
        mac=datai['wifimac']
        stre=datai['wifistrength']
        jsondatas.append([mac,stre])
        x=datai['lx']
        y=datai['ly']
        z=datai['lz']
        print((x,y,z))
        lables.append((x,y,z))

print(len(lables))

conn=pymysql.connect(
    host='localhost',
    user='root',
    passwd='123',
    database='WIFI'
)

cursor=conn.cursor()

sql="SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'locationdata3' AND table_schema = 'WIFI'"
cursor.execute(sql)
dim=cursor.fetchall()[0][0]-5

sql="show columns from locationdata3"
cursor.execute(sql)
macs=cursor.fetchall()
mac_tp=macs[3:-2]
macs_tmp = [item[0] for item in mac_tp]
for k in macs_tmp:
    print(type(k))
    macs=[item[1:].replace('_',':') for item in macs_tmp]

print("start_eval")


knn=kNN.kNN()
knn.build()
# knn.classify_()

# fig, ax = plt.subplots()
resdict={}

for i,datai in enumerate(jsondatas):
    jsondic={"mac_list":datai[0],"mac_strength":datai[1]}
    pre_x,pre_y,pre_z=knn.classify_(jsondic)
    
    print(f"pre_x:{pre_x} pre_y:{pre_y} pre_z:{pre_z}")
    print(f"lable:{lables[i]}")
    if lables[i] not in resdict:
        resdict[lables[i]] = []
    resdict[lables[i]].append((pre_x,pre_y))



import numpy as np
import matplotlib.pyplot as plt

# 计算CDF
def calculate_cdf(data):
    n = len(data)
    x = np.sort(data)
    y = np.arange(1, n+1) / n
    return x, y

# 为每个测试点绘制CDF曲线
plt.figure(figsize=(15, 10))
cnt=1

for label, predictions in resdict.items():
    cnt=cnt+1
    # 提取预测值
    pre_x_values = [pre[0] for pre in predictions]
    pre_y_values = [pre[1] for pre in predictions]
    # pre_z_values = [pre[2] for pre in predictions]
    label_x_values = [label[0]] * len(pre_x_values)
    label_y_values = [label[1]] * len(pre_y_values)

    predictions = np.array([pre_x_values, pre_y_values])  # 预测值
    labels = np.array([label_x_values, label_y_values])  # 真实值

    # 计算误差
    errors = np.abs(predictions - labels)

    # 计算最大误差
    max_error = np.max(errors)

    # 计算平均误差
    mean_error = np.mean(errors)

    print(f'({label_x_values[0]},{label_y_values[0]})')
    print(f"最大误差: {max_error}")
    print(f"平均误差: {mean_error}")

    print()

    # 计算CDF
    x_pre_x, y_pre_x = calculate_cdf(pre_x_values)
    x_pre_y, y_pre_y = calculate_cdf(pre_y_values)
    # x_pre_z, y_pre_z = calculate_cdf(pre_z_values)

    # 绘制CDF曲线
    plt.plot(x_pre_x, y_pre_x, label=f'{(label[0],label[1])} pre_x')
    plt.plot(x_pre_y, y_pre_y, label=f'{(label[0],label[1])} pre_y')
    # plt.plot(x_pre_z, y_pre_z, label=f'{label} pre_z')

    if cnt%2==0:
        
        plt.xlabel('Predicted Value')
        plt.ylabel('CDF')
        plt.title('CDF of Predicted Values for Each Test Point')
        plt.legend(bbox_to_anchor=(1.05, 1), loc='upper left') 
        plt.grid(True)
        plt.tight_layout() 
        plt.show()
        plt.savefig(f'./knncdf/output_{cnt}.png')
        plt.figure(figsize=(15, 10))

print(len(resdict))


# import matplotlib.pyplot as plt
# import numpy as np
# import matplotlib.cm as cm

# # 定义颜色和标记样式
# colors = cm.rainbow(np.linspace(0, 1, len(resdict)))
# markers = ['o', '^']

# # 遍历resdict中的每一个键
# for i, key in enumerate(resdict):
#     # 创建一个新的figure
#     fig, ax = plt.subplots()
#     ax.set_title(f"KNN:{key}")

#     # 设置x和y轴的范围
#     ax.set_xlim([-0.2, 4.2])
#     ax.set_ylim([-0.2, 3.2])

#     # 画出键的位置，用大圆点表示
#     ax.scatter(key[0], key[1], color=colors[i], marker=markers[0], s=100)

#     # 遍历键对应的列表，画出每个点的位置，用小三角形表示
#     for point in resdict[key]:
#         offset_x = random.uniform(-0.5, 0.5)
#         offset_y = random.uniform(-0.5, 0.5)
#         ax.scatter(point[0]+offset_x, point[1]+offset_y, color=colors[i], marker=markers[1], s=50)

#     # 保存图片到当前目录，文件名为'output_key.png'
#     print(i,end='')
#     plt.savefig(f'./knnfigs/output_{key}.png')

#     plt.close(fig)  # 关闭figure，释放内存






