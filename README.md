## AndroidHttpCapture网络诊断工具      [![Travis](https://img.shields.io/travis/rust-lang/rust.svg)](https://travis-ci.org/JZ-Darkal/AndroidHttpCapture)<br>
是一款针对于移动流量劫持而开发的手机抓包软件，可以当作是Android版的‘Fiddler’<br>
主要功能包括：手机端抓包、PING/DNS/TraceRoute诊断、抓包HAR数据上传分享<br>
使用前请确保手机HTTP代理的关闭<br><br>
### [Demo APK下载](http://static.hk.darkal.cn/har/demo.apk)<br>

### [点击查看操作手册](http://static.hk.darkal.cn/har/guide/widget.guide.html)<br><br>


### 功能简介
1． HTTP/HTTPS抓包<br>
当用户通过AndroidHttpCapture访问页面的时候，所有的http请求都会被记录下来，然后这些请求包可以预览、分享、上传（上传接口的网址需自行在MainActivity修改）。<br>
#### 第一次进入程序需要安装CA证书以便进行HTTPS抓包（原理同fiddler，MITM中间人）不安装证书的话无法抓取HTTPS的请求<br>
#### 高版本的Android不允许跳转设置安装证书，需要自行在设置->安全和锁屏->加密与凭据->安装证书（证书位置：/har/littleproxy-mitm.pem）<br>
预览页面可以查看从APP启动起所有网络请求数据，实现了按分页过滤、URL搜索功能，并可清空所有数据包<br>
预览的内容包括Request Header、Request Cookie、Request Content、Response Header、Response Cookie、Response Content<br>
Content内容如果为JSON将会自动格式化显示<br>
![image](http://static.hk.darkal.cn/imgd.php?src=/2016/09/WechatIMG77.jpeg&width=350) 
![image](http://static.hk.darkal.cn/imgd.php?src=/2016/09/230686663947787928.jpg&width=350)<br>
分享功能将抓包生成的所有数据包打包为har文件并压缩为zip，支持分享到微信、QQ等<br><br>

2． 返回包注入<br>
支持修改流量返回包（该版本暂时只支持http的修改）<br>
![image](http://static.hk.darkal.cn/imgd.php?src=/2016/09/WechatIMG180.jpeg&width=350)
![image](http://static.hk.darkal.cn/imgd.php?src=/2016/09/WechatIMG181.jpeg&width=350)<br><br>

3． 环境切换<br>
支持切换模拟为微信、手Q，默认为普通浏览器。<br>
![image](http://static.hk.darkal.cn/imgd.php?src=/2016/09/WechatIMG81.jpeg&width=350)<br><br>

4． 多样性输入：导航、地址栏、扫一扫、schema呼起<br>
支持地址栏直接输入地址，扫扫描二维码，以及schema呼起app并打开目标页面。<br>
schema的协议格式为：jdhttpmonitor://webview?param={'url'='http://www.darkal.cn'}<br><br>

5． Host配置<br>
可以配置各域名的host<br>
![image](http://static.hk.darkal.cn/imgd.php?src=/2016/09/WechatIMG79.jpeg&width=350&t=1)<br><br>


6． 查看console.log日志<br>
![image](http://static.hk.darkal.cn/imgd.php?src=/2016/09/WechatIMG82.jpeg&width=350)<br><br>


7． 网络工具<br>
目前AndroidHttpCapture集成了常见的网络工具，如dns,ping,以及设备信息<br>
![image](http://static.hk.darkal.cn/imgd.php?src=/2016/09/621495078826.jpg&width=350)<br><br>

8． 设置系统代理，监听其他app请求包<br>
当将用户手机的代理服务器设置为127.0.0.1:8888时，可以对其他app（例如微信）的HTTP数据进行抓包<br>
（此时AndroidHttpCapture就是一个手机上的fiddler）<br>
![image](http://static.hk.darkal.cn/imgd.php?src=/2016/09/WechatIMG80.jpeg&width=350)<br><br>

### Q & A<br>
1. 分享的http包如何查看和分析？<br>
    分享的文件解压后为.har文件,可以通过fiddler方式或者在线工具进行分析。<br>
    Fiddler方式需要先将包导到电脑上，然后使用fiddler导入该包：Import Sessions->Select Import Format ->HTTPArchive ->选择包，即可<br>
    在线工具外网：http://static.hk.darkal.cn/har/ 只需要将包拖入此工具即可分析<br><br>

### 已知BUG<br>
1. 信任所有的服务器证书不做校验<br>
~~2. 开启返回包注入功能后，https返回的部分页面存在 err_CONTENT_LENGTH_MISMATCH 错误<br>~~（看起来似乎是解决了，待用户反馈）

#### 如果觉得工具好用的话请多多star以及Pull requests<br>支持我喝杯咖啡请扫描下面的二维码，谢谢(ง •̀_•́)ง<br>
![image](http://static.hk.darkal.cn/har/guide/img/code.jpg)<br><br>


### 致谢<br>
AndroidHttpCapture基于Netty、browsermob-proxy来实现核心抓包的功能<br>
Netty is an asynchronous event-driven network application framework for rapid development of maintainable high performance protocol servers & clients.<br>
https://github.com/netty/netty<br>
由于Android5.0+不支持Provider 为JKS的证书，所以逆向修改了Netty库的证书部分适配Android系统（netty_android.jar）<br><br>

A free utility to help web developers watch and manipulate network traffic from their AJAX applications.<br>
https://github.com/lightbody/browsermob-proxy<br>
修改了多处browsermob-proxy的源码适配Android系统<br><br>

MIT License<br>
Copyright (c) 2016 AndroidHttpCapture

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
