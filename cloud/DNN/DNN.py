import torch
from torch import nn
import matplotlib.pyplot as plt


class DNN(nn.Module):

    

    def load_train_data(self,train_input,train_lable):
        self.train_data=torch.tensor(train_input,dtype=torch.float32).to(self.device)
        self.train_lable=torch.tensor(train_lable,dtype=torch.float32).to(self.device)
        self.train_lable=self.train_lable.long()


    def __init__(self):
        super(DNN, self).__init__()
        self.device= torch.device('cuda:0' if torch.cuda.is_available() else 'cpu')
        self.relu = nn.LeakyReLU(negative_slope=0.5)

        nums_input=42
        nums_output=5
        nums_hiddens1=1024
        nums_hiddens2=512
        nums_hiddens3=256
        nums_hiddens4=128
        nums_hiddens5=64
        nums_hiddens6=32




        # sigma=0.23
        sigma=0.3
        
        # self.w1 = nn.Parameter(torch.randn(nums_input, nums_hiddens1, requires_grad=True) * sigma)
        # self.b1 = nn.Parameter(torch.randn(nums_hiddens1, requires_grad=True) * sigma)

        # self.w2 = nn.Parameter(torch.randn(nums_hiddens1, nums_hiddens1, requires_grad=True) * sigma)
        # self.b2 = nn.Parameter(torch.randn(nums_hiddens1, requires_grad=True) * sigma)

        # self.w3 = nn.Parameter(torch.randn(nums_hiddens1, nums_hiddens2, requires_grad=True) * sigma)
        # self.b3 = nn.Parameter(torch.randn(nums_hiddens2, requires_grad=True) * sigma)

        # self.w4 = nn.Parameter(torch.randn(nums_hiddens2, nums_hiddens2, requires_grad=True) * sigma)
        # self.b4 = nn.Parameter(torch.randn(nums_hiddens2, requires_grad=True) * sigma)

        # self.w5 = nn.Parameter(torch.randn(nums_hiddens2, nums_hiddens3, requires_grad=True) * sigma)
        # self.b5 = nn.Parameter(torch.randn(nums_hiddens3, requires_grad=True) * sigma)

        # self.w6 = nn.Parameter(torch.randn(nums_hiddens3, nums_hiddens3, requires_grad=True) * sigma)
        # self.b6 = nn.Parameter(torch.randn(nums_hiddens3, requires_grad=True) * sigma)

        # self.w7 = nn.Parameter(torch.randn(nums_hiddens3, nums_hiddens4, requires_grad=True) * sigma)
        # self.b7 = nn.Parameter(torch.randn(nums_hiddens4, requires_grad=True) * sigma)

        # self.w8 = nn.Parameter(torch.randn(nums_hiddens4, nums_hiddens4, requires_grad=True) * sigma)
        # self.b8 = nn.Parameter(torch.randn(nums_hiddens4, requires_grad=True) * sigma)

        # self.w9 = nn.Parameter(torch.randn(nums_hiddens4, nums_hiddens5, requires_grad=True) * sigma)
        # self.b9 = nn.Parameter(torch.randn(nums_hiddens5, requires_grad=True) * sigma)
                
        # self.w10 = nn.Parameter(torch.randn(nums_hiddens5, nums_hiddens5, requires_grad=True) * sigma)
        # self.b10 = nn.Parameter(torch.randn(nums_hiddens5, requires_grad=True) * sigma)

        # self.w11 = nn.Parameter(torch.randn(nums_hiddens5, nums_hiddens6, requires_grad=True) * sigma)
        # self.b11 = nn.Parameter(torch.randn(nums_hiddens6, requires_grad=True) * sigma)

        # self.w12 = nn.Parameter(torch.randn(nums_hiddens6, nums_output, requires_grad=True) * sigma)
        # self.b12 = nn.Parameter(torch.randn(nums_output, requires_grad=True) * sigma)

        self.w1 = nn.Parameter(torch.Tensor(nums_input, nums_hiddens1).uniform_(-sigma, sigma))
        self.b1 = nn.Parameter(torch.Tensor(nums_hiddens1).uniform_(-sigma, sigma))

        self.w2 = nn.Parameter(torch.Tensor(nums_hiddens1, nums_hiddens1).uniform_(-sigma, sigma))
        self.b2 = nn.Parameter(torch.Tensor(nums_hiddens1).uniform_(-sigma, sigma))

        self.w3 = nn.Parameter(torch.Tensor(nums_hiddens1, nums_hiddens2).uniform_(-sigma, sigma))
        self.b3 = nn.Parameter(torch.Tensor(nums_hiddens2).uniform_(-sigma, sigma))

        self.w4 = nn.Parameter(torch.Tensor(nums_hiddens2, nums_hiddens2).uniform_(-sigma, sigma))
        self.b4 = nn.Parameter(torch.Tensor(nums_hiddens2).uniform_(-sigma, sigma))

        self.w5 = nn.Parameter(torch.Tensor(nums_hiddens2, nums_hiddens3).uniform_(-sigma, sigma))
        self.b5 = nn.Parameter(torch.Tensor(nums_hiddens3).uniform_(-sigma, sigma))

        self.w6 = nn.Parameter(torch.Tensor(nums_hiddens3, nums_hiddens3).uniform_(-sigma, sigma))
        self.b6 = nn.Parameter(torch.Tensor(nums_hiddens3).uniform_(-sigma, sigma))

        self.w7 = nn.Parameter(torch.Tensor(nums_hiddens3, nums_hiddens4).uniform_(-sigma, sigma))
        self.b7 = nn.Parameter(torch.Tensor(nums_hiddens4).uniform_(-sigma, sigma))

        self.w8 = nn.Parameter(torch.Tensor(nums_hiddens4, nums_hiddens4).uniform_(-sigma, sigma))
        self.b8 = nn.Parameter(torch.Tensor(nums_hiddens4).uniform_(-sigma, sigma))

        self.w9 = nn.Parameter(torch.Tensor(nums_hiddens4, nums_hiddens5).uniform_(-sigma, sigma))
        self.b9 = nn.Parameter(torch.Tensor(nums_hiddens5).uniform_(-sigma, sigma))

        self.w10 = nn.Parameter(torch.Tensor(nums_hiddens5, nums_hiddens5).uniform_(-sigma, sigma))
        self.b10 = nn.Parameter(torch.Tensor(nums_hiddens5).uniform_(-sigma, sigma))

        self.w11 = nn.Parameter(torch.Tensor(nums_hiddens5, nums_hiddens6).uniform_(-sigma, sigma))
        self.b11 = nn.Parameter(torch.Tensor(nums_hiddens6).uniform_(-sigma, sigma))

        self.w12 = nn.Parameter(torch.Tensor(nums_hiddens6, nums_output).uniform_(-sigma, sigma))
        self.b12 = nn.Parameter(torch.Tensor(nums_output).uniform_(-sigma, sigma))


        self.params=[self.w1,self.b1,
                     self.w2,self.b2,
                     self.w3,self.b3,
                     self.w4,self.b4,
                     self.w5,self.b5,
                     self.w6,self.b6,
                     self.w7,self.b7,
                     self.w8,self.b8,
                     self.w9,self.b9,
                     self.w10,self.b10,
                     self.w11,self.b11,
                     self.w12,self.b12]
        self.loss = nn.CrossEntropyLoss()
        
    
    def forward(self, x):
        # print(x)
        # input()
        H1 = self.relu(x @ self.w1 + self.b1)
        H2 = self.relu(H1 @ self.w2 + self.b2)
        H3 = self.relu(H2 @ self.w3 + self.b3)
        H4 = self.relu(H3 @ self.w4 + self.b4)
        H5 = self.relu(H4 @ self.w5 + self.b5)
        H6 = self.relu(H5 @ self.w6 + self.b6)
        H7 = self.relu(H6 @ self.w7 + self.b7)
        H8 = self.relu(H7 @ self.w8 + self.b8)
        H9 = self.relu(H8 @ self.w9 + self.b9)
        H10 = self.relu(H9 @ self.w10 + self.b10)
        H11 = self.relu(H10 @ self.w11 + self.b11)
        Out = H11 @ self.w12 + self.b12
        return Out

    
    def Train(self,epochs,lr):
        self.to(self.device)
        optimizer=torch.optim.SGD(self.params,lr=lr)
        warmup_epochs=0.001*epochs
        scheduler = torch.optim.lr_scheduler.CosineAnnealingLR(optimizer, T_max=epochs, eta_min=0.000000001)
        warmup_scheduler = torch.optim.lr_scheduler.LambdaLR(optimizer, lr_lambda=lambda epoch: epoch / warmup_epochs)
        
        loss_list=[]
        acc_list=[]
        for epoch in range(epochs):
            outputs=self.forward(self.train_data)
            loss=self.loss(outputs,self.train_lable)

            # 计算准确率
            _, predicted = torch.max(nn.functional.softmax(outputs.data), 1)
            total = self.train_lable.size(0)
            correct = (predicted == self.train_lable).sum().item()
            acc = correct / total
            acc_list.append(acc)

            optimizer.zero_grad()
            loss.backward()
            optimizer.step()
            if epoch<warmup_epochs:
                warmup_scheduler.step()
            else:
                scheduler.step()

            print(f'Epoch:{epoch} loss:{loss.item()} acc:{acc}')
            # print(f'Epoch:{epoch} loss:{loss.item()}')
            loss_list.append(loss.item())
        



        # torch.save(self.state_dict(),'./model.pth')
        plt.figure()
        plt.plot(range(epochs), loss_list)
        plt.title('Training Loss Curve')
        plt.xlabel('Epoch')
        plt.ylabel('Loss')
        plt.show()
        return loss_list, acc_list
        # plt.savefig('./loss_curve.png')
    
    def from_pretrained(self,model_path):
        self.load_state_dict(torch.load(model_path))
        return self
    
    def predict(self,input):
        input=torch.tensor(input,dtype=torch.float32).to(self.device)
        output=nn.functional.softmax(self.forward(input))
        output=torch.argmax(output).item()
        return output
        
        