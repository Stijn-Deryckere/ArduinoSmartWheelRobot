#include <SoftwareSerial.h>

const int trigPin = 8;
const int echoPin = A1;

void setup() {
  // put your setup code here, to run once:
     Serial.begin(9600);           //  setup serial
}

void loop() {
  // put your main code here, to run repeatedly:
  int right = digitalRead(7);   
  int mid = digitalRead(4);
  int left = digitalRead(3);
  long duration, cm;
  
 // debugValues(left, mid, right);
  
  //rijden
  analogWrite(11, 50);
  analogWrite(6, 50);
  
  //middenste LED staat niet op de lijn
  if(mid == 0)
  { 
    
    if(left == 0 && right == 0)
     {
       //Geen zwarte lijn beschikbaar dus niet corrigeren
       //We versturen een hoge pulse
        pinMode(trigPin, OUTPUT);
        digitalWrite(trigPin, LOW);
        delayMicroseconds(2);
        digitalWrite(trigPin, HIGH);
        delayMicroseconds(10);
        digitalWrite(trigPin, LOW);
        
        // checken lengte van de hoge pulse
        pinMode(echoPin, INPUT);
        duration = pulseIn(echoPin, HIGH);
       
        cm = microsecondsToCentimeters(duration);
        
        if(cm < 20)
        {
            //kan niet verder dan +- 35 cm als ie 0 geeft is er geen muur
             if(cm != 0)
            {
             //draaien
             //DRAAIEN 
             analogWrite(11, 255);
             delay(1000);
           }  
        }
        delay(5);

   }
   else{
      //Corrigeer
     if(right == 0) 
     {analogWrite(11, 255);}
     if(left == 0)
     {analogWrite(6,255);}
   }
     
  }
}

void debugValues(int sLeft, int sMid, int sRight){
  Serial.print("(");
  Serial.print(sLeft);
  Serial.print(", ");
  Serial.print(sMid);
  Serial.print(", ");
  Serial.print(sRight);
  Serial.println(")");
}

long microsecondsToCentimeters(long microseconds)
{
  // The speed of sound is 340 m/s or 29 microseconds per centimeter.
  // The ping travels out and back, so to find the distance of the
  // object we take half of the distance travelled.
  // OP google vind je alles!
  return microseconds / 29 / 2;
}
