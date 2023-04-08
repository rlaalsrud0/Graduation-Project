//모든 사용자 목록
//PMenuListUser.kt

var express = require('express');
var app = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');

app.use(bodyParser.json({ extended: true }));
app.use(bodyParser.urlencoded({ extended: true }));

app.listen(1035, () => {
    console.log('Sever is running 1035');
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

//device 값 받기
app.post('/devicesend', function (req, res) {
    if(req.body.device.length > 20){
        res.json({
            msg: '디바이스는 20자 이하'
        });
    }else{
        device = req.body.device;
    }

    console.log('post : ' + device);
    res.json({
        result: true,
    });
});

//해당하는 정보 읽어오기
app.get('/puser', function (req, res) {

    //보호자
    var sql = 'SELECT u_name FROM user '
            +'WHERE u_num IN (SELECT m_pnum FROM mapping WHERE device = ?)';
    
    var params = [device];

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
        console.log('보호자 이름 SLEECT 중 오류 : ' + error);
        res.json({
            msg: '보호자 이름 SELECT 실패'
        });
    }
});

app.get('/vuser', function(req, res){

    //취약계층
    var sql1 = 'SELECT u_name FROM user '
            +'WHERE u_num IN (SELECT m_vnum FROM mapping WHERE device = ?)';

    var params1 = [device];

    try {
        connection.query(sql1, params1, function (err, result){
            if(err) console.log(err);
            else{
                console.log(result);
                if(result.length > 0){
                    res.send({
                        result
                    });
                    console.log(result);
                }
            }
        });
    } catch (error) {
        console.log('취약계층 이름 SLEECT 중 오류' + error);
        res.json({
            msg: '취약계층 이름 SLEECT 실패'
        });
    }
});