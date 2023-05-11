//一些变量和常量
var userInfo = document.getElementById("userInfo");
var logoutbtn = document.getElementById("logoutbtn");

//获取cookie的函数
function getCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        //while 循环用于去除字符串开头的空格。
        //由于在分割 cookie 时，每个 cookie 值之间可能会有空格，
        //所以需要使用 while 循环去除这些空格，以便能够正确地匹配 cookie 名称。
        while (c.charAt(0)==' ') 
            c = c.substring(1,c.length);

        if (c.indexOf(nameEQ) == 0) 
            return c.substring(nameEQ.length,c.length);
    }
    return null;
}


//根据当前时间，获取不同的问候语
function getGreeting() {
    var currentTime = new Date().getHours();
    if (currentTime >= 23 || currentTime < 4) {
        return "祝你好梦！";
    } 
    else if (currentTime >= 4 && currentTime < 6) {
        return "清晨好！";
    }
    else if (currentTime >= 6 && currentTime < 11) {
        return "早上好！";
    }
    else if (currentTime >= 11 && currentTime < 14) {
        return "中午好！";
    }
    else if (currentTime >= 14 && currentTime < 18) {
        return "下午好！";
    }
    else if (currentTime >= 18 && currentTime < 23) {
        return "晚上好！";
    }

}

//每次加载页面：1、从服务器获取登录用户信息并显示问候语
//            2、为所有链接添加时间戳（点击事件实现）避免浏览器缓存
//            3、若用户已登录，则注销按钮
window.onload = function() {
    var xhr = new XMLHttpRequest();
    var sayHello = getGreeting();
    xhr.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            if (this.responseText != "") {
                userInfo.innerHTML = sayHello + this.responseText;
                logoutbtn.style.display = "inline";
            } else {
                userInfo.innerHTML = sayHello + "访客，您还未登录！";
            }
        }
    };
    xhr.open("GET", "/GetLoggedUserInfo", true);
    xhr.send();
    
    //为所有链接添加时间戳（点击事件实现）
    var links = document.querySelectorAll("a");
    for (var i = 0; i < links.length; i++) {
        links[i].addEventListener("click", function(event) {
            var href = this.getAttribute("href");
            var timestamp = new Date().getTime();
            href += "?timestamp=" + timestamp;
            this.setAttribute("href", href);
        });
    }

}



