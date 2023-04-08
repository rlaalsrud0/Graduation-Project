//사용자의 정보 반환, 시간 선택에서 스피너에 취약계층 리스트 반환
//PMenuSelectTime.kt

var express = require('express');
var app = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');

app.use(bodyParser.json({ extended: true }));
app.use(bodyParser.urlencoded({ extended: true }));

app.listen(1037, () => {
    console.log('Sever is running 1037');
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

    console.log('post : ' + ssaid);
    res.json({
        result: true,
    });
});

//해당하는 정보 읽어오기
app.get('/vselect', function (req, res) {
    var sql = 'SELECT u_name FROM user '
                +'WHERE u_num in (SELECT m_vnum FROM mapping WHERE m_pnum = ?)'
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
        console.log('취약계층 이름 SELECT 중 오류 : ' + error);
        res.json({
            msg: '취약계층 이름 SELECT 실패'
        });
    }
});