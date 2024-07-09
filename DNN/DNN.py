import torch
from torch import nn
import matplotlib.pyplot as plt


class DNN(nn.Module):

    def __init__(self):
        super(DNN, self).__init__()
        self.device= torch.device('cuda' if torch.cuda.is_available() else 'cpu')
        self.relu = nn.LeakyReLU(negative_slope=0.1)

    def load_train_data(self,train_input,train_lable):
        self.train_data=torch.tensor(train_input,dtype=torch.float32).to(self.device)
        self.train_lable=torch.tensor(train_lable,dtype=torch.float32).to(self.device)

    # def relu(self,x):
        
    #     a = torch.zeros_like(x)
    #     return torch.max(x, a)
    


    
    def init(self):
        nums_input=32
        nums_output=1
        nums_hiddens=64

        
        self.w1 = nn.Parameter(torch.randn(nums_input, nums_hiddens, requires_grad=True)* 0.1 + 0.001)
        self.b1 = nn.Parameter(torch.randn(nums_hiddens, requires_grad=True) * 0.1)

        self.w2 = nn.Parameter(torch.randn(nums_hiddens, nums_hiddens, requires_grad=True) *0.1 + 0.001)
        self.b2 = nn.Parameter(torch.randn(nums_hiddens, requires_grad=True) * 0.1 + 0.001)

        self.w3 = nn.Parameter(torch.randn(nums_hiddens, nums_hiddens, requires_grad=True) *0.1 + 0.001)
        self.b3 = nn.Parameter(torch.randn(nums_hiddens, requires_grad=True) * 0.1 + 0.001)

        self.w4 = nn.Parameter(torch.randn(nums_hiddens, nums_hiddens, requires_grad=True) * 0.1 + 0.001)
        self.b4 = nn.Parameter(torch.randn(nums_hiddens, requires_grad=True) * 0.1 + 0.001)

        self.w5 = nn.Parameter(torch.randn(nums_hiddens, nums_hiddens, requires_grad=True) * 0.1 + 0.001)
        self.b5 = nn.Parameter(torch.randn(nums_hiddens, requires_grad=True) * 0.1 + 0.001)

        self.w6 = nn.Parameter(torch.randn(nums_hiddens, nums_hiddens, requires_grad=True) * 0.1 + 0.001)
        self.b6 = nn.Parameter(torch.randn(nums_hiddens, requires_grad=True) * 0.1 + 0.001)

        self.w7 = nn.Parameter(torch.randn(nums_hiddens, nums_output, requires_grad=True) * 0.1 + 0.001)
        self.b7 = nn.Parameter(torch.randn(nums_output, requires_grad=True) * 0.1 + 0.001)

        self.params=[self.w1,self.b1,
                     self.w2,self.b2,
                     self.w3,self.b3,
                     self.w4,self.b4,
                     self.w5,self.b5,
                     self.w6,self.b6,
                     self.w7,self.b7]
        self.loss = nn.MSELoss(reduction='mean')
    
    def forward(self, x):
        # print(x)
        # input()
        H1 = self.relu(x @ self.w1 + self.b1)
        H2 = self.relu(H1 @ self.w2 + self.b2)
        # H3 = self.relu(H2 @ self.w3 + self.b3)
        # H4 = self.relu(H3 @ self.w4 + self.b4)
        H5 = self.relu(H2 @ self.w5 + self.b5)
        H6 = self.relu(H5 @ self.w6 + self.b6)
        Out = H6 @ self.w7 + self.b7
        return Out

    
    def Train(self,epochs,lr):
        self.to(self.device)
        optimizer=torch.optim.SGD(self.params,lr=lr)
        scheduler = torch.optim.lr_scheduler.CosineAnnealingLR(optimizer, T_max=epochs, eta_min=0)
        loss_list=[]
        for epoch in range(epochs):
            outputs=self.forward(self.train_data)
            loss=self.loss(outputs,self.train_lable)

            optimizer.zero_grad()
            loss.backward()
            optimizer.step()
            scheduler.step()

            # if epoch%1000 ==0 :
            print(f'Epoch:{epoch} loss:{loss.item()}')
            loss_list.append(loss.item())


        torch.save(self.state_dict(),'./DNN/model.pth')
        plt.figure()
        plt.plot(range(epochs), loss_list)
        plt.title('Training Loss Curve')
        plt.xlabel('Epoch')
        plt.ylabel('Loss')
        # plt.show()
        plt.savefig('./DNN/loss_curve.png')
    
    def from_pretrained(self,model_path):
        self.load_state_dict(torch.load(model_path))
        return self
    
    # def predict(self,input):
    #     input=torch.tensor(input,dtype=torch.float32).to(self.device)
        
    #     res=self.forward(input)
    #     loss=self.loss(res,self.train_lable)
    #     return res,loss
        
        


