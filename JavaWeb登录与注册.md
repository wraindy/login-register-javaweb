# 使用JavaWeb技术实现账号的登录、注册和注销功能

---



## 前言



### 作业简介

​		该项目是学校的JavaWeb作业，基于Tomcat服务器和MySQL数据库实现了简单的登录、注册和注销登录功能，包含验证码的绘制，记住已登录的账号等功能。

​		项目还有许多漏洞，十分欢迎各位大佬锐评。

### 原作业题目要求

1. 首页以index.html命名，具体使用技术不限，但必须实现以下功能：
2. 注册功能要求实现JDBC数据库连接，完成用户信息注册，并动态更新数据库。
3. 登录页面要使用验证码技术，并与数据库连接，实现用户信息及验证码验证；没有登录的用户只能以游客身份访问首页，其余子页要进行屏蔽，没有访问权限。
4. 成功登录后，在主页面要显示用户名、实时时间及不同时间的问候语。

### 注意

​	本项目由本人完成，禁止商用，仅供学习交流，转载需经过本人同意。

​	联系方式：[Github主页有邮箱](https://github.com/wraindy)

​	全部源码：[点击跳转](https://github.com/wraindy/login-register-javaweb)

---



## 作业内容

​	本作业所有源码已做超级详细的注释，具体自己去看，这里仅作大概的介绍。

​	Session是完成这个作业的关键，每一个用户访问服务器的时候都会被赋予一个cookie（保存的是SessionID），服务器根据这个cookie来区分每一个来访者，那么我们可以在这基础之上实现



### 环境要求

本项目实现的环境：Windows11+IDEA2023+JDK1.8+MySQL8.0+Tomcat9+JDBC

本项目JDBC包的路径：`"...Lab5\src\main\webapp\WEB-INF\lib\mysql-connector-java-8.0.21.jar"`

如果你要下载部署，不一定跟我一样的版本，主要是Servlet的实现逻辑





### 项目包含的文件

#### Servlet（.java）

DBAccess、GetCAPTCHA、GetLoggedUserInfo、GetMsg、LoginControl、LogoutControl、MyFilter、RegisterControl、RemoveDebug、User

#### 前端（.html、.css、.js）

index.html主页

login.html登录页面

register.html注册页面

test.html受保护的页面（需要登录才能访问，非登录用户访问则跳转到登录页面）

同名.css、.js文件是对应.html的文件，favicon.ico是网页标签小图标。

#### MySQL

​		DBAccess类是访问MySQL的Servlet，具体请看对应注释，现在给出users表的建表SQL语句：

```mysql
CREATE TABLE `users` (
  `username` varchar(20) NOT NULL,
  `password` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`username`),
  CONSTRAINT `CK_name` CHECK (((length(`username`) >= 8) and (length(`username`) <= 20))),
  CONSTRAINT `CK_psw` CHECK (((length(`password`) >= 8) and (length(`password`) <= 20)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
```

​		因为将用户名设置为`Primary Key`，因此若注册重复的用户名的账号，会被拒绝添加进表。



### 部分实现功能详解



#### 验证码的实现

验证码出现在注册和登录界面。

##### 两种刷新方式

​		1、每次刷新页面

​		2、每次点击“看不清”按钮

以注册为例，

##### Servlet的实现

​		Servlet的实现参考了这篇文章：[点击跳转](https://blog.csdn.net/qq_40693603/article/details/109469211)，十分感谢@sinJack

​		只要前端发送请求，GetCAPTCHA类会发送最新的随机验证码图片给前端，然后将答案保存在Session的CAPTCH属性中，若用户登录成功则会移除该属性。

##### 前端的实现

​		对于第一种刷新方式，只需要在html设置img标签的src即可：

```html
<div class="captchabox">
            <img src="/GetCAPTCHA" alt="验证码">
            <button type="button" class="button">看不清</button><br><br>
        </div>
```

​		对于第二种刷新方式，需要用js函数对“看不清”按钮添加点击事件，每次点击都会重置src属性，新的src属性要添加时间戳，避免浏览器使用缓存，无法刷新验证码。

```js
//重置src属性就会重新加载新的验证码
function refreshCaptcha() {
    var captchaImg = document.querySelector('.captchabox img');
    captchaImg.src = '/GetCAPTCHA?' + new Date().getTime();
}

//按钮点击事件
document.querySelector('.captchabox button').addEventListener('click', function() {
    refreshCaptcha();
});

```



#### 登录的实现

##### 流程

用户输入表单→前端合法性检查→提交表单（→展示msg错误消息）...

Servlet接收表单→Servlet合法性检查→登录成功跳转主页/登录失败反馈信息...

##### 前端的实现

合法性检查有两种两种方式

​		1、输入框的失焦

```js
//当用户名输入框失去焦点时，检查用户名是否符合正则表达式
usernameInput.onblur = function() {
    if (!usernameReg.test(usernameInput.value)) {
        msgSpan.innerHTML = "用户名格式错误";
    } else {
        msgSpan.innerHTML = "";
    }
}
```

​		2、点击提交按钮

```js
//当表单提交时，检查用户名和密码是否符合正则表达式
document.getElementById("lgform").addEventListener("submit", function(event) {
    
    // 检查输入是否符合正则表达式
    if (!usernameReg.test(usernameInput.value)) {
        // 显示错误消息
        msgSpan.innerHTML = "用户名格式错误";
        // 阻止表单提交
        event.preventDefault();
        //刷新验证码
        refreshCaptcha();
    } else if (!passwordReg.test(passwordInput.value)) {
        // 显示错误消息
        msgSpan.innerHTML = "密码格式错误";
        // 阻止表单提交
        event.preventDefault();
        //刷新验证码
        refreshCaptcha();
    }
});
```

​		具体实现可以看login.js，需要用到正则表达式，限制范围：用户名，8-20字符，仅允许英文和数字；密码，8-20字符。

​		登录和注册都用到相同的限制，服务器也要做正则的判断，防御性编程。

```js
//js里面的正则
const usernameReg = /^[a-zA-Z0-9]{8,20}$/;
const passwordReg = /^.{8,20}$/;
```

```java
//Servlet里面的正则
//检查账号or密码的合法性（长度8-20，密码为任意字符/用户名必须字母或数字）
        if (!username.matches("[a-zA-Z0-9]{8,20}") || !password.matches(".{8,20}")) {
            visitorSession.setAttribute("msgCode", GetMsg.incorrectInput);
            resp.sendRedirect("/login.html");
            return;
        }
```

##### Servlet的实现

​		LoginControl已经给出详细的注释，具体自己去看。重点关注msg的设置。msg就是登录页面给出的错误信息提示，包含：

​		1、验证码错误

​		2、输入错误

​		3、账号不存在

​		4、用户重复登录

​		然后用户调用DBAccess类的`queryUser()`方法，判断用户是否已注册，决定用户是否登录成功。

​		若用户登录失败，LoginControl类根据用户提交的表单设置好msg的消息种类（定义在GetMsg类），将其保存在Session中，GetMsg类根据消息种类使用打印流给前端发送msg，前端对msg进行展示。

前端js实现，用到了Ajax：

```js
//刷新msg的函数
function refreshMsg() {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
        msgSpan.innerHTML = this.responseText;
    }};
    xhr.open("GET", "/GetMsg", true);
    xhr.send();
}

```

Ajax的学习请参考：[点击跳转](https://www.w3school.com.cn/js/js_ajax_intro.asp)

#### 注册的实现

​		注册的具体实现和登录类似，只需要在合法性检查和MySQL的使用有所区别，还需要考虑用户已登录和未登录两种状态时进行注册的情况。