# ROM Builder
*Testé sur Ubuntu 14.04 64-bit*

Afin de créer une application "KioskMode", il faut s'assurer d'un vérouillage du système de la tablette, c'est pour cette raison qu'une version modifiée d'Android est nécessaire.

Utilitaire d'exctraction et de création de Rom pour Android 5+.

Les stock images d'Android ne sont pas unpackable, il faut utiliser releases tierces basées sur l'AOSP (genre CyanogenMod).

Ça par exemple ça marche: [Release CyanogenMod](https://download.cyanogenmod.org/?type=snapshot&device=flounder).

##Qu'est-ce que ça fait
L'utilitaire décompresse la Rom et applique les modification souhaitées sur le répertoire system: installation/désinstallation d'applications natives,
 privilèges root (silencieux ou non), modofication de l'animation de boot, désactiviation de process système...

## Comment on l'utilise
Après avoir téléchargé une Rom valide (basée sur l'AOSP et Android 5+) [CyanogenMod](https://download.cyanogenmod.org/?type=snapshot&device=flounder) il ne reste plus qu'a la copier dans le répertoire original_update.

Lancer l'utilitaire

    ./menu

Puis mettre en place l'environnement de travail pour effectuer les modifiactions sur la rom, en tapant 1.
Le script va décompresser la rom, et monter la partition system dans WORKING_DIR/

Une fois les modifications nécessaires terminées, il ne reste plus qu'à build la rom (99).
Le fichier créé sera exporté dans le répertoire OUTPUT/

Les fichiers .apk à installer doivent être placés dans le répertoire install_apk/
Le fichier bootamination.zip doit être placé dans install_bootanimation/ (Un exemple est présent dans install_bootanimation/exemple).

## Quand on appuie sur 1
Le répertoire WORKING_DIR est entièrement supprimé, donc faut penser sauvegarder ce qui est dedans.

L'image selectionnnée est décompressée, et la partition system est montée dans WORKING_DIR/system_mount/

## SuperSU Embedded
Permet de rooter la rom de façon silencieuse ou non.
Les fichiers de SU-Embed sont copiés sur la partition system.
Pour le root silencieux, le fichier /META-INF/com/google/android/updater-script (script executé par Android une fois le flash de la rom terminé) est modifié afin de supprimer le fichier superSU.apk (-> plus de prompt lors d'un accés root par une application).

Dans notre cas ça permet d'installer/mettre à jour les applications depuis l'AppStore de manière silencieuse.

## Lazy mode



##Stock tablet
Avant de pouvoir flasher une tablette neuve avec une rom custom, un custom recovery doit être installer
[TWRP Recovery](https://twrp.me/devices/htcnexus9.html)

*Testé avec TWRP 2.8.7.0*

Pour l'installer il faut démarrer la tablette en fastboot mode: 
	 - Boutons POWER + VOLUME BAS jusqu'au redémarrage
	 - Selectionner le mode fastboot avec les commandes de volume et valider avec le bouton power

Flasher le recovery avec celui fournis par TWRP (fastboot est fournis avec le sdk android)

    fastboot flash recovery twrp.img

Selectionner le mode HBOOT, puis le mode RECOVERY afin d'accéder à TWRP.

#TWRP
Pour booter sur la partition de recovery plusieurs solutions:
Si le mode debug est activé:
    adb reboot recovery
Sinon boot avec POWER + VOLUME BAS enfoncé, puis RECOVERY

Pour installer une rom, il faut que celle-ci soit copier sur le stockage interne de la tablette.
Avant d'installer un bon Wipe ne fait pas de mal: Wipe/Advanced Wipe (On peut tout vider, sauf le stockage interne... bah oui)

Puis dans Install/ choisir le .zip de la rom à flasher