# 基于WIFI指纹定位的图书馆综合服务系统-书山智寻

## 代码结构

### 接口模块

* `serveapi.py`：服务器API，完成与客户端的http交互逻辑
* `LLMapis.py`：大模型交互接口，实现大模型不需要每次重新加载

### 功能模块

* `DNN/`：定位用神经网络文件夹
  * `dataloader.py`：从数据库加载数据，划分训练集和验证集
  * `DNN.py`：模型文件（内置Train()函数，可直接调用，无需重写训练回路）
  * `eval.py`：从`test.json`加载测试数据，进行模型性能分析
  * `modelx.pth`：表现效果最好的一版预测`x`坐标模型
  * `modely.pth`：表现效果最好的一版预测`y`坐标模型
  * `modelz.pth`：表现效果最好的一版预测`z`坐标模型
  * `train.ipynb`：训练和验证监控，提供参数接口
  * `test.json`：额外采集的未在数据库中，用于评估模型泛化能力的数据
  * `415.json`：采集的训练用数据，被存储入数据库中，划分为测试集和验证集
* `glm4/`：chatglm4-9B-chat模型存储文件夹
* `wifidbcreate`：自动化数据库构建程序，支持动态导入数据库
  * `datas/`：设备采集的WIFI指纹数据
  * `createlocationdada.py`：将`datas/`中的数据创建表，并插入
  * `unique.py`提供采集数据去重的工具（防止由于采集过快，造成的数据重复）
* `kNN.py`：KNN算法类，提供利用KNN完成的精细定位和房间定位函数
* `navigator.py`：导航算法，利用SPFA实现书记导航
* `PermissionController.py`：权限检查模块，用于登录和注册
* `LLMkernel.py`：大模型调用逻辑实现模块