//마이페이지 - 회원 탈퇴
//MypageDelete.kt

var express = require('express');
var app = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');

app.use(bodyParser.json({ extended: true }));
app.use(bodyParser.urlencoded({ extended: true }));

app.listen(1027, () => {
    console.log('Sever is running 1027');
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
var div = "";

//ssaid 값 받기
app.post('/send', function (req, res) {
    ssaid = req.body.ssaid;

    console.log('post : ' + ssaid);

    var sql = 'SELECT u_div FROM user WHERE u_num = ?';
    var params = [ssaid];

    try {
        connection.query(sql, params, function (err, result) {
            if (err)
                console.log(err);
            else {
                console.log(result);
                if (result.length > 0) {
                    div = result[0].u_div;
                }
            }
        })
    } catch (error) {
        console.log('SELECT u_div 중 오류 : ' + error);
        res.json({
            msg: 'SELECT u_div 실패'
        });
    }
    
    if (div == 'p') {
        //mapping테이블에서 해당 값 삭제(보호자)
        var sql = 'DELETE FROM mapping WHERE m_pnum = ?';
        var params = [ssaid];

        try {
            connection.query(sql, params, function (err, result) {
                if (err)
                    console.log(err);
                else {
                    res.json({
                        result: true,
                        msg: '보호자 mapping테이블 삭제 완료'
                    });
                }
            });
        } catch (error) {
            console.log('보호자 DELETE mapping 중 오류 : ' + error);
            res.json({
                msg: '보호자 DELETE mapping 실패'
            });
        }
    }
    else if (div == 'v') {
        //mapping테이블에서 해당 값 삭제(취약계층)
        var sql1 = 'DELETE FROM mapping WHERE m_vnum = ?';
        var params1 = [ssaid];

        try {
            connection.query(sql1, params1, function(err, result){
                if(err)
                    console.log(err);
                else{
                    res.json({
                        result: true,
                        msg: '취약계층 mapping테이블 삭제 완료'
                    });
                }
            });
        } catch (error) {
            console.log('취약계층 DELETE mapping 중 오류 : ' + error);
            res.json({
                msg: '취약계층 DELETE mapping 실패'
            });
        }
    }

    //user테이블에서 해당 값 삭제
    var sql2 = 'DELETE FROM user WHERE u_num = ?';
    var params2 = [ssaid];

    try {
        connection.query(sql2, params2, function (err, result) {
            if (err)
                console.log(err);
            else {
                res.json({
                    result: true,
                    msg: '보호자 회원탈퇴 완료'
                });
            }
        });
    } catch (error) {
        console.log('DELETE user 중 오류 : ' + error);
        res.json({
            msg: 'DELETE user 실패'
        });
    }
    // res.json({
    //     result: true,
    // });

});