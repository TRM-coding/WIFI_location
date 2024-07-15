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
