#AppStore

Cette application est déstinée à être installée nativement sur une version d'android modifiée grâce au ROM Builder.
Lors de son lancement l'application désactive une partie de l'interface du système afin de vérouiller l'utilisation de la tablette.


A Chaque lancement AppStore vérifie les versions des applications disponibles sur S3 à l'adresse suivante http://spicesoft.pro.s3.amazonaws.com/webapps/appName (Cette URL est défnie dans Model/ServerInfo.java). Si une mise à jour est disponible, celle-ci est installée de manière silencieuse.

## Applications du store

Les applications doivent être stockées sur un serveur accessible via http, selon l'arborescence suivante:

	/
	 |_ apps
	 |_ AppName1
	 |	|_ app1.apk
	 |	|_ version
	 |_ AppName2
	 |	|_ app2.apk
	 |	|_ version

Le fichier apps liste les répertoire d'applications disponibles:
Par exemple si on a 2 applications Monolith et Cowork:

	/
	 |_ apps
	 |_ Monolith
	 |	|_ monolith.apk
	 |	|_ version
	 |_ Cowork
	 |	|_ cowork.apk
	 |	|_ version

le fichier apps sera alors:
    
    
