//로그인
//Login.kt

var express = require('express');
var app = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');

app.use(bodyParser.json({ extended: true }));
app.use(bodyParser.urlencoded({ extended: true }));

app.listen(1026, () => {
    console.log('Sever is running 1026');
});

var connection = mysql.createConnection({
    host: 'ollie.whitehat.kr',
    user: 'admin',
    database: 'ollie',
    password: 'a20192020',
    port: 3306
});

console.log("db연결");

//로그인
app.post('/login', function (req, res) {
    ssaid = req.body.ssaid;
    if (req.body.name.length > 10) {
        res.json({
            msg: '이름은 최대 한글 5글자, 영어 10글자'
        });
    } else {
        var name = req.body.name;
    }

    if (req.body.phone.length > 20) {
        res.json({
            msg: '전화번호는 20자를 넘지 않음'
        });
    } else {
        var phone = req.body.phone;
    }

    if (req.body.passwd.length > 20) {
        res.json({
            msg: '비밀번호는 20자를 넘지 않음'
        });
    } else {
        var passwd = req.body.passwd;
    }

    var sql = 'SELECT * FROM user WHERE u_name = ? AND u_phone = ? AND u_passwd = ?';
    var params = [name, phone, passwd];

    try {
        connection.query(sql, params, function (err, result) {
            if (err) {
                console.log(err);
            }
            else {
                if (result.length > 0) {
                    console.log('모든 select 결과 : ' + JSON.stringify(result));
                    res.status(200).json({
                        result: true
                    });
                }
                else {
                    console.log('로그인 실패');
                    res.status(400).json({
                        result: false
                    });
                }
            }
        });
    } catch (error) {
        console.log('SELECT 중 오류 : ' + error);
        res.json({
            msg: 'SELECT 실패'
        });
    }
});

//구분 가져오기
app.get('/loginData', function (req, res) {
    console.log('/loginData가 실행되긴 한건가?');
    console.log('ssaid값 확인' + ssaid);
    
    var sql = "SELECT u_div FROM user WHERE u_num = ?";
    var params = [ssaid];

    connection.query(sql, params, function (err, result) {
        if (err)
            console.log(err);
        else {
            if (result.length > 0) {
                console.log('구분 : ' + JSON.stringify(result));
                if (result[0].u_div === 'p') {
                    console.log('p');
                    res.json({
                        // result: true
                        result: [{"u_div":"보호자"}]
                        //msg: '보호자'
                    });
                    console.log('보호자 로그인 성공');
                } else {
                    console.log('v');
                    res.json({
                        // result: true
                        result: [{"u_div":"취약계층"}]
                        //msg: '취약계층'
                    });
                    console.log('취약계층 로그인 성공');
                }
            }
        }
    });
});