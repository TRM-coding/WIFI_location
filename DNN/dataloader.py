import random
import pymysql
import numpy as np
class dataloader:
    def __init__(self,traindata_rate):
        self.traindata_rate=traindata_rate
        self.input=list()
        self.label=list()
        conn=pymysql.connect(
            host='localhost',
            user='root',
            passwd='123',
            database='WIFI'
        )
        self.rate=-0.005
        cursor=conn.cursor()
        sql='select * from locationdata3'
        cursor.execute(sql)
        datas=cursor.fetchall()
        # print(datas)
        # input()
        for datai in datas:
            input_tmp=[]
            for item in datai[3:-1]:
                input_tmp.append(item*self.rate)
            lable_tmp=(datai[0],datai[1],datai[2])

            # mean = np.mean(input_tmp)  # 计算平均值
            # std = np.std(input_tmp)  # 计算标准差
            # ls = [(x - mean) / std for x in input_tmp]

            # print(lable_tmp)
            
            for _ in range(15):
                noise = np.random.uniform(-0.02, 0.02, len(input_tmp))  # 生成噪声
                noisy_data = [x + n for x, n in zip(input_tmp, noise)]  # 将噪声添加到数据中
                self.input.append(tuple(noisy_data))  # 将带有噪声的数据添加到输入中
                self.label.append(lable_tmp)  # 对于每个带有噪声的数据，我们使用相同的标签
        paired = list(zip(self.input, self.label))
        random.shuffle(paired)
        self.input, self.label = map(list,zip(*paired))

        
    def train_load(self):
        Len=len(self.input)
        return self.input[:int(Len*self.traindata_rate)],self.label[:int(Len*self.traindata_rate)]

    def test_load(self):
        Len=len(self.input)
        return self.input[int(Len*self.traindata_rate):],self.label[int(Len*self.traindata_rate):]


