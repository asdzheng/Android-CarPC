## Android Car-PC App ##
([Make a Micro Donation if you find this post usefull!](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=ZXXJNRQW9QYAN))

This app will only control a handy like a build in On Board Unit (OBU). It will use the detection of USB Power to control backlight. On ignition you do not have a
break in your power, it will ignore this. The handy should be always on with its battery and you can use the app webkey for car location and remote desktop
access. For a landscape mode of your handy you should test the available landscape override apps on the market. Some are really great.
The App will go to lowest backlight after the ignition is off and after again 60 seconds it will go to standby without backlight.
The sound volume will be set to default everytime you start the ingnition after more than 60 seconds of unpowered device.

![Settings Window](https://raw.github.com/sirnoname/Android-CarPC/master/img/screenshot.jpg)

### You will need for this project: ###
- a handy/smartphone with android 2.3 and up
- a lightsensor in the handy (if needed you can reprogram this)
- some solder knowlege to gt the GPS antenna to your window
- A loader cable to your handy

##Quick starter guide##

1. Download the servicedemo.apk out of the bin directory here in github, it is precompiled
2. Copy the app to your smartphone
3. Do not forget to enable the "installation of unknown resources" option in android
4. Execute the apk and install it
5. Open the apk, it will run in the background until you click it for the menu
6. Setup the init sound volume
7. Setup the light sensor

Thats all!
