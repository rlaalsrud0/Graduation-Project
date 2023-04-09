#import pymysql
# -*- coding: utf-8 -*-

import mysql.connector
import RPi.GPIO as GPIO                    # RPi.GPIO에 정의된 기능을 GPIO라는 명칭으로 사용
import time                                # time 모듈
import picamera
import datetime
import os
import blescan
import sys
import bluetooth._bluetooth as bluez
import requests

count = 0
path = "/home/pi/iBeacon-Scanner-/"
server = 'ADDRESS:1055/upload'

conn = mysql.connector.connect(
    host="hostname",
    database="databasename",
    user="admin",
    password="password",
    buffered=True
    )

camera = picamera.PiCamera()
camera.resolution = (1920, 1080) # (64, 64) ~ (1920, 1080) px
camera.framerate = 30 # 1 ~ 30 fps


GPIO.setmode(GPIO.BCM)        
GPIO.setwarnings(False)
GPIO.setup(4, GPIO.OUT)              

ECHOS = {"ECHO_1": 17, "ECHO_2": 27}
for e in ECHOS:
    GPIO.setup(ECHOS[e], GPIO.IN)

print("Press SW or input Ctrl+C to quit") 

#convert mp4
#def convert(filename, filename2) :
    

#센서1
try:
    while True:
        GPIO.output(4, False)
        time.sleep(0.5)

        GPIO.output(4, True)          # 10us 펄스를 내보낸다.
        time.sleep(0.00001)            # Python에서 이 펄스는 실제 100us 근처가 될 것이다
        GPIO.output(4, False)        

        while GPIO.input(17) == 0:     # 17번 핀이 OFF 되는 시점을 시작 시간으로 잡는다
            start = time.time()

        while GPIO.input(17) == 1:     # 17번 핀이 다시 ON 되는 시점을 반사파 수신시간으로 잡는다
            stop = time.time()

        time_interval = stop - start      # 초음파가 수신되는 시간으로 거리를 계산한다
        distance = time_interval * 17000
        distance = round(distance, 2)

        print("Distance1 => ", distance, "cm")
        #temp1.append(distance)
        if(distance < 30) :
            print("one")
            
            filename = str(datetime.date.today()) + "_out"
            fullname = filename + ".h264"
            file_pate = path + fullname

            file_list = os.listdir(path) # 현재 위치 디렉토리 내 파일명 리스트 추출
            #print(os.listdir(path))
            file_list_log = [file for file in file_list if file.startswith(filename)]
            # 파일 리스트내에서 lenna로 시작하는 파일리스트 추출

            count = 1 + len(file_list_log)
            #파일리스트 숫자에 1을 더해서 다음에 저장할 파일의 숫자로 이용
            

            if (os.path.isfile(file_pate)==True) :
                newfilename=filename+str(count)
            else:
                newfilename = filename
            
            
            camera.start_recording(newfilename+".h264")
            print("camera start")
            break

except KeyboardInterrupt:                  # Ctrl-C 입력 시
    GPIO.cleanup()                         # GPIO 관련설정 Clear
    print("bye~")
    
    
#센서2
try:
    while True:
        GPIO.output(4, False)
        time.sleep(0.5)

        GPIO.output(4, True)          # 10us 펄스를 내보낸다.
        time.sleep(0.00001)            # Python에서 이 펄스는 실제 100us 근처가 될 것이다
        GPIO.output(4, False)         

        while GPIO.input(27) == 0:     # 27번 핀이 OFF 되는 시점을 시작 시간으로 잡는다
            start = time.time()

        while GPIO.input(27) == 1:     # 27번 핀이 다시 ON 되는 시점을 반사파 수신시간으로 잡는다
            stop = time.time()

        time_interval = stop - start      # 초음파가 수신되는 시간으로 거리를 계산한다
        distance = time_interval * 17000
        distance = round(distance, 2)

        print("Distance2 => ", distance, "cm")
        if(distance < 30) :
            print("two")
            time.sleep(3)
            camera.stop_recording()
            print("camera stop")
            
            filename2 = newfilename+".mp4"
            #filename2.strip()
            
            #home 테이블에 insert
            cursor = conn.cursor()
            print("connect")

            sql2 = "select u_num from user"
            cursor.execute(sql2)

            result = cursor.fetchall()

            resultA=[]
            for data in result:
                resultA.append(data[0])
            print(resultA)
            
            
            #beacon start
            dev_id = 0
            resultB=[]

            try:
                sock = bluez.hci_open_dev(dev_id)
                print ("ble thread started")

            except:
                print ("error accessing bluetooth device...")
                sys.exit(1)

            blescan.hci_le_set_scan_parameters(sock)
            blescan.hci_enable_le_scan(sock)

            returnedList = blescan.parse_events(sock, 40)
            print ("----------")

            #beacon array save
            for beacon in returnedList:
                resultB.append(beacon)
            print(resultB)
                
            ssaid=list(set(resultB).intersection(resultA))   
            print(ssaid[0])
            
            os.system("MP4Box -add "+newfilename+ ".h264 " + filename2)
            
            files = open('./' + filename2, 'rb')
            upload = {'file': files}
            try:
                res = requests.post(server, files=upload)
                print(res.status_code)
                if res.status_code == 200:
                    print("Success!!")
            except Exception as ex:
                print("Except!")
    
            
            
            SSAID = ssaid[0]
            
            oroute = 'ADDRESS' + filename2
            
            now = time.strftime('%Y-%m-%d %H:%M:%S').strip()
            
            #add
            sql = "SELECT device FROM mapping WHERE m_vnum = '" + SSAID + "'"
            
            cursor.execute(sql)
            #conn.commit()
            
            print(str(cursor.rowcount))
            
            result = cursor.fetchall()
            #for i in result:
            #    print(i)
            print(str(result[0][0]))
            
            device = str(result[0][0])
            
            if cursor.rowcount > 0 :
                cursor.execute("""INSERT INTO home (h_num,device,h_odate,h_oroute)
                  VALUES ('%s', '%s', '%s', '%s')""" % (ssaid[0],device, time.strftime('%Y-%m-%d %H:%M:%S').strip(), oroute))
                conn.commit()
            
                print(cursor.rowcount, "record inserted.")
            
                conn.close()
            
            #convert(filename, filename2)

            
            break
        
            
            
except KeyboardInterrupt:                  # Ctrl-C 입력 시
    GPIO.cleanup()                         # GPIO 관련설정 Clear
    print("bye~")