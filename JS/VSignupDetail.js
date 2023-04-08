//취약계층 회원가입 세부
//VSignupDetail.kt

var express = require('express');
var app = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');

app.use(bodyParser.json({extended: true}));
app.use(bodyParser.urlencoded({extended: true}));

app.listen(1046,()=>{
    console.log('Sever is running 1046');
});

var connection = mysql.createConnection({
    host: 'hostname',
    user: 'admin',
    database: 'databasename',
    password: 'password',
    port: 3306
});

console.log("db연결")
//회원가입
app.post('/signup_detail', function(req, res){
    var ssaid = req.body.ssaid;
    if(req.body.birth.length > 20){
        res.json({
            msg: '생일은 20자 이하'
        });
    }else{
        var birth = req.body.birth;
    }

    if(req.body.gender.length > 5){
        res.json({
            msg: '성별은 5자 이하'
        });
    }else{
        var gender = req.body.gender;
    }

    if(req.body.height.length > 10){
        res.json({
            msg: '키는 10자 이하'
        });
    }else{
        var height = req.body.height;
    }

    if(req.body.weight.length > 10){
        res.json({
            msg: '몸무게는 10자 이하'
        })
    }else{
        var weight = req.body.weight;
    }    

    console.log('ssaid : ' + ssaid + ', birth : ' + birth + ', gender : ' + gender + ', height : ' + height + ', weight : ' + weight);

    var sql = 'UPDATE user SET u_birth = ?, u_gender = ?, u_height = ?, u_weight = ? WHERE u_num = ?';
    var params = [birth, gender, height, weight, ssaid];

    try {
        connection.query(sql, params, function(err, result){
            if(err)
                console.log(err);
            else{

                res.json({
                    result : true,
                    msg : '회원가입에 성공했습니다.'
                });
            }
        });
    } catch (error) {
        console.log('취약계층 회원가입 세부 UPDATE 중 오류 : ' + error);
        res.json({
            msg: '취약계층 회원가입 세부 UPDATE 실패'
        });
    }
});
