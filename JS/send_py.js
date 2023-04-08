var express = require('express');
var app = express();

var bodyParser = require('body-parser');

var multer = require('multer');


app.use(bodyParser.json({extended: true}));
app.use(bodyParser.urlencoded({extended: true}));

app.listen(1055,()=>{
    console.log('Sever is running 1055');
});


var upload = multer({
    storage: multer.diskStorage({
        destination: function (req, file, cb) {
            cb(null, './');
        },
        filename: function (req, file, cb) {
            cb(null, file.originalname);
        }
    })
    });


    app.post('/upload', upload.single('file'), function(req, res) {
        console.log("file upload success!!");
        res.sendStatus(200);
        
    });
    

