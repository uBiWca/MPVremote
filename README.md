# MPVremote
This android application is intended to be used as client application for  LUA script mpv-remote, which
allows to control MPV media player bundled with KOID in RetroorangePi.

Это приложение под Android, предназначенное для управления плеером MPV, который используется Kodi,  поставляемой вместе с ОС RetroorangePi.

# Basics
This app is uses UDP for sending commands to server script, runned by MPV during playback. Script is based on
https://github.com/iamevn/mpv-network-commands
with some significant changes.
Unlike basic script, this one is allows android app to show progress and overall length of media file and allows to change playback position using SeekBar. This functionality is important for RetroorangePi users since MPV in this image does not supporing GUI.


Приложение использует UDP для отправки комманд серверному скрипту, выполняемому MPV во время воспроизведения. Скрипт основан на серверному скрипте, который можно найти в репозитории выше и содержит значительные изменения. В частности, он позволяет андроид-приложению отображать положение и общую продолжительность  медиа-файла, а так же менять место вопроизведения при помощи обычного ползунка. Это важно, с учетом того, что MPV в RetroorangePi не имеет GUI.

# Installation
This app has a serious limitation - you need a fixed IP in your Wi-Fi network for your phone. This can be done by binding your phone MAC-adress to IP on DHCP server settings of your router.

У приложения есть серьезное ограничение - необходим фиксированные IP адрес для телефона в вашей Wi-Fi сети. Этого можно добиться, к примеру, настройкой DHCP сервера вашего роутера, указав выдавать конкретный IP устройству с MAC -адресом вашего телефона.
- Install LUA and LUA socket. Go to desktop and type in console :
```
sudo apt-get install lua5.1 luasocket
```
- Copy server.lua to /home/pi/.config/mpv/lua 
- Open server.lua with nano and in this line
```
conn_up:sendto(title.."$"..length.."$"..pos, "192.168.100.168", 5050);
```
replace IP with your phone IP and save changes.
- Install 
https://github.com/uBiWca/MPVremote/blob/master/app/release/app-release.apk
- After installation, go to Settings... and type your OranegePi IP and port in relevant fields. Default port is 7550.
- Reboot OrangePi.
