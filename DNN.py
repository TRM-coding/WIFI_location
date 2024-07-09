import torch
from torch import nn

class DNN(nn.Module):

    def __init__(self):
        super(DNN, self).__init__()

    def load_train_data(self,train_input,train_lable):
        self.train_data=train_input
        self.train_lable=train_lable


    def relu(self,x):
        a=torch.zeros_like(x)
        return torch.max(x,a)

    
    def init(self):
        nums_input=123
        nums_output=3
        nums_hiddens=256

        self.w1=nn.Parameter(torch.randn(nums_input,nums_hiddens,requires_grad=True))
        self.b1=nn.Parameter(torch.randn(nums_hiddens,requires_grad=True))
        self.w2=nn.Parameter(torch.randn(nums_hiddens,nums_hiddens,requires_grad=True))
        self.b2=nn.Parameter(torch.randn(nums_hiddens,nums_hiddens,requires_grad=True))
        self.w3=nn.Parameter(torch.randn(nums_hiddens,nums_output,requires_grad=True))
        self.b3=nn.Parameter(torch.randn(nums_output,requires_grad=True))
        self.params=[self.w1,self.b1,self.w2,self.b2,self.w3,self.b3]
        self.loss=nn.CrossEntropyLoss(reduction='none')
    
    def forward(self,x):
        H1=self.relu(x@self.w1+self.b1)
        H2=self.relu(H1@self.w2+self.b2)
        return (H2@self.w3+self.b3)
    
    def train(self,epochs,lr):
        optimizer=torch.optim.SGD(self.params,lr=lr)

        for epoch in range(epochs):
            outputs=self.forward(self.train_data)
            loss=self.loss(outputs,self.train_lable)

            optimizer.zero_grad()
            loss.backward()
            optimizer.step()

            print(f'Epoch:{epoch} loss:{loss.item()}')

        torch.save(self.state_dict(),'./DNN/model.pth')
    
    def from_pretrained(self,model_path):
        self.load_state_dict(torch.load(model_path))
        return self
    
    def predict(self,input):
        return self.forward(input)
        
        


