//디바이스 계정 목록
//PDevice.kt

var express = require('express');
var app = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');

app.use(bodyParser.json({ extended: true }));
app.use(bodyParser.urlencoded({ extended: true }));

app.listen(1030, () => {
    console.log('Sever is running 1030');
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

//ssaid 값 받기
app.post('/send', function (req, res) {
    ssaid = req.body.ssaid;

    console.log('post : ' + ssaid);
    res.json({
        result: true,
    });
});

//해당하는 정보 읽어오기
app.get('/device', function (req, res) {

    var sql = 'SELECT u_name, device FROM user JOIN mapping '
                + 'ON user.u_num = mapping.m_vnum '
                + 'WHERE device IN (SELECT device FROM mapping WHERE m_pnum = ?)';
    
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
                    })
                    console.log(result)
                }
            }
        });
    } catch (error) {
        console.log('디바이스 정보 SELECT 중 오류 : ' + error);
        res.json({
            msg: '디바이스 정보 SELECT 실패'
        });
    }
});
