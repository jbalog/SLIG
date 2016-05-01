#include <SPI.h>
#include <Wire.h>
#include <SoftwareSerial.h>
#include <SparkFunLSM9DS1.h>

LSM9DS1 imu;

// constants
const int sensorPinPINKEY = A2;
const int sensorPinTHUMB = A1;
const int sensorPinMIDDLE = A4;
const int sensorPinINDEX = A3;
const int sensorPinRING = A0;

// variables:
int sensorValueTHUMB = 0;
int sensorValueINDEX = 0;
int sensorValueMIDDLE = 0;
int sensorValueRING = 0;
int sensorValuePINKEY = 0;

int sensorMinTHUMB = 1023;
int sensorMinINDEX = 1023;
int sensorMinMIDDLE = 1023;
int sensorMinRING = 1023;
int sensorMinPINKEY = 1023;

int sensorMaxTHUMB = 0;           
int sensorMaxINDEX = 0;          
int sensorMaxMIDDLE = 0;          
int sensorMaxRING = 0;
int sensorMaxPINKEY = 0;

SoftwareSerial BTSerial(2, 3); //RX|TX

void setup(){

  imu.settings.device.commInterface = IMU_MODE_SPI; // Set mode to SPI
  imu.settings.device.mAddress = 10; // Mag CS pin connected to D9
  imu.settings.device.agAddress = 9; // AG CS pin connected to D10
  
  //Configure i/o pins
  pinMode(4,INPUT_PULLUP);
  pinMode(5,INPUT_PULLUP);
  pinMode(6,INPUT_PULLUP);
  pinMode(7,INPUT_PULLUP);
  
  Serial.begin(9600);
  BTSerial.begin(9600); // default baud rate

  //accelerometer setup
  if (!imu.begin())
  {
    BTSerial.println("Failed to communicate with LSM9DS1.");
    while (1)
      ;
  }
  
  //BT module setup
  while(!BTSerial); //if it is an Arduino Micro
  //Serial.println("AT commands: ");
 // BTSerial.println("AT+NAME<SLIG GLOVE>"); // just a check
  delay(2000);
 // BTSerial.println("AT+ROLE0"); // set up a slave



  BTSerial.println("Start");
  delay(3000);
  //

  //Record current runtime and begin 5 second calibration period

  unsigned long currentMillis = millis();
  BTSerial.println("Begin Calibration");


  //5 second calibration loop
  while (millis() - currentMillis < 5000) {

    sensorValueTHUMB = analogRead(sensorPinTHUMB);
    sensorValueINDEX = analogRead(sensorPinINDEX);
    sensorValueMIDDLE = analogRead(sensorPinMIDDLE);
    sensorValueRING = analogRead(sensorPinRING);
    sensorValuePINKEY = analogRead (sensorPinPINKEY);

    // Record minimum and maximum sensor values for each finger

    if (sensorValueTHUMB > sensorMaxTHUMB) {
      sensorMaxTHUMB = sensorValueTHUMB;
    }

    if (sensorValueINDEX > sensorMaxINDEX) {
      sensorMaxINDEX = sensorValueINDEX;
    }

    if (sensorValueMIDDLE > sensorMaxMIDDLE) {
      sensorMaxMIDDLE = sensorValueMIDDLE;
    }

    if (sensorValueRING > sensorMaxRING) {
      sensorMaxRING = sensorValueRING;
    }

    if (sensorValuePINKEY > sensorMaxPINKEY) {
      sensorMaxPINKEY = sensorValuePINKEY;
    }

    if (sensorValueTHUMB < sensorMinTHUMB) {
      sensorMinTHUMB = sensorValueTHUMB;
    }

    if (sensorValueINDEX < sensorMinINDEX) {
      sensorMinINDEX = sensorValueINDEX;
    }

    if (sensorValueMIDDLE < sensorMinMIDDLE) {
      sensorMinMIDDLE = sensorValueMIDDLE;
    }

    if (sensorValueRING < sensorMinRING) {
      sensorMinRING = sensorValueRING;
    }

    if (sensorValuePINKEY < sensorMinPINKEY) {
      sensorMinPINKEY = sensorValuePINKEY;
    }

  } // End of calibration phase

 // digitalWrite(13, HIGH);

  BTSerial.println("End Calibration");

  
}

