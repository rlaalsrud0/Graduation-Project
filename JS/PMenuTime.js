//귀가 시간 설정
//PMenuTime.kt

var express = require('express');
var app = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');

app.use(bodyParser.json({extended: true}));
app.use(bodyParser.urlencoded({extended: true}));

app.listen(1038,()=>{
    console.log('Sever is running 1038');
});

var connection = mysql.createConnection({
    host: 'ollie.whitehat.kr',
    user: 'admin',
    database: 'ollie',
    password: 'a20192020',
    port: 3306
});

console.log("db연결");


app.post('/aa', function(req, res){
    vname = req.body.vname;  //원래는 vnum이 맞는데 여기서는 그냥 vname으로 가겠음
    hour = req.body.hour;
    min = req.body.min;
    active = req.body.active;

    console.log(vname);

    console.log("active : " + active);

    time = hour + " " + min;

    var sql = 'SELECT u_num FROM user WHERE u_name = ?';
    var params = [vname];

    try {
        connection.query(sql, params, function(err, result){
            if(err)
                console.log(err);
            else{
                console.log(result);
                if(result.length > 0){
                    var ssaid = result[0].u_num; //맞는 지 확인 필요
    
                    var sql1 = 'INSERT INTO time VALUES (?, ?, ?)';
                    var params1 = [ssaid, time, active];
    
                    connection.query(sql1, params1, function(err, result){
                        if(err) console.log(err);
                        else{
                            console.log(result);
                            res.json({
                                result : true
                            });
    
                            //백그라운드로 돌게 수정
    
                        }
                    });
                }
            }
        });
    } catch (error) {
        console.log('귀가 시간 설정 중 오류' + error);
        res.json({
            msg: '귀가 시간 설정 실패'
        });
    }
});