//디바이스 삭제
//PMenuDeviceDelete.kt

var express = require('express');
var app = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');
const e = require('express');

app.use(bodyParser.json({ extended: true }));
app.use(bodyParser.urlencoded({ extended: true }));

app.listen(1033, () => {
    console.log('Sever is running 1033');
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
//ssaid 값도 받아야 한다.
app.post('/sendDevice', function (req, res) {
    ssaid = req.body.ssaid;
    if(req.body.device.length > 20){
        res.json({
            msg: '디바이스는 20자 이하'
        });
    }else{
        device = req.body.device;
    }

    console.log('post : ' + ssaid + ', ' + device);

    //mapping 테이블에서 해당 값 삭제
    var sql = 'DELETE FROM mapping WHERE m_pnum = ? and device = ?';
    var params = [ssaid, device];

    try {
        connection.query(sql, params, function (err, result) {
            if (err)
                console.log(err);
            else {
                //mapping테이블에 이 디바이스와 연결되어 있는 다른 사람들이 있는지 확인한다.
                //매핑이 되어 있는 다른 사람들이 있다면 home테이블과 hw테이블에서 삭제하지 않는다.
                //매핑이 되어 있는 다른 사람들이 없다면 home테이블과 hw테이블에서 이 디바이스를 삭제한다.
                var sql1 = 'SELECT m_pnum FROM mapping WHERE device = ?';
                var params1 = [device];
    
                connection.query(sql1, params1, function (err, result) {
                    if (err)
                        console.log(err);
                    else {
                        console.log(result);
                        if (result.length > 0) {
                            console.log('매핑되어 있는 사람이 있음');
                        } else {
                            console.log('매핑되어 있는 사람이 없음');
                            //home 테이블에서 해당 값 삭제
                            var sql3 = 'DELETE FROM home WHERE device = ?';
                            var params3 = [device];
    
                            connection.query(sql3, params3, function (err, result) {
                                if (err)
                                    console.log(err);
                                else {
                                    //hw 테이블에서 해당 값 삭제
                                    var sql4 = 'DELETE FROM hw WHERE device = ?';
                                    var params4 = [device];
    
                                    connection.query(sql4, params4, function (err, result) {
                                        if (err)
                                            console.log(err);
                                        else {
                                            res.json({
                                                result: true,
                                                msg: '디바이스 삭제 완료'
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });
    } catch (error) {
        console.log('디바이스 DELETE 중 오류 : ' + error);
        res.json({
            msg: '디바이스 DELETE 삭제'
        });
    }
});