# 基于WIFI指纹定位的图书馆综合服务系统-书山智寻前端

## 代码结构


### 功能模块

* `com.example.wifilocation/`
  * `Login.java`：登录模块
  * `Register.java`：注册模块
  * `MainActivity.java`：主页面模块
  * `ChatFragment.java`：聊天模块
  * `ChatActivity.java`：大语言交互模块
  * `LocateFragment.java`：搜索书籍模块
  * `LocateActivity.java`：地图模块
  * `LoadingDialog.java`：加载对话框类
  * `SelfFragment.java`：我的模块
  * `OutdoorActivity.java`：室外地图模块
  * `Util.java`：工具类
  * `chat/`
    * `ChatAdapter`：聊天适配器类
    * `MessageAdapter`：消息适配器类
  * `locate/`
    * `Book`：书本类
    * `BookAdapter`：书本适配器
    * `MapContainer`：地图容器类
    * `MapView`：地图视图类
    * `Marker`：标记类
    * `Me`：自身类
  
 * `res/`
   * `layout/`
     * `avtivity_login.xml`：登录页面
     * `avtivity_register.xml`：注册页面
     * `avtivity_main.xml`：主页面
     * `fragment_chat.xml`：聊天窗口
     * `avtivity_chat.xml`：大语言交互页面
     * `fragment_locate.xml`：搜索书籍窗口
     * `avtivity_locate.xml`：地图页面
     * `fragment_self.xml`：我的窗口
     * `layout_book_item.xml`：书籍列表样式
     * `layout_chat_item.xml`：聊天列表样式
     * `layout_message_left_item.xml`：他人消息样式
     * `layout_message_right_item.xml`：自己消息样式
     * `dialog_loading.xml`：加载对话框样式
     * `dialog_temperature.xml`：温度调节对话框样式



# 基于WIFI指纹定位的图书馆综合服务系统-书山智寻后端

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
