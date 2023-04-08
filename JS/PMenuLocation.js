//위치
//PMenuLocation.kt

var express = require('express');
var app = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');

app.use(bodyParser.json({ extended: true }));
app.use(bodyParser.urlencoded({ extended: true }));

app.listen(1047, () => {
    console.log('Sever is running 1047');
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

    var sql = 'SELECT u_div FROM user WHERE u_num = ?';
    var params = [ssaid];

    try {
        connection.query(sql, params, function(err, result){
            if(err)
                console.log(err);
            else{
                console.log('u_div 결과 ' + result);
                if(result.length > 0){
                    var vssaid = result[0].u_div;
                    console.log('취약계층 ssaid : ' + vssaid);
    
                    if(result[0].u_div == 'v'){
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
            }
        });
    } catch (error) {
        console.log('u_div SELECT 중 오류 : ' + error);
        res.json({
            msg: 'u_div SELECT 실패'
        });
    }
});

app.post('/getLocation', function(req, res){
    ssaid = req.body.ssaid;
    width = req.body.width;
    vertical = req.body.vertical;

    var sql = 'INSERT INTO location(l_vnum, l_width, l_vertical) VALUES(?, ?, ?)';
    var params = [ssaid, width, vertical];

    try {
        connection.query(sql, params, function(err, result){
            if(err)
                console.log(err);
            else{
                res.json({
                    result : true
                });
            }
        });
    } catch (error) {
        console.log('위치 INSERT 중 오류 : ' + error);
        res.json({
            msg: '위치 INSERT 실패'
        });
    }
});

app.get('/vLocation', function(req, res){

    var sql = 'SELECT u_name, l_width, l_vertical '
        + 'FROM location JOIN user '
        + 'ON location.l_vnum = user.u_num '
        + 'WHERE l_vnum = ? ORDER BY l_req DESC LIMIT 1';
    var params = [ssaid];

    try {
        connection.query(sql, params, function(err, result){
            if(err)
                console.log(err);
            else{
                console.log('위치 결과 ' + result);
                if(result.length > 0){
                    res.send({
                        result
                    });
                }else{
                    res.json({
                        msg: "GPS 확인"
                    });
                }
            }
        });
    } catch (error) {
        console.log('최근 위치 SELECT 중 오류 : ' + error);
        res.json({
            msg: '최근 위치 SELECT 실패'
        });
    }
});