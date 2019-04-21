import os
import time
import json
from firebase import firebase
import board
import busio
import serial 
import adafruit_lsm9ds0
import math


i2c = busio.I2C(board.SCL, board.SDA)
sensor = adafruit_lsm9ds0.LSM9DS0_I2C(i2c)

while True:
	#Runs the linux command that gets the raw NMEA data from the GPS
	#Sends the output to the file
    os.system('timeout 5 cat /dev/serial0 >> DDData.txt')
	#Connects to firebase
    myfirebase = firebase.FirebaseApplication('https://digitaldashboard-f7a33.firebaseio.com/')
    
    f= open("DDData.txt","r") #Opens the file to be parsed for data
    for line in f.read().split('\n'):
        if line.startswith('$GPGGA'):#If the NMEA data line starts with GPGGA then parse the longitude, latitude and altitude
            lat, t,lon, = line.strip().split(',')[2:5]
            alt = line.strip().split(',')[9]
            try :
                lat = float(lat)/100
                lon = float(lon)/100
                alt = float(alt)
                print('Latitude:', lat)
                print('Longitude:', lon)
                print('Altitude', alt)
				#Post the data into firebase 
                postdata = myfirebase.put('Raspberry Pi','Latitude',str(lat))
                postdata = myfirebase.put('Raspberry Pi','Longitude',str(lon))
                postdata = myfirebase.put('Raspberry Pi','Altitude',str(alt))
            except:
                pass
            
        elif line.startswith('$GPVTG'): #If the NMEA data line starts with GPVTG then parse the speed in KM/H
            knots = line.strip().split(',')[7]
            try:
                speed = float(knots)
                print('Km/h: ({0:0.0f})'.format(speed))
                DigitalDashboard.poll(speed)
                postdata = myfirebase.put('Raspberry Pi','Speed',str(speed))
            except:
                pass
        else:
			#Gets the acceleration from the accelerometer from the x-axis
            accel_x, accel_y, accel_z = sensor.acceleration;
            accel = math.sqrt((accel_x*accel_x)) #Gets a positive acceleration
            print('Acceleration (m/s^2): ({0:0.0f})'.format(accel))
            postdata = myfirebase.put('Raspberry Pi','Acceleration',str(accel))	#Post the data into firebase 