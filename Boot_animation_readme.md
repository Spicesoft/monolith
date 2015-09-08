#Boot Animation
L'animation de boot est définie par l'archive bootanimation.zip qui se trouve dans le répertoire /system/media/ de la rom.

	bootanimation.zip
	|_desc.txt
	|_part0
	|	|_img0001.png
	|	|_img0002.png
	|	|_...
	|_partX...

L'archive est composée de un ou plusieurs répertoires 'partX' ( 0 <= X <= 9). Chaque répertoire 'part' contient un ensemble d'image .png composant l'animation.
Les fichiers doivent être nommé de la même façon et de manière ordonnée.
*Eviter d'utiliser des images avec un fond transparent*

Elle contient aussi le fichier desc.txt

    550 550 30
    p 0 0 part0

Qui décrit le comportement de l'animation.
Les 2 premières valeurs définissent la résolution de l'animation, la 3e donne le nombre de FPS.
Les lignes suivantes décrivent le déroulement de l'animation.
	- 'p' définit une nouvelle ligne.
	- le premier nombre spécifie la répétition de la partie de l'animation concernée (0 -> infini).
	- le deuxième nombre est la durée de la pause à effectuer avant de répéter la partie concernée ou de passer à la ligne suivante.
	- partX est le répertoire d'images à lire.

Pour être lisible par le système, l'archive doit être au format zip sans compression:

    zip -r -0 bootanimation.zip desc.txt part0/ partX/

Et avec les permissions: rw-r—r—
