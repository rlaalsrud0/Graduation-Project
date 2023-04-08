//보호자, 취약계층 정보수정
//PMenuMypageUpdate.kt, VMypageUpdate.kt

var express = require('express');
var app = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');

app.use(bodyParser.json({ extended: true }));
app.use(bodyParser.urlencoded({ extended: true }));

app.listen(1043, () => {
    console.log('Sever is running 1043');
});

var connection = mysql.createConnection({
    host: 'hostname',
    user: 'admin',
    database: 'databasename',
    password: 'password',
    port: 3306
});

console.log("db연결")

var ssaid = ""

//ssaid 값 받기
app.post('/send', function (req, res) {
    ssaid = req.body.ssaid;

    console.log('post : ' + ssaid);
    res.json({
        result: true,
    });
});

//해당하는 정보 읽어오기
app.get('/mypages', function (req, res) {
    console.log('get : ' + ssaid);

    var sql = 'SELECT * FROM user WHERE u_num = ?'
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
        console.log('정보 수정을 위한 SELECT 중 오류 : ' + error);
        res.json({
            msg: '정보 수정을 위한 SELECT 실패'
        });
    }
});

//보호자 정보 수정
app.post('/pmypageu', function (req, res) {
    var pssaid = req.body.pssaid;
    if(req.body.pname.length > 10){
        res.json({
            msg: '이름은 최대 한글 5글자, 영어 10글자'
        });
    }else{
        var pname = req.body.pname;
    }
    
    if(req.body.pphone.length > 20){
        res.json({
            msg: '전화번호는 20자를 넘지 않음'
        });
    }else{
        var pphone = req.body.pphone;
    }

    if(req.body.ppasswd.length > 20){
        res.json({
            msg: '비밀번호는 20자를 넘지 않음'
        });
    }else{
        var ppasswd = req.body.ppasswd;
    }

    var sql = 'UPDATE user SET u_name = ?, u_phone = ?, u_passwd = ? WHERE u_num = ?';
    var params = [pname, pphone, ppasswd, pssaid];

    try {
        connection.query(sql, params, function (err, result) {
            if (err)
                console.log(err);
            else {
                res.json({
                    result: true,
                    msg: '보호자 정보수정 완료'
                })
            }
        });
    } catch (error) {
        console.log('보호자 정보 UPDATE 중 오류 : ' + error);
        res.json({
            msg: '보호자 정보 UPDATE 실패'
        });
    }
});

//취약계층 정보 수정
app.post('/vmypageu', function (req, res) {
    var vssaid = req.body.vssaid;
    if(req.body.vname.length > 10){
        res.json({
            msg: '이름은 최대 한글 5글자, 영어 10글자'
        });
    }else{
        var vname = req.body.vname;
    }
    
    if(req.body.vphone.length > 20){
        res.json({
            msg: '전화번호는 20자를 넘지 않음'
        });
    }else{
        var vphone = req.body.vphone;
    }

    if(req.body.vpasswd.length > 20){
        res.json({
            msg: '비밀번호는 20자를 넘지 않음'
        });
    }else{
        var vpasswd = req.body.vpasswd;
    }

    if(req.body.vbirth.length > 20){
        res.json({
            msg: '생일은 20자 이하'
        });
    }else{
        var vbirth = req.body.vbirth;
    }

    if(req.body.vheight.length > 10){
        res.json({
            msg: '키는 10자 이하'
        });
    }else{
        var vheight = req.body.vheight;
    }

    if(req.body.vweight.length > 10){
        res.json({
            msg: '몸무게는 10자 이하'
        })
    }else{
        var vweight = req.body.vweight;
    }    

    var sql = 'UPDATE user SET u_name = ?, u_phone = ?, u_passwd = ?, u_birth = ?, u_height = ?, u_weight = ? WHERE u_num = ?';
    var params = [vname, vphone, vpasswd, vbirth, vheight, vweight, vssaid];

    try {
        connection.query(sql, params, function (err, result) {
            if (err)
                console.log(err);
            else {
                res.json({
                    result: true,
                    msg: '취약계층 정보수정 완료'
                })
            }
        });
    } catch (error) {
        console.log('취약계층 정보 UPDATE 중 오류 : ' + error);
        res.json({
            msg: '취약계층 정보 UPDATE 실패'
        });
    }
});
