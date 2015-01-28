# PebbAirDrum
[PennApps 2015] A wearable drum that you can bring anywhere with your band!

##Brief
 - With powerful wearable sensors like Pebble watch, we want to bring new experiences to players.
 - Users can play in 3D.
 - We admire musicians and hope to make their performance easier.
 - With our app users are able to simulate drum set without having an actual one. 
 - Anyone who loves music, wants to perform and practice drums, or even just need to express their feelings with powerful beats can find the app very helpful and feasible. 
 - The use of pebble makes it possible that users don't have to bring any additional equipment other than phones/watches that people live with everyday to play drum anywhere whenever they want. 

  
#Pebble Air Drum App Description

##I. Description
Pebble Air Drum is an Android app. It uses Pebble Watch as an input of user gestures, and let the user to play simulated drums displayed on smartphone.

When the user launches an app, he first chooses which hand wearing pebble watch. Then the user will go to the screen with simulated drums. 
In playing the drum, to obtain accuracy, the user should keep the pebble watch face upward. If the user wants to beat a specific drum, he can turn the watchface slightly toward the direction of the intended drum when beating.
There’s background beats for user to keep track of, which automatically starts when enterring the game screen.

##II. Implementation
Pebble Air Drum contains two parts, the Pebble part and the Android part.
The Pebble part program is written in C, it reads and processes the data from accelerometer sensor, and send processed results to Android phone. The sampling rate of the sensor is 10 Hz, and every 5 samples forms a batch, and calls a data handler. The handler judges whether there is a beat by calculating the combined acceleration. When the watch is in static, there is a stable value of accelerometer sensor, which is the gravity. The beat gesture will cause a weight loss, and the combined acceleration will decrease sharply. After Then the handler judges a beat, it calculates the offsets of the sampled data and adjust the data by subtracting the offsets. The adjusted data then integrated and used to judge whether the user is beating towards front, back, left or right. In the end, the handler sends the result to Android phone.

The Android application targets API level 19 (Android 4.4) and the UI interface was designed for running on Nexus 7 or other device with same resolution. Pebble watch and Android application communicates in AppMessage. 

##III. Test Results
Latency:
There is a perceptible latency in the Android app reaction. Since the data is processed in batch, not strictly real time, there will be a delay in Pebble watch process the data, and send the data to Android phone. However, the latency is acceptable.
Accuracy:
The user could obtain a good accuracy if performing “as expected”.

##Some Screenshots

* First three for Android App, the last one is APP_LOG output recording gesture recoginition.

|                |                |
|:--------------:| --------------:|
|![alt text][p1] |![alt text][p2] |
| ![alt text][p3]|![alt text][p4] |

[p1]: https://github.com/snugglelamb/PebbAirDrum/blob/master/screenshots/p0.png "p1"
[p2]: https://github.com/snugglelamb/PebbAirDrum/blob/master/screenshots/p1.png "p2"
[p3]: https://github.com/snugglelamb/PebbAirDrum/blob/master/screenshots/p2.png "p3"
[p4]: https://github.com/snugglelamb/PebbAirDrum/blob/master/screenshots/p3.png "p4"
