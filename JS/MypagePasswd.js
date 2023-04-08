//비밀번호 일치한지 확인
//MypagePasswd.kt

var express = require('express');
var app = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');

app.use(bodyParser.json({ extended: true }));
app.use(bodyParser.urlencoded({ extended: true }));

app.listen(1028, () => {
    console.log('Sever is running 1028');
});

var connection = mysql.createConnection({
    host: 'ollie.whitehat.kr',
    user: 'admin',
    database: 'ollie',
    password: 'a20192020',
    port: 3306
});

console.log("db연결");

var ssaid = "";
var passwd = "";

//ssaid, passwd 값 받기
app.post('/sendData', function (req, res) {
    ssaid = req.body.ssaid;
    if(req.body.passwd.length > 20){
        res.json({
            msg: '비밀번호는 20자를 넘지 않음'
        });
    }else{
        passwd = req.body.passwd;
    }

    console.log('post : ' + ssaid + ', ' + passwd);
    res.json({
        result: true,
    });
});

//일치하는 정보가 있는지 읽어오기
app.get('/SelectPasswd', function (req, res) {

    var sql = 'SELECT * FROM user '
            + 'WHERE u_num = ? AND u_passwd = ?';
    var params = [ssaid, passwd];

    console.log('get   :   ' + ssaid + ', ' + passwd);

    try {
        connection.query(sql, params, function (err, result) {
            console.log('쿼리문 실행');
            if (err)
                console.log(err);
            else {
                console.log(result);
                if (result.length > 0) {
                    console.log('get : ' + result);
                    res.json({
                        result: true
                    });
                }
                else{
                    res.json({
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