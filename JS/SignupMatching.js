//보호자 - 취약계층 매핑
//SignupMatching.kt

var express = require('express');
var app = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');

app.use(bodyParser.json({extended: true}));
app.use(bodyParser.urlencoded({extended: true}));

app.listen(1042,()=>{
    console.log('Sever is running 1042');
});

var connection = mysql.createConnection({
    host: 'ollie.whitehat.kr',
    user: 'admin',
    database: 'ollie',
    password: 'a20192020',
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

//매칭
app.post('/matching', function(req, res){
    var ssaid = req.body.ssaid;
    if(req.body.device.length > 20){
        res.json({
            msg: '디바이스는 20자 이하'
        });
    }else{
        var device = req.body.device;
    }

    if(req.body.passwd.length > 20){
        res.json({
            msg: '비밀번호는 20자를 넘지 않음'
        });
    }else{
        var passwd = req.body.passwd;
    }

    var sql = 'SELECT * FROM hw WHERE device = ? and hw_passwd = ?';
    var params = [device, passwd];

    try {
        connection.query(sql, params, function(err, result){
            if(err)
                console.log(err);
            else{
                var sql1 = 'SELECT u_div FROM user WHERE u_num = ?';
                var params1 = [ssaid];
    
                connection.query(sql1, params1, function(err, result){
                    if(err) console.log(err);
                    else{
                        console.log('보호자/취약계층 구분 읽어옴');
                        console.log(result[0])
                        if(result[0].u_div === 'p'){
                            
                            //구분이 보호자일 경우
                            console.log('p');
    
                            //여기에 같은 디바이스를 사용하는 취약계층이 있는지 체크해야 한다.
                            //있고 보호자도 있으면 새롭게 insert, 있고 보호자가 없으면 update
                            //없으면 새롭게 insert
                            var sql7 = 'SELECT distinct m_vnum FROM mapping WHERE m_vnum is not null AND device = ?';
                            var params7 = [device];
    
                            connection.query(sql7, params7, function(err, result){
                                if(err) console.log(err);
                                else{
                                    console.log('같은 디바이스를 사용하는 취약계층 유무');
    
    
                                    console.log(result);
                                    //for
                                    var arr = [];
                                    for(var i = 0; i < result.length; i++){
                                        arr.push(result[i].m_vnum);
                                    }
    
                                    // //var vs = result[0];
                                    // var vs = result[0].m_vnum;
                                    console.log(arr);
    
                                    if(result.length > 0){
                                        //같은 디바이스를 사용하는 보호자가 있는지 체크
                                        var sql6 = 'SELECT distinct m_pnum FROM mapping WHERE m_pnum is not null AND device = ?';
                                        var params6 = [device];
    
                                        connection.query(sql6, params6, function(err, result){
                                            if(err) console.log(err);
                                            else{
                                                console.log('같은 디바이스를 사용하는 보호자 유무');
                                                console.log(result);
                                                if(result.length > 0){
                                                    var sql8 = 'INSERT INTO mapping VALUES(?,?,?)';
                                                    //취약계층의 ssaid 가져와야 한다
                                                    //for
                                                    for(var i = 0; i < arr.length; i ++){
                                                        connection.query(sql8, [ssaid, arr[i], device], function(err, result){
                                                            if(err) console.log(err);
                                                            else{
                                                                console.log('보호자 mapping테이블에 insert 완료');
                                                            }
                                                        });
                                                    }
                                                }
                                                else{
                                                    var sql4 = 'UPDATE mapping SET m_pnum = ? WHERE device = ?';
                                                    var params4 = [ssaid, device];
    
                                                    connection.query(sql4, params4, function(err, result){
                                                        if(err) console.log(err);
                                                        else{
                                                            console.log('보호자 mapping테이블에 update 완료');
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                    else{
                                        var sql2 = 'INSERT INTO mapping(m_pnum, device) VALUES(?,?)';
                                        var params2 = [ssaid, device];
    
                                        connection.query(sql2, params2, function(err, result){
                                            if(err) console.log(err);
                                            else{
                                                console.log('보호자 mapping 테이블 insert 완료');
                                                // res.json({
                                                //     result : true,
                                                //     msg : '보호자 mapping테이블 insert 완료'
                                                // })
                                            }
                                        });
                                    }
                                }
                            });
                        }
                        else if(result[0].u_div === 'v'){
                            //구분이 취약계층일 경우
                            console.log('v')
    
                            //여기에 같은 디바이스를 사용하는 보호자가 있는지 체크해야 한다.
                            //있고 취약계층이 있으면 insert, 있고 취약계층은 없으면 update
                            //없으면 insert
                            var sql6 = 'SELECT distinct m_pnum FROM mapping WHERE m_pnum is not null AND device = ?';
                            var params6 = [device];
    
                            connection.query(sql6, params6, function(err, result){
                                if(err) console.log(err);
                                else{
                                    console.log('같은 디바이스를 사용하는 보호자 유무');
    
                                    console.log(result);
                                    //console.log(result[0].m_pnum);
                                    //for
                                    var arr1 = [];
                                    for(var i = 0; i < result.length; i++){
                                        arr1.push(result[i].m_pnum);
                                    }
                                    //var ps = result[0].m_pnum;
                                    //var ps = result[0];
                                    console.log(arr1);
                                    
                                    if(result.length > 0){
                                        var sql7 = 'SELECT distinct m_vnum FROM mapping WHERE m_vnum is not null AND device = ?';
                                        var params7 = [device];
    
                                        connection.query(sql7, params7, function(err, result){
                                            if(err) console.log(err);
                                            else{
                                                console.log('같은 디바이스를 사용하는 보호자가 있고, 취약계층 유무');
                                                console.log(result);
                                            
                                                if(result.length > 0){ // null이 아니면 insert
                                                    var sql8 = 'INSERT INTO mapping VALUES(?,?,?)';
                                                    //보호자의 ssaid 가져와야 한다
                                                    //for
                                                    for(var i = 0; i < arr1.length; i ++){
                                                        connection.query(sql8, [arr1[i], ssaid, device], function(err, result){
                                                            if(err) console.log(err);
                                                            else{
                                                                console.log('같은 디바이스를 사용하는 보호자가 있고, 취약계층도 이미 매칭 되어 있음');
                                                                console.log('취약계층 mapping 테이블에 새로 insert 완료');
                                                            }
                                                        });
                                                    }
                                                }
                                                else{
                                                    // 매핑테이블에 취약계층넘버 추가
                                                    var sql5 = 'UPDATE mapping SET m_vnum = ? WHERE device = ?';
                                                    var params5 = [ssaid, device];
    
                                                    connection.query(sql5, params5, function(err, result){
                                                        if(err) console.log(err);
                                                        else{
                                                            console.log('같은 디바이스를 사용하는 보호자는 있는데 취약계층은 비어있음');
                                                            console.log('mapping 테이블에 보호자 칸에 취약계층 넘버 update 완료');
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                    else{
                                        var sql3 = 'INSERT INTO mapping(m_vnum, device) VALUES(?,?)';
                                        var params3 = [ssaid, device];
    
                                        connection.query(sql3, params3, function(err, result){
                                            if(err) console.log(err);
                                            else{
                                                console.log('같은 디바이스를 사용하는 보호자가 없으면, insert');
                                                console.log('취약계층 mapping 테이블 insert 완료');
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    } catch (error) {
        console.log('매칭 중 오류 : ' + error);
        res.json({
            msg: '매칭 실패'
        });
    }
});

app.get('/ssaidpass', function(req, res){

    var sql = 'SELECT u_div FROM user WHERE u_num = ?';
    var params = [ssaid];

    try {
        connection.query(sql, params, function(err, result){
            if(err) console.log(err);
            else{
                console.log('ssaid 로 구분 보내기! ');
                
                //console.log(result[0].u_div);   //2022/9/14 김민경  이게 안 나온다?
                //console.log(result[0]);
    
                if(result[0].u_div === 'p'){
                    //구분이 보호자일 경우
                   // var div = 'p';
                    console.log('p');
                    res.send({
                        result
                    })
                }
                else if(result[0].u_div === 'v'){
                    //구분이 취약계층일 경우
                  //  var div = 'v';
                    console.log('v');
                    res.send({
                        result
                    })
                    console.log('div 보내기 성공');
                }
            }
        });
    } catch (error) {
        console.log('구분 보내기 중 오류 : ' + error);
        res.json({
            msg: '구분 보내기 실패'
        });
    }
});