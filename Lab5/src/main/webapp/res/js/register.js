//一些变量和常量
var msgSpan = document.getElementById("msg");
var usernameInput = document.getElementById("username");
var passwordInput = document.getElementById("password");
var password2Input = document.getElementById("password2");
const usernameReg = /^[a-zA-Z0-9]{8,20}$/;
const passwordReg = /^.{8,20}$/;


//刷新验证码图片的函数
function refreshCaptcha() {
    var captchaImg = document.querySelector('.captchabox img');
    captchaImg.src = '/GetCAPTCHA?' + new Date().getTime();
}

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


//每次点击“看不清”按钮，都会重新加载一张验证码图片
//注意，要在请求的URL后面加上一个时间戳，否则浏览器会认为是同一张图片，不会重新加载
document.querySelector('.captchabox button').addEventListener('click', function() {
    refreshCaptcha();
});

// //获取cookie的函数
// function getCookie(name) {
//     var nameEQ = name + "=";
//     var ca = document.cookie.split(';');
//     for(var i=0;i < ca.length;i++) {
//         var c = ca[i];
//         //while 循环用于去除字符串开头的空格。
//         //由于在分割 cookie 时，每个 cookie 值之间可能会有空格，
//         //所以需要使用 while 循环去除这些空格，以便能够正确地匹配 cookie 名称。
//         while (c.charAt(0)==' ') 
//             c = c.substring(1,c.length);

//         if (c.indexOf(nameEQ) == 0) 
//             return c.substring(nameEQ.length,c.length);
//     }
//     return null;
// }

//每次加载页面：从服务器获取登录反馈信息msg            
window.onload = function() {

    // var user = getCookie("user");
    // var psw = getCookie("psw");
    // if (user != null && user != "" && psw != null && psw != "") {
    //     usernameInput.value = user;
    //     passwordInput.value = psw;
    // }

    refreshMsg();
//     var xhr = new XMLHttpRequest();
//     xhr.onreadystatechange = function() {
//     if (this.readyState == 4 && this.status == 200) {
//         msgSpan.innerHTML = this.responseText;
//     }};

// xhr.open("GET", "/GetMsg", true);
// xhr.send();
}




//当用户名输入框失去焦点时，检查用户名是否符合正则表达式
usernameInput.onblur = function() {
    if (!usernameReg.test(usernameInput.value)) {
        msgSpan.innerHTML = "用户名格式错误";
    } else {
        msgSpan.innerHTML = "";
    }
}

//当密码输入框失去焦点时，检查密码是否符合正则表达式
passwordInput.onblur = function() {
    if (!passwordReg.test(passwordInput.value)) {
        msgSpan.innerHTML = "密码格式错误";
    } else {
        msgSpan.innerHTML = "";
    }
}

//当密码确认输入框失去焦点时，检查两次密码是否一致
password2Input.onblur = function() {
    if (passwordInput.value != password2Input.value) {
        msgSpan.innerHTML = "两次密码不一致";
    } else {
        msgSpan.innerHTML = "";
    }
}

//当表单提交时，检查用户名和密码是否符合正则表达式
document.getElementById("rform").addEventListener("submit", function(event) {
    
    // 检查输入是否符合正则表达式
    if (!usernameReg.test(usernameInput.value)) {
        // 显示错误消息
        msgSpan.innerHTML = "用户名格式错误";
        // 阻止表单提交
        event.preventDefault();
        refreshCaptcha();
    } else if (!passwordReg.test(passwordInput.value)) {
        // 显示错误消息
        msgSpan.innerHTML = "密码格式错误";
        // 阻止表单提交
        event.preventDefault();
        refreshCaptcha();
    }
});

//当表单提交时，检查
//1、用户名和密码是否符合正则表达式
//2、两次密码是否一致
document.getElementById("rform").addEventListener("submit", function(event) {
    // 检查输入是否符合正则表达式
    if (!usernameReg.test(usernameInput.value)) {
        // 显示错误消息
        msgSpan.innerHTML = "用户名格式错误";
        // 阻止表单提交
        event.preventDefault();
        refreshCaptcha();
    } else if (!passwordReg.test(passwordInput.value)) {
        // 显示错误消息
        msgSpan.innerHTML = "密码格式错误";
        // 阻止表单提交
        event.preventDefault();
        refreshCaptcha();
    } else if (passwordInput.value != password2Input.value) {
        // 显示错误消息
        msgSpan.innerHTML = "两次密码不一致";
        // 阻止表单提交
        event.preventDefault();
        refreshCaptcha();
    }
});