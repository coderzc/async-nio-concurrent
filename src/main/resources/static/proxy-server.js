//引入http，模块
var http = require('http');
//引入url模块 解析请求的url
var url = require('url');
//引入IO文件模块 操作文件
var fs = require('fs');

//创建server 并指定服务器请求响应函数
http.createServer(function (req, res) {
    var pathname = url.parse(req.url).pathname;
    if (pathname == '/') {
        //请求 index.html 返回请求状态为200
        var data = fs.readFileSync('./index.html').toString();//读取文件内容并转换为字符串
        res.writeHead(200, {'Content-Type': 'text/html; charset=utf-8'});
        res.end(data);
    } else {
        console.log('hello world');
        //不是请求 index.html 返回请求状态为404
        res.writeHead(200, {'Content-Type': 'text/plain; charset=utf-8'});
        res.end('hello world');
    }
})
//指定服务器监听的端口
    .listen(3000);
console.log('服务器开启在：http://localhost:3000/');