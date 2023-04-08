//회원가입
//Signup.kt

var express = require('express');
var app = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');

app.use(bodyParser.json({extended: true}));
app.use(bodyParser.urlencoded({extended: true}));

app.listen(1041,()=>{
    console.log('Sever is running 1041');
});

var connection = mysql.createConnection({
    host: 'ollie.whitehat.kr',
    user: 'admin',
    database: 'ollie',
    password: 'a20192020',
    port: 3306
});

console.log("db연결")
//회원가입
app.post('/signup2', function(req, res){
    var ssaid = req.body.ssaid;
    if(req.body.name.length > 10){
        res.json({
            msg: '이름은 최대 한글 5글자, 영어 10글자'
        });
    }else{
        var name = req.body.name;
    }
    
    if(req.body.phone.length > 20){
        res.json({
            msg: '전화번호는 20자를 넘지 않음'
        });
    }else{
        var phone = req.body.phone;
    }

    if(req.body.passwd.length > 20){
        res.json({
            msg: '비밀번호는 20자를 넘지 않음'
        });
    }else{
        var passwd = req.body.passwd;
    }
    var div = req.body.div;

    var sql = 'INSERT INTO user (u_num, u_name, u_phone, u_passwd, u_div) VALUES (?, ?, ?, ?, ?)';
    var params = [ssaid, name, phone, passwd, div];

    try {
        connection.query(sql, params, function(err, result){
            if(err)
                console.log(err);
            else{
                res.json({
                    result : true,
                    msg : '회원가입에 성공했습니다.'
                })
            }
        });
    } catch (error) {
        console.log('user INSERT 중 오류 : ' + error);
        res.json({
            msg: 'user INSERT 실패'
        });
    }
});