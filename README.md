# exposed: "TrosSense" 图片客户端

Tip:蓝字可以点击。

”TrosSense“以下简称“ts”。

## 序章：关于ts的开发者

抽象的[B站简介](https://space.bilibili.com/3546768430074157)

![Bili](https://raw.githubusercontent.com/ReallyErYZ/TrosSense/main/imgs/383593db912d92d8b1b7cd7eaf311b91.png)


存500元装图吧神机大战黑神话：

<img src="https://raw.githubusercontent.com/AngelPwn/TrosSense/main/imgs/ts_dev_computer.png" title="" alt="ts_dev_computer.png" width="220">

居然是神權狗和拉黑狗？！

<img title="" src="https://raw.githubusercontent.com/AngelPwn/TrosSense/main/imgs/ts_devkick.jpg" alt="tsdevkickjpg" width="253">

<img title="" src="https://raw.githubusercontent.com/AngelPwn/TrosSense/main/imgs/ts_dev_black.jpg" alt="tsdevblackjpg" width="251">

抽象群介紹好似奶龍：

<img src="https://raw.githubusercontent.com/AngelPwn/TrosSense/main/imgs/ts_group.jpg" title="" alt="ts_group.jpg" width="271">

假开源，真装逼？

[Github仓库地址](https://github.com/Necole1337/TrosSense)

![github](https://raw.githubusercontent.com/ReallyErYZ/TrosSense/main/imgs/58be11a23fe06b2e4a390844e59aa393.png)

![kook](https://raw.githubusercontent.com/ReallyErYZ/TrosSense/main/imgs/afdb4c65713e36eb96cf944a13b0cbdc.png)

![ZoomFly?](https://raw.githubusercontent.com/ReallyErYZ/TrosSense/main/imgs/5425037ba8a4e97bc0374133387d8c04.png)

## 老鼠传奇

最讲究的一集，有后门居然说出来。

![TrosSense_user_agreement_data_collection.png](https://raw.githubusercontent.com/AngelPwn/TrosSense/main/imgs/TrosSense_user_agreement_data_collection.png)

> 节选自[TrosSense用户协议[TOS]&常见问题【】](/imgs/TrosSense用户协议[TOS]&常见问题【】.png)（是的，它原本就是这样命名的）。
> <img title="" src="https://raw.githubusercontent.com/AngelPwn/TrosSense/main/imgs/shit_name.png" alt="shit_name.png" width="422">

给你文件偷干净：

![permission.png](https://raw.githubusercontent.com/AngelPwn/TrosSense/main/imgs/permission.png)

## META-INF对比法

ts开发者发的公告：

<img title="" src="https://raw.githubusercontent.com/AngelPwn/TrosSense/main/imgs/meta-inf.jpg" alt="meta-inf.jpg" width="233">

![c88a77fbddf3e4db1283d7dd885bbdea.jpg](https://raw.githubusercontent.com/AngelPwn/TrosSense/main/imgs/c88a77fbddf3e4db1283d7dd885bbdea.jpg)

> 作为中国人故意把系统语言调成英文，崇洋媚外？

而最令人感叹的是，该目录实际上是一个是 Android 系统用于验证 APK 文件的完整性并防止未经授权的修改的一个目录。换句话说，他是全局的，任何安装包都有，至于其目录下其他的`*.version` 文件，则是一种描述文件。只要使用这类库，都会存在。而这个库的名字是什么呢: **AndroidX**。这是 Google 为了兼容不同的 Android Sdk 版本所发行的一种不需要让开发者自己适配版本的解决方案，基本上现在的任何一个新版 Android 工程结构都会引用该包。

> 安卓的系统设置也有这个捏，是不是也算抄袭呢？
> 
> <img title="" src="https://raw.githubusercontent.com/AngelPwn/TrosSense/main/imgs/meta-inf_android_settings.jpg" alt="meta-inf_android_settings.jpg" width="211">

## PhotoShopUI(造假)

我们在解包时发现，ts安装包中的`assets/imgs/back.img`竟然与ts开发者发布在**哔哩哔哩**

的一个视频游戏界面一模一样，难道使用了[PhotoShopUI](https://github.com/CodeRyn2019/PhotoShopUI)？

这是ts安装包中的`assets/imgs/back.img`：

![back100.png](https://raw.githubusercontent.com/ReallyErYZ/TrosSense/main/imgs/back100.png)

这是ts作者[胎里素宝宝](https://space.bilibili.com/3546768430074157)发布在[哔哩哔哩上的视频](https://www.bilibili.com/video/BV1FfxeeQErV)截图：

<img src="https://raw.githubusercontent.com/ReallyErYZ/TrosSense/main/imgs/video.png" title="" alt="video.png" width="243">

让我们把他们合在一起（视频截图透明度50%）：

![back100_video50.png](https://raw.githubusercontent.com/ReallyErYZ/TrosSense/main/imgs/back100_video50.png)

惊人的吻合呢，看来真的使用了大名鼎鼎的[PhotoShopUI](https://github.com/CodeRyn2019/PhotoShopUI)。

耀武扬威但是没有实际能力实现是这样的。

## 脑电波模块

还没开始逆向，群友就发现了端倪。

以下聊天截图来自Horion dev群聊：

<img src="https://raw.githubusercontent.com/AngelPwn/TrosSense/main/imgs/brainwave_02.png" title="" alt="brainwave_02.png" width="235">

<img src="https://raw.githubusercontent.com/AngelPwn/TrosSense/main/imgs/brainwave_03.png" title="" alt="brainwave_03.png" width="282">

<img src="https://raw.githubusercontent.com/AngelPwn/TrosSense/main/imgs/brainwave_05.png" title="" alt="brainwave_05.png" width="337">

<img src="https://raw.githubusercontent.com/AngelPwn/TrosSense/main/imgs/brainwave_01.png" title="" alt="brainwave_01.png" width="285">

耀武扬威但是压根不了解耀的武是什么是这样的。

## 遥遥领先

“领先挂圈100年”。

可能指的是造假技术吧。

<img title="" src="https://raw.githubusercontent.com/AngelPwn/TrosSense/main/imgs/a789e9fc1ce3b0216cd294552c981d1e.jpg" alt="a789e9fc1ce3b0216cd294552c981d1e.jpg" width="294">

> 名字都打错了。

## 作者破防/自知之明？

这时，ts开发者加入了Horion dev群聊。

<img src="https://raw.githubusercontent.com/AngelPwn/TrosSense/main/imgs/ts_dev_join_group.png" title="" alt="ts_dev_join_group.png" width="271">

刚进群看到群友如此评价，它破防了。

或者说是有了自知之明。

![ts_dev_crash.png](https://raw.githubusercontent.com/AngelPwn/TrosSense/main/imgs/ts_dev_crash.png)

> 2024/11/10 15:31 看来不是有了自知之明，是真的破防了
> 
> ![ts_dev_real_crash.png](https://raw.githubusercontent.com/AngelPwn/TrosSense/main/imgs/ts_dev_real_crash.png)
