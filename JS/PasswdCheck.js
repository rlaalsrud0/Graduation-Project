//비밀번호 찾기
//PasswdCheck.kt

var express = require('express');
var app = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');

app.use(bodyParser.json({ extended: true }));
app.use(bodyParser.urlencoded({ extended: true }));

app.listen(1029, () => {
    console.log('Sever is running 1029');
});

var connection = mysql.createConnection({
    host: 'hostname',
    user: 'admin',
    database: 'databasename',
    password: 'password',
    port: 3306
});

console.log("db연결");

var ssaid = "";
var name = "";
var phone = "";

var dname = "";
var dphone = "";

var random = "";

//ssaid, name, phone 값 받기
app.post('/sendPasswdInfo', function (req, res) {
    ssaid = req.body.ssaid;
    if (req.body.name.length > 10) {
        res.json({
            result: false,
            msg: '이름은 최대 한글 5글자, 영어 10글자'
        });
    } else {
        name = req.body.name;
    }

    if (req.body.phone.length > 20) {
        res.json({
            result: false,
            msg: '전화번호는 20자를 넘지 않음'
        });
    } else {
        phone = req.body.phone;
    }

    console.log('post : ' + ssaid);
    console.log('post : ' + name);
    console.log('post : ' + phone);

    res.json({
        result: true,
    });
});

//DB의 값과 일치한지 확인
app.get('/passwd_check', function (req, res) {

    //해당하는 ssaid의 DB 값 가져오기
    var sql = 'SELECT u_name, u_phone FROM user WHERE u_num = ?';
    var params = [ssaid];

    try {
        connection.query(sql, params, function (err, result) {
            if (err)
                console.log(err);
            else {
                //입력받은 값과 DB의 값이 일치하는 지 확인
                dname = result[0].u_name;
                dphone = result[0].u_phone;

                if (name === dname && phone === dphone) {
                    //정보 일치
                    //랜덤 비밀번호 만들어서 전송
                    random = Math.random().toString(36).substr(2, 9);
                    console.log(random);

                    var sql1 = 'UPDATE user SET u_passwd = ? WHERE u_num = ?'
                    var params1 = [random, ssaid];

                    connection.query(sql1, params1, function (err, result) {
                        if (err)
                            console.log(err);
                        else {
                            res.json({
                                result: true,
                                passwd: random,
                                msg: '비밀번호 수정 완료'
                            });
                        }
                    });

                }else {
                    //정보 불일치
                    //400보내기
                    res.json({
                        result: false,
                        msg: '정보 불일치'
                    });
                }
            }
        });
    } catch (error) {
        console.log('비밀번호 찾기 중 오류 : ' + error);
        res.json({
            msg: '비밀번호 찾기 실패'
        });
    }

});
