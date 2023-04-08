//영상 모아보기
//PMenuVideo.kt

var express = require('express');
var app = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');
const e = require('express');

app.use(bodyParser.json({ extended: true }));
app.use(bodyParser.urlencoded({ extended: true }));

app.listen(1039, () => {
    console.log('Sever is running 1039');
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
var outList = [];
var inList = [];

//이름으로 ssaid 값 받기
app.post('/sendName', function (req, res) {
    if(req.body.name.length > 10){
        res.json({
            msg: '이름은 최대 한글 5글자, 영어 10글자'
        });
    }else{
        name = req.body.name;
    }
    
    console.log("post 이름 : " + name);

    var sql = 'SELECT u_num FROM user WHERE u_name = ?';
    var params = [name];

    try {
        connection.query(sql, params, function(err, result){
            if(err)
                console.log(err);
            else{
                console.log(result);
                if (result.length > 0) {
                    ssaid = result[0].u_num;
                }
                console.log(ssaid);
            }
        });
    } catch (error) {
        console.log('ssaid SELECT 중 오류 : ' + error);
        res.json({
            msg: 'ssaid SELECT 실패'
        });
    }
    
    res.json({
        result: true,
    });
});

//외출 정보 읽어오기
app.get('/outList', function (req, res) {
    var sql = "SELECT replace(h_odate,'T',' ') as h_odate, h_oroute FROM home WHERE h_num = ?";
    var params = [ssaid];

    try {
        connection.query(sql, params, function (err, result) {
            if (err)
                console.log(err);
            else {
                console.log(result);
                if (result.length > 0) {
                    outList = result;
                    console.log("video 결과: " + outList);
                }
                res.send({
                    outList
                });
            }
        });
    } catch (error) {
        console.log('외출 영상 SELECT 중 오류 : ' + error);
        res.json({
            msg: '외출 영상 SELECT 실패'
        });
    }
    
    //초기화
    outList.length = 0;
});

//귀가 정보 읽어오기
app.get('/inList', function (req, res) {

    var sql = "SELECT replace(h_idate,'T',' ') as h_idate, h_iroute FROM home "
            + "WHERE h_num = ? and h_iroute is not null";
    var params = [ssaid];

    try {
        connection.query(sql, params, function (err, result) {
            if (err)
                console.log(err);
            else {
                console.log(result);
                if (result.length > 0) {
                    inList = result;
                    console.log("video 결과: " + inList);
                }
            }
        });
    } catch (error) {
        console.log('귀가 영상 SELECT 중 오류 : ' + error);
        res.json({
            msg: '귀가 영상 SELECT 실패'
        });
    }

    res.send({
        inList
    });

    //초기화
    inList.length = 0;
});
