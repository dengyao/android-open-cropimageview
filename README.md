# CropImageView
### 1.简要介绍
CropImageView是基于ImageView实现的带裁剪框的ImageView控件，可用于实现图片裁剪区域的选择。  
主要功能：
* 拖动裁剪选择框，调整裁剪区域的位置和大小。
* 获取裁剪框相对于图片的位置，通过调用函数返回（float类型，0.0~1.0数值）。

**说明：**该控件只提供裁剪区域的选择的功能，未提供图片裁剪的功能。  
效果如下：  
![CropImageView](https://github.com/truistic/android-open-cropimageview/blob/master/screenshot/screenshot1.png)  
### 2.使用说明
简要说明：可以当普通的ImageView控件使用，ScaleTyle建议使用CENTER_INSIDE，设置padding值为12dp（建议）。

待更新
### 3.实现思路
简要说明：  
* 重写ImageView的onDraw（）方法，在ImageView上绘制裁剪区域矩形选择框。
* 监听触摸手势的动作，调整裁剪矩形选择框的位置和大小，刷新，重新绘制Imageview。
* 记录并计算矩形选择框相对于图片的位置。

待更新和完善
### 4.注意事项
该控件待更新和完善


