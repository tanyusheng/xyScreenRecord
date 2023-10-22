android screen record tool

![Snipaste_2023-10-22_18-57-01](localpicbed/README.assets/Snipaste_2023-10-22_18-57-01.png)

### 原理：

通过MediaRecorder对Surface进行抓取实现录屏；

### 功能：

* 可以录制麦克风声音；

* 输出的MP4视频文件存储在sdcard根目录下；

* 以Service的形式启动录屏，实现后台录制，突破只能在当前Activity进行录制的限制；

* 录制长度小于3秒自动删除录制的内容

* 当前系统存储空间小于4Mb自动停止录屏