void loop(){
  //read from the HM-10 and print in the Serial
  if(BTSerial.available())
    Serial.write(BTSerial.read());
    
  //read from the Serial and print to the HM-10
  if(Serial.available())
    BTSerial.write(Serial.read());

//Begin infinite loop by reading each sensor value
  sensorValueTHUMB = analogRead(sensorPinTHUMB);
  sensorValueINDEX = analogRead(sensorPinINDEX);
  sensorValueMIDDLE = analogRead(sensorPinMIDDLE);
  sensorValueRING = analogRead(sensorPinRING);
  sensorValuePINKEY = analogRead(sensorPinPINKEY);
  imu.readAccel();
  //

  // Map the range of values from each sensor into 255 discrete levels
  sensorValueTHUMB = map(sensorValueTHUMB, sensorMinTHUMB, sensorMaxTHUMB, 1, 255);
  sensorValueINDEX = map(sensorValueINDEX, sensorMinINDEX, sensorMaxINDEX, 1, 255);
  sensorValueMIDDLE = map(sensorValueMIDDLE, sensorMinMIDDLE, sensorMaxMIDDLE, 1, 255);
  sensorValueRING = map(sensorValueRING, sensorMinRING, sensorMaxRING, 1, 255);
  sensorValuePINKEY = map(sensorValuePINKEY, sensorMinPINKEY, sensorMaxPINKEY, 1, 255);
  //

  // Set a floor and ceiling of 1 and 255 to avoid out-of-bounds values
  sensorValueTHUMB = constrain(sensorValueTHUMB, 1, 255);
  sensorValueINDEX = constrain(sensorValueINDEX, 1, 255);
  sensorValueMIDDLE = constrain(sensorValueMIDDLE, 1, 255);
  sensorValueRING = constrain(sensorValueRING, 1, 255);
  sensorValuePINKEY = constrain(sensorValuePINKEY, 1, 255);

  
  
  // Diagnostic code that displays current values for all sensors when needed
  
    /*
    Serial.println ("-------------------");
    Serial.print ("Thumb Value = ");
    Serial.println (sensorValueTHUMB);

    Serial.print ("Index Finger Value = ");
    Serial.println (sensorValueINDEX);

    Serial.print ("Middle Finger Value = ");
    Serial.println (sensorValueMIDDLE);

    Serial.print ("PINKEY Value = ");
    Serial.println (sensorValuePINKEY);

    Serial.println ("   ");
    Serial.println (" ");  
    
    
    */
    

  // Begin conditionals that check for specific gestures


  // Check for 'A'
  if (digitalRead(7)== LOW && sensorValueTHUMB > 150 && sensorValueINDEX < 200 && sensorValueMIDDLE < 200 && sensorValuePINKEY<200) {
      Serial.println ("A"); 
      Serial.println  ("        ");
      BTSerial.println ("A"); 
      BTSerial.println  ("        ");
    }
  
  else { }


  // Check for 'B'
  if (sensorValueTHUMB < 180 && sensorValueINDEX > 200 && sensorValueMIDDLE > 200 && sensorValuePINKEY>200) {
    
    Serial.println ("B"); 
    Serial.println  ("        ");
    BTSerial.println ("B"); 
    BTSerial.println  ("        ");
    }
  
  else { }


  // Check for 'C'
  if (digitalRead(7) ==  HIGH && digitalRead(6)==HIGH && digitalRead(5) == HIGH && digitalRead(4) ==HIGH && sensorValueTHUMB > 200 && sensorValueINDEX < 200 && sensorValueRING < 200 && sensorValuePINKEY<200) {
   
    Serial.println ("C"); 
    Serial.println  ("        ");
    BTSerial.println ("C"); 
    BTSerial.println  ("        ");
    }
 
  else { }

  // Check for 'D'
  if (imu.ax>0 && sensorValueTHUMB < 200 && sensorValueINDEX > 200 && sensorValueMIDDLE < 200 && sensorValuePINKEY<120 && digitalRead(6)==HIGH) {
    
    Serial.println ("D"); 
    Serial.println  ("        ");
    BTSerial.println ("D"); 
    BTSerial.println  ("        ");
      }
  
  else { }

  // Check for 'E'
  if (imu.ax > 0 && sensorValueTHUMB < 180 && sensorValueTHUMB>50 && sensorValueINDEX < 200 && sensorValueMIDDLE < 200 && digitalRead(7)== HIGH && digitalRead(6) == HIGH && digitalRead(4)==HIGH && digitalRead(5)==HIGH && sensorValueTHUMB != 1 && sensorValuePINKEY <200){
    Serial.println  ("        ");
    BTSerial.println ("E"); 
    BTSerial.println  ("        ");
  }
  else { }

  // Check for 'F'
  if (sensorValueTHUMB < 180 && sensorValueINDEX < 120 && sensorValueMIDDLE > 200 && sensorValueRING >200) {
    Serial.println ("F"); 
    Serial.println  ("        ");
    BTSerial.println ("F"); 
    BTSerial.println  ("        ");
      }
  else { }

  // Check for 'G'
  if (sensorValueTHUMB > 180 && sensorValueINDEX > 200 && sensorValueMIDDLE < 200 && sensorValueRING < 200 && digitalRead(6)==LOW) {
    Serial.println ("G"); 
    Serial.println  ("        ");
    BTSerial.println ("G"); 
    BTSerial.println  ("        ");
      }
  else { }

  // Check for 'H'
  if (sensorValueTHUMB > 180 && sensorValueINDEX > 200 && sensorValueMIDDLE > 200 && sensorValueRING<200 &&digitalRead(6)==HIGH && digitalRead(5)==HIGH) {
    Serial.println ("H"); 
    Serial.println  ("        ");
    BTSerial.println ("H"); 
    BTSerial.println  ("        ");
      }

  // Check for 'I'
  if (sensorValueTHUMB < 120 && sensorValueINDEX < 120 && sensorValueMIDDLE < 120 && sensorValuePINKEY>200 && digitalRead(7)==HIGH) {
    Serial.println ("I"); 
    Serial.println  ("        ");
    BTSerial.println ("I"); 
    BTSerial.println  ("        ");
      }

  // Check for 'J'
  if (sensorValueTHUMB < 120 && sensorValueINDEX < 180 && sensorValueMIDDLE < 180 && sensorValuePINKEY>200 && digitalRead(7)==LOW) {
    Serial.println ("J"); 
    Serial.println  ("        ");
    BTSerial.println ("J"); 
    BTSerial.println  ("        "); 
  }

   // Check for 'K'
  if (imu.ax>0 && digitalRead(6)==LOW && sensorValueINDEX > 200 && sensorValueMIDDLE >180 && sensorValueRING < 200 && sensorValuePINKEY<200) {
    Serial.println ("K"); 
    Serial.println  ("        ");
    BTSerial.println ("K"); 
    BTSerial.println  ("        ");
  }
  else { }

  // Check for 'L'
  if (imu.ax>0 && sensorValueTHUMB > 180 && sensorValueINDEX > 200 && sensorValueMIDDLE < 200  && sensorValuePINKEY <200 && digitalRead(6)==HIGH) {
    Serial.println ("L"); 
    Serial.println  ("        ");
    BTSerial.println ("L"); 
    BTSerial.println  ("        ");
  }
  else { }

  // Check for 'M'
  if (sensorValueTHUMB < 180 && sensorValueINDEX < 200 && sensorValueMIDDLE < 200 && digitalRead(4)==LOW) {
    Serial.println ("M"); 
    Serial.println  ("        ");
    BTSerial.println ("M"); 
    BTSerial.println  ("        ");
  }
  else { }

  // Check for 'N'
  if (sensorValueTHUMB < 180 && sensorValueINDEX < 150 && sensorValueMIDDLE < 150 && digitalRead(5)==LOW) {
    Serial.println ("N"); 
    Serial.println  ("        ");
    BTSerial.println ("N"); 
    BTSerial.println  ("        ");
  }
  else { }

  // Check for 'O'
  if (sensorValueTHUMB < 180 && sensorValueINDEX < 180 && sensorValueMIDDLE < 180 && sensorValueRING<180 && sensorValuePINKEY<200 && digitalRead(7)==LOW) {
    Serial.println ("O"); 
    Serial.println  ("        ");
    BTSerial.println ("O"); 
    BTSerial.println  ("        ");  
  }
  else { }

// Check for 'P'
  if (imu.ax<0 && digitalRead(6)==LOW && sensorValueINDEX > 200 && sensorValueMIDDLE >180 && sensorValueRING < 200 && sensorValuePINKEY<200) {
    Serial.println ("P"); 
    Serial.println  ("        ");
    BTSerial.println ("P"); 
    BTSerial.println  ("        ");  
  }
  else { }
  
  // Check for 'Q'
  if (imu.ax<0 && sensorValueTHUMB > 180 && sensorValueINDEX > 180 && sensorValueMIDDLE < 180 && sensorValueRING<180 && digitalRead(6)==HIGH) {
    Serial.println ("Q"); 
    Serial.println  ("        ");
    BTSerial.println ("Q"); 
    BTSerial.println  ("        ");
  }
  else { }

  // Check for 'R'
  if (sensorValueTHUMB < 180 && sensorValueINDEX > 200 && sensorValueMIDDLE > 200 && sensorValueRING<200 && digitalRead(4)==LOW) {
    Serial.println ("R"); 
    Serial.println  ("        ");
    BTSerial.println ("R"); 
    BTSerial.println  ("        ");
  }
  else { }

  
  // Check for 'S'
  if (imu.ax <0 && sensorValueTHUMB<200 && sensorValueINDEX < 200 && sensorValueMIDDLE < 200  && digitalRead(7==HIGH)) {
    Serial.println ("S"); 
    Serial.println  ("        ");
    BTSerial.println ("S"); 
    BTSerial.println  ("        ");
  }

// Check for 'T'
  if (sensorValueTHUMB < 180 && sensorValueINDEX < 200 && sensorValueMIDDLE < 200 && digitalRead(6)==LOW) {
    Serial.println ("T"); 
    Serial.println  ("        ");
    BTSerial.println ("T"); 
    BTSerial.println  ("        ");
  }
  else { }

  // Check for 'U'
  if (sensorValueTHUMB < 180 && sensorValueINDEX > 200 && sensorValueMIDDLE > 200 && sensorValueRING<200 && digitalRead(5)==LOW) {
    Serial.println ("U"); 
    Serial.println  ("        ");
    BTSerial.println ("U"); 
    BTSerial.println  ("        ");
  }
  else { }

  // Check for 'V'
  if (sensorValueTHUMB < 180 && sensorValueINDEX > 200 && sensorValueMIDDLE > 200 && sensorValueRING<200 && digitalRead(4)==HIGH && digitalRead(5)==HIGH) {
    Serial.println ("V"); 
    Serial.println  ("        ");
    BTSerial.println ("V"); 
    BTSerial.println  ("        ");
  }
  else { }

  // Check for 'W'
  if (sensorValueTHUMB < 180 && sensorValueINDEX > 200 && sensorValueMIDDLE > 200 && sensorValueRING>200 && sensorValuePINKEY<200) {
    Serial.println ("W"); 
    Serial.println  ("        ");
    BTSerial.println ("W"); 
    BTSerial.println  ("        ");
  }
  else { }

  // Check for 'X'
  if (sensorValueTHUMB <50 && sensorValueINDEX < 200 && sensorValueMIDDLE < 200 &&  sensorValueRING<200 && sensorValuePINKEY<200 ) {
    Serial.println ("X"); 
    Serial.println  ("        ");
    BTSerial.println ("X"); 
    BTSerial.println  ("        ");
  }
  else { }
  
  // Check for 'Y'
  if (sensorValueTHUMB > 180 && sensorValueINDEX < 150 && sensorValueMIDDLE < 150  && sensorValuePINKEY >200 && digitalRead(7)== HIGH) {
    Serial.println ("Y"); 
    Serial.println  ("        ");
    BTSerial.println ("Y"); 
    BTSerial.println  ("        ");
  }

  // Check for 'Z'
  if (imu.ay<0 && sensorValueTHUMB < 180 && sensorValueINDEX > 200 && sensorValueMIDDLE <200 && sensorValueRING<200 && digitalRead(6)==LOW) {
    Serial.println ("Z"); 
    Serial.println  ("        ");
    BTSerial.println ("Z"); 
    BTSerial.println  ("        ");
  }
  else { }
  
  
  /// End Infinite loop. Wait one sec. then restart
  delay (1000);    
}
