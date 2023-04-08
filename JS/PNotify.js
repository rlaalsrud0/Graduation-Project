//신고 페이지, 외출 정보 페이지
//PNotify.kt, PMenuOut.kt

var express = require('express');
var app = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');
var moment = require('moment');

app.use(bodyParser.json({ extended: true }));
app.use(bodyParser.urlencoded({ extended: true }));

app.listen(1040, () => {
    console.log('Sever is running 1040');
});

var connection = mysql.createConnection({
    host: 'ollie.whitehat.kr',
    user: 'admin',
    database: 'ollie',
    password: 'a20192020',
    port: 3306
});

console.log("db연결")

var ssaid = "";
var h_seq = "";

//취약계층의 ssaid 받기
app.post('/send', function (req, res) {
    ssaid = req.body.ssaid;

    console.log('post : ' + ssaid);
    res.json({
        result: true,
    });
});

//해당하는 영상 경로와 취약계층의 정보 가져오기
app.get('/getVInfo', function (req, res) {
    var sql = 'SELECT h_seq, h_oroute FROM home WHERE h_num = ? ORDER BY h_seq DESC limit 1';
    var params = [ssaid];

    try {
        connection.query(sql, params, function (err, result) {
            if (err)
                console.log(err);
            else {
                if (result.length > 0) {
                    var route = result[0].h_oroute;
                    h_seq = result[0].h_seq;
                    console.log(route);
    
                    var sql1 = 'SELECT u_name, u_birth, u_gender, u_phone FROM user WHERE u_num = ?';
                    var parmas1 = [ssaid];
    
                    connection.query(sql1, parmas1, function (err, result) {
                        if (err)
                            console.log(err);
                        else {
                            if (result.length > 0) {
                                console.log(result);
                                
                                //result += route;
                                
                                res.send({
                                    result, route
                                });
                            }
                            else {
                                res.json({
                                    result: false,
                                    msg: '해당하는 정보가 없습니다.'
                                });
                            }
                        }
                    });
                }
                else {
                    res.json({
                        result: false,
                        msg: '외출 영상이 없습니다.'
                    });
                }
            }
        });
    } catch (error) {
        console.log('외출 영상, 취약계층 정보 SELECT 중 오류 : ' + error);
        res.json({
            msg: '외출 영상, 취약계층 정보 SELECT 실패'
        });
    }
});

//신고 버튼 눌렀을 때
app.post('/report', function (req, res) {
    //현재시간
    var date = moment().format('YYYY-MM-DD HH:mm:ss');
    //구분
    var div = 'N';

    var sql = 'UPDATE home SET h_idate = ?, h_div = ? WHERE h_seq = ?';
    var params = [date, div, h_seq];

    try {
        connection.query(sql, params, function (err, result) {
            if (err)
                console.log(err);
            else {
                res.status(200).json({
                    result: true
                });
            }
        });
    } catch (error) {
        console.log('신고버튼 UPDATE 중 오류 : ' + error);
        res.json({
            msg: '신고버튼 UPDATE 실패'
        });
    }
});