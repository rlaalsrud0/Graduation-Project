//모든 취약계층 목록
//PMenuMypageListVul.kt

var express = require('express');
var app = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');

app.use(bodyParser.json({ extended: true }));
app.use(bodyParser.urlencoded({ extended: true }));

app.listen(1036, () => {
    console.log('Sever is running 1036');
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

//취약계층 이름 읽어오기
app.get('/vname', function (req, res) {

    //취약계층
    var sql = 'SELECT DISTINCT u_name, device FROM user JOIN mapping '
            + 'ON user.u_num = mapping.m_vnum '
            + 'WHERE u_num IN (SELECT m_vnum FROM mapping WHERE m_pnum = ?)';
    
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
        console.log('취약계층 목록 SELECT 중 오류 : ' + error);
        res.json({
            msg: '취약계층 목록 SELECT 실패'
        });
    }
});
