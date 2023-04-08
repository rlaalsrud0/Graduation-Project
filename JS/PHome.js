//보호자 홈
//PHome.kt

var express = require('express');
var app = express();
var mysql = require('mysql');
var bodyParser = require('body-parser');
var moment = require('moment');

app.use(bodyParser.json({ extended: true }));
app.use(bodyParser.urlencoded({ extended: true }));

app.listen(1051, () => {
    console.log('Sever is running 1051');
});

var connection = mysql.createConnection({
    host: 'hostname',
    user: 'admin',
    database: 'databasename',
    password: 'password',
    port: 3306
});

console.log('db연결');

var device = '';
var h_num = '';
var vssaid = [];

//device,ssaid 값 받기
app.post('/sendHome', function (req, res) {
    device = req.body.device;
    var ssaid = req.body.ssaid;
    console.log('device : ' + device);
    console.log('ssaid : ' + ssaid);

    var sql = 'SELECT m_vnum FROM mapping WHERE m_pnum = ? and device = ? ';
    var params = [ssaid, device];

    try {
        connection.query(sql, params, function (err, result) {
            if (err)
                console.log(err);
            else {
                if (result.length > 0) {
                    console.log('h_seq, h_num 결과: ' + JSON.stringify(result));
                    console.log(result);

                    for (var i = 0; i < result.length; i++) {
                        vssaid.push(result[i].m_vnum);
                    }
                    console.log('취약계층의 ssaid : ' + vssaid);
                    res.status(200).json({
                        result: true
                    });
                } else {
                    res.status(400).json({
                        result: false
                    });
                }
            }
        });
    } catch (error) {
        console.log('디바이스로부터 정보 SELECT 중 오류 : ' + error);
        res.json({
            msg: '디바이스로부터 정보 SELECT 실패'
        });
    }
});


//백그라운드
//설정한 시간되면 귀가했는지 확인해서 귀가 안 했다고 알려주는 코드
app.get('/timeOK', function (req, res) {
    //현재시간
    var date = moment().format('HH mm ss');


    var sql = 'SELECT t_vnum as u_num, u_name, t_time FROM time JOIN user '
        + 'ON time.t_vnum = user.u_num '
        + 'WHERE t_vnum = ? order by t_time desc limit 1';


    try {
        connection.query(sql, [vssaid[0]], function (err, result) {
            if (err) console.log(err);
            else {
                if (result.length > 0) {
                    console.log(result);
                    var u_name = result[0].u_name;
                    var t_time = result[0].t_time + ' 00';
                    console.log('u_name 결과 : ' + u_name + '\nt_time 결과: ' + t_time);

                    var time2 = result[0].t_time + ' 50';

                    if ((date == t_time) || (date == time2)) {
                        result.splice(0, 0, { div: '귀가하지 않았습니다.' });
                        res.json({
                            result
                        });
                    } else {
                        result.splice(0, 0, { div: '아직 시간 전' });
                        res.json({
                            result
                        });
                    }
                }
            }
        });
    } catch (error) {
        console.log('시간 SELECT 중 오류 : ' + error);
        res.json({
            msg: '시간 SELECT 실패'
        });
    }
});


// //백그라운드
// //home insert(외출)
app.get('/insertMapping', function (req, res) {
    var date = moment().format('YYYY-MM-DD HH:mm:ss');
    var dateset = date + "%"
    console.log('insertMapping, date: ' + date);


    var sql = "select h_num, device, replace(h_odate,'T',' ') as h_odate, h_oroute from home "
        + "where device = ? and h_odate like ? and h_idate is null and h_iroute is null and h_div is null "
        + "order by h_seq desc limit 1";

    var params = [device, dateset];

    try {
        connection.query(sql, params, function (err, result) {
            if (err)
                console.log(err);
            else {
                if (result.length > 0) {
                    console.log('외출 : ' + JSON.stringify(result));
                    var o_num = result[0].h_num;

                    var sql1 = "select u_num, u_name, replace(h_odate,'T',' ') as h_odate, h_oroute from home join user "
                        + "on home.h_num = user.u_num "
                        + "where h_num = ? order by h_odate desc limit 1";
                    var params1 = [o_num];

                    connection.query(sql1, params1, function (err, result) {
                        if (err)
                            console.log(err);
                        else {
                            if (result.length > 0) {
                                console.log('결과 : ' + JSON.stringify(result));
                                result.splice(0, 0, { div: '외출' });
                                res.json({
                                    result
                                });
                            }
                        }
                    });
                }
                else {
                    res.json({
                        result: [{ "div": "외출하지 않음" }, { "u_num": "디바이스번호" }]
                        //msg: '외출하지 않음'
                    });
                }
            }
        });
    } catch (error) {
        console.log('외출한 사람의 정보 SELECT 중 오류 : ' + error);
        res.json({
            msg: '외출한 사람의 정보 SELECT 실패'
        });
    }
});


//백그라운드
//home update(귀가)
app.get('/updateMapping', function (req, res) {
    var date = moment().format('YYYY-MM-DD HH:mm:ss');
    var dateset = date + "%"

    var sql = "select h_num, device, replace(h_idate, 'T', ' ') as h_idate, h_iroute from home "
        + "where device = ? and h_idate like ? and h_iroute is not null and h_div is null order by h_seq desc limit 1";

    var params = [device, dateset];

    try {
        connection.query(sql, params, function (err, result) {
            if (err)
                console.log(err);
            else {
                if (result.length > 0) {
                    console.log('귀가 : ' + JSON.stringify(result));
                    var i_num = result[0].h_num;

                    var sql1 = "select u_num, u_name, replace(h_idate, 'T', ' ') as h_idate, h_iroute from home join user "
                        + "on home.h_num = user.u_num "
                        + "where h_num = ? order by h_seq desc limit 1";

                    var params1 = [i_num];

                    connection.query(sql1, params1, function (err, result) {
                        if (err)
                            console.log(err);
                        else {
                            if (result.length > 0) {
                                console.log('결과 : ' + JSON.stringify(result));
                                result.splice(0, 0, { div: '귀가' });
                                res.send({
                                    result
                                });
                            }
                        }
                    });
                }
                else {
                    res.json({
                        result: [{ "div": "귀가하지 않음" }, { "u_num": "디바이스번호" }]
                    });
                }
            }
        });
    } catch (error) {
        console.log('귀가한 사람의 정보 SELECT 중 오류 : ' + error);
        res.json({
            msg: '귀가한 사람의 정보 SELECT 실패'
        });
    }

});


//대신확인 버튼 눌렀을 때
app.post('/checkInstead', function (req, res) {
    h_num = req.body.h_num;
    //현재시간
    var date = moment().format('YYYY-MM-DD HH:mm:ss');
    //구분
    var div = 'Y';


    var sql = 'UPDATE home SET h_idate = ?, h_div = ? ' +
        'WHERE h_num = ? and h_idate is null and h_iroute is null and h_div is null';
    var params = [date, div, h_num];

    try {
        connection.query(sql, params, function (err, result) {
            if (err)
                console.log(err);
            else {
                res.send({
                    date, div
                });
            }
        });
    } catch (error) {
        console.log('대신확인 UPDATE 중 오류 : ' + error);
        res.json({
            msg: '대신확인 UPDATE 실패'
        });
    }
});
