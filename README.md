##AndroidHttpCapture网络诊断工具<br>
是一款针对于移动流量劫持而开发的手机抓包软件<br>
主要功能包括：手机端抓包、PING/DNS/TraceRoute诊断、抓包HAR数据上传分享<br>
使用前请确保手机HTTP代理的关闭<br><br>
###[Demo APK下载](https://github.com/darkal/AndroidHttpCapture/blob/master/demo.apk)<br><br>
1． http抓包<br>
当用户通过HttpInterceptor访问页面的时候，所有的http请求都会被记录下来，然后这些请求包可以预览、分享、上传。<br>
其中预览是请求包在本机的预览，可以查看请求的响应头和响应体(图片类无法查看响应体)<br>
分享支持分享到微信、分享到手Q等常见分享功能<br>
![image](https://www.darkal.cn/imgd.php?src=/2016/09/WechatIMG77.jpeg&width=350) 
![image](https://www.darkal.cn/imgd.php?src=/2016/09/230686663947787928.jpg&width=350)<br><br>

2． 环境切换<br>
支持切换模拟为微信、手Q，默认为普通浏览器。<br>
![image](https://www.darkal.cn/imgd.php?src=/2016/09/WechatIMG81.jpeg&width=350)<br><br>

3． 多样性输入：导航、地址栏、扫一扫、schema呼起<br>
HttpInterceptor的首页为一个导航页，目前集成了微信和手Q的一级和二级入口，可以快速直达各目标页面。<br>
另外还支持地址栏直接输入地址，扫扫描二维码，以及schema呼起app并打开目标页面。<br>
schema的协议格式为：jdhttpmonitor://webview?param={'url'='http://www.baidu.com'}<br><br>

4． Host配置<br>
可以配置各域名的host<br>
![image](https://www.darkal.cn/imgd.php?src=/2016/09/WechatIMG79.jpeg&width=350&t=1)<br><br>


5． 查看console.log日志<br>
![image](https://www.darkal.cn/imgd.php?src=/2016/09/WechatIMG82.jpeg&width=350)<br><br>


6． 网络工具<br>
目前HttpInterceptor集成了常见的网络工具，如dns,ping,以及设备信息<br><br>

7． 设置系统代理，监听其他app请求包（仅android版支持）<br>
当将用户所使用的wifi代理服务器设置为127.0.0.1：8888时，可以对其他app进行抓包（此时该HttpInterceptor就是一个手机上的fiddler）<br>
![image](https://www.darkal.cn/imgd.php?src=/2016/09/WechatIMG80.jpeg&width=350)<br><br>

 
二．Q & A<br>
1. 分享的http包如何查看和分析？<br>
    分享的http包格式后缀为.har,可以通过fiddler方式或者在线工具进行分析。<br>
    Fiddler方式需要先将包导到电脑上，然后使用fiddler导入该包：Import Sessions->Select Import Format ->HTTPArchive ->选择包，即可<br>
    在线工具外网：http://h5.darkal.cn/har/<br>
    只需要将包拖入此工具即可分析<br>
