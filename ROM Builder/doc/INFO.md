

Les applications (.apk) sont stockées dans /system/app et /system/priv-app (system app). Les fichiers apk sont installés lors du premier boot de la ROM.


#Boot animation

Le fichier bootanimation.zip contient un fichier 'desc.txt' décrivant le comportement de l'animation lors du boot, ainsi que les 
dossiers 'partN' (N entier <= 9) contenant les .png .
desc.txt example :
```
550 550 30
p 1 0 part0
p 0 0 part1
```

La première ligne du fichier 'desc.txt' définit la résolution de l'animation et son fps.
les lignes suivantes sont construites de la façon suivante:
'p' -> définit la ligne comme une partie de l'animation
le premier chiffre est le nombre de répétition de l'animation. (0 -> l'animation boucle à l'infini sur le dossier concerné)
le second chiffre est le temps de pause avant de poursuivre l'animation
'partN' est le dossier contenant les images (ou une partie) de l'animation.

bootanimation.zip est une archive zip sans compression -> zip -0 bootanimation.zip desc.txt part0/* part1/*


sources:
http://forum.xda-developers.com/showthread.php?t=2756198
http://forum.xda-developers.com/showthread.php?t=1852621
http://www.modaco.com/topic/338623-ubuntu-how-to-make-bootanimationzip-futurama-bender-bootanimationzip/


#Root access

The ROM is rooted using chainfire (SuperSU) Embedded solution. A supersu flashable zip file is placed at the root of the ROM's zip file.


sources:
http://su.chainfire.eu/


#Flashing .zip

When a .zip file is flashed on an Android device using Custom recovery (CWMR / TWPR)