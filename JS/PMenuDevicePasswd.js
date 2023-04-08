//디바이스의 비밀번호 일치한지 확인
//PMenuDevicePasswd.kt

var express = require('express');
var app = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');

app.use(bodyParser.json({ extended: true }));
app.use(bodyParser.urlencoded({ extended: true }));

app.listen(1034, () => {
    console.log('Sever is running 1034');
});

var connection = mysql.createConnection({
    host: 'ollie.whitehat.kr',
    user: 'admin',
    database: 'ollie',
    password: 'a20192020',
    port: 3306
});

console.log("db연결");

var device = "";
var passwd = "";

//device, passwd 값 받기
app.post('/sendDeviceData', function (req, res) {
    if(req.body.device.length > 20){
        res.json({
            msg: '디바이스는 20자 이하'
        });
    }else{
        device = req.body.device;
    }

    if(req.body.passwd.length > 20){
        res.json({
            msg: '비밀번호는 20자를 넘지 않음'
        });
    }else{
        passwd = req.body.passwd;
    }

    console.log('post : ' + device + ', ' + passwd);

    res.json({
        result: true,
    });

});

app.get('/SelectDevicePasswd', function(req, res){
    //일치하는 정보가 있는지 읽어오기
    var sql = 'SELECT * FROM hw '
            + 'WHERE device = ? AND hw_passwd = ?';
    var params = [device, passwd];

    console.log(device + passwd);

    try {
        connection.query(sql, params, function (err, result) {
            if (err)
                console.log(err);
            else {
                console.log(result);
                if (result.length > 0) {
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
        console.log('SELECT hw 중 오류 : ' + error);
        res.json({
            msg: 'SELECT hw 실패'
        });
    }
});