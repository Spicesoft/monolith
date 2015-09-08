Ionic sample application
========================
//TODO: Doc
Application de démo Cordova basée sur le framework Ionic.


Installation:
=============

     git clone

     bower install

Use:
====

Splash screen :
Placer les fichier icon.psd et splash.psd dans le repertoire projet/resource puis:
     ionic resource


Resources:
=========
 - splashscreen + icon how to create & generate. set config.xml to maintain ratio

Release:
========

Désintaller les plugins de debug:

    cordova plugin rm org.apache.cordova.console

http://ionicframework.com/docs/guide/publishing.html

cordova build --release android


Plugins:
========
NFC
Splashcreen
statusbar
whitelist


Sources:
========
Angular-Translate ?
https://github.com/angular-translate/angular-translate

UI Calendar
http://angular-ui.github.io/ui-calendar/

Angular Chart
http://jtblin.github.io/angular-chart.js/

Datepicker for Ionic Framework
http://rajeshwarpatlolla.github.io/DatePickerForIonicFramework/

Timepicker for Ionic Framework
http://rajeshwarpatlolla.github.io/TimePickerForIonicFramework/