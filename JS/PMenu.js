//상단 메뉴바 이름 가져오기
//PMenu.kt

var express = require('express');
var app = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');

app.use(bodyParser.json({ extended: true }));
app.use(bodyParser.urlencoded({ extended: true }));

app.listen(1032, () => {
    console.log('Sever is running 1032');
});

var connection = mysql.createConnection({
    host: 'hostname',
    user: 'admin',
    database: 'databasename',
    password: 'password',
    port: 3306
});

console.log("db연결")

var ssaid = ""

//ssaid 값 받기
app.post('/send', function (req, res) {
    ssaid = req.body.ssaid;

    console.log('post : ' + ssaid);
    res.json({
        result: true,
    });
});

//DB의 값과 일치한지 확인
app.get('/PMenuN', function (req, res) {

    //해당하는 ssaid의 DB 값 가져오기
    var sql = 'SELECT u_name FROM user WHERE u_num = ?';
    var params = [ssaid];

    try {
        connection.query(sql, params, function (err, result) {
            if (err)
                console.log(err);
            else {
                console.log(result);
                if (result.length > 0) {
                    res.send({
                        result
                    });
                    console.log(result);
                }
            }
        });
    } catch (error) {
        console.log('이름 SELECT 중 오류 : ' + error);
        res.json({
            msg: '이름 SELECT 실패'
        });
    }
});
