//모든 보호자 목록
//VListPro.kt

var express = require('express');
var app = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');

app.use(bodyParser.json({ extended: true }));
app.use(bodyParser.urlencoded({ extended: true }));

app.listen(1045, () => {
    console.log('Sever is running 1045');
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

//ssaid 값 받기
app.post('/send', function (req, res) {
    ssaid = req.body.ssaid;

    console.log('post : ' + ssaid);
    res.json({
        result: true,
    });
});

//보호자 이름 읽어오기
app.get('/pname', function (req, res) {

    //보호자
    var sql = 'SELECT u_name FROM user '
            + 'WHERE u_num IN (SELECT m_pnum FROM mapping WHERE m_vnum = ?)';
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
        console.log('보호자 목록 SELECT 중 오류 : ' + error);
        res.json({
            msg: '보호자 목록 SELECT 실패'
        });
    }
});