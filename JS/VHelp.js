//취약계층 도움 요청
//VHelp.kt

var express = require('express');
var app = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');
var moment = require('moment');

app.use(bodyParser.json({ extended: true }));
app.use(bodyParser.urlencoded({ extended: true }));

app.listen(1044, () => {
    console.log('Sever is running 1044');
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

//취약계층 ssaid 값 받기
app.post('/send', function (req, res) {
    ssaid = req.body.ssaid;

    console.log('post : ' + ssaid);
    // res.json({
    //     result: true,
    // });

    //현재시간
    var time = moment().format('YYYY-MM-DD HH:mm');

    var sql = 'INSERT INTO request(r_num, r_time, r_help) VALUES(?, ?, ?)';
    var params = [ssaid, time, 'Y'];

    try {
        connection.query(sql, params, function(err, result){
            if(err)
                console.log(err);
            else{
                res.json({
                    result : true,
                    msg : 'request테이블에 insert 성공'
                });
                console.log('request테이블에 insert 성공');
            }
        });
    } catch (error) {
        console.log('reqeust INSERT 중 오류 : ' + error);
        res.json({
            msg: 'request INSERT 실패'
        });
    }
});

var pList = [];

//알림 보내주는 거 백그라운드에서 돌려야 한다
//매핑 테이블에서 취약계층과 매핑되어 있는 보호자 정보 읽어오기
app.get('/help', function (req, res) {
    //var ssaid = req.body.ssaid;
    console.log('/help 실행');

    var date = moment().format('YYYY-MM-DD HH:mm');
    console.log(date);

    var sqlTest = 'SELECT r_num FROM request WHERE r_time = ? and r_help = ? ORDER BY r_seq DESC LIMIT 1';
    var paramsTest = [date, 'Y'];
    
    try {
        connection.query(sqlTest, paramsTest, function(err, result){
            if(err)
                console.log(err);
            else{
                if(result.length > 0){
                    console.log('r_num : ' + result);
                    var sql = 'SELECT distinct m_pnum FROM mapping WHERE m_vnum = ?';
                    var params = [result[0].r_num];
    
                    console.log(params);

                    connection.query(sql, params, function (err, result) {
                        if (err)
                            console.log(err);
                        else {
                            if (result.length > 0) {
                                console.log('m_pnum : ' + result);
                                pList = result;
                                // res.json({
                                //     result: true,
                                // });
                                res.send({
                                    pList
                                });
                                console.log(result);
                            }
                            else{
                                pList.length = 0;
                                res.json({
                                    result: false
                                });
                            }
                        }
                    });
                }
            }
        });
    } catch (error) {
        console.log('request테이블에 insert한 취약계층의 보호자 리스트 SELECT 중 오류 : ' + error);
        res.json({
            msg: 'request테이블에 insert한 취약계층의 보호자 리스트 SELECT 실패'
        });
    }
    pList.length = 0;
});


// app.get('/helpP', function(req, res){
//     console.log(pList);
//     res.send({
//         pList
//     })
// });