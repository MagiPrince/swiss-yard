# swiss-yard

## Description

Le projet consiste à recréer le jeu Scotland Yard. Le principe du jeu est qu'il y a des policiers recherchant à arrêter un voleur dans un périmètre défini. Tous se déplacent en utilisant des transports publics. Notre jeu se déroule en Suisse et simule les déplacements utilisant des transports en commun. Ces transports correspondent à la circulation des transports réels en direct.

## How to

### Cloner le repos :
```
git clone ssh://git@ssh.hesge.ch:10572/horn-of-the-Alps/swiss-yard.git
cd swiss-yard
```

### Exécuter la commande suivante :
```
mvn exec:java -Dexec.mainClass="ch.hepia.swissyard.Main"
```

## Fonctionnalités

### Fonctionnalités à implementer

- [X] Affichage de tous les arrêts sur la map
- [X] Affichage d'un joueur à un arrêt aléatoire
- [ ] Affichage des prochains départs d'un arrêt
- [ ] Un joueur peut choisir le transport qu'il va prendre parmi les prochains départs
- [X] Affichage d'un joueur dans un transport pris
- [ ] Affichage de la liste de passage minutée d'un transport
- [ ] Un joueur peut décider de monter/sortir à un arrêt
- [X] Un policier peut capturer un voleur en étant au même endroit

### Optionnel

- [ ] Le voleur n'est affiché qu'aux arrêts
- [ ] Le voleur possède un transport qui lui est réservé
- [ ] ...


### Fonctionnalités non-terminées :sob:

- Affichage des prochains départs d'un arrêt, une partie du code est présente mais incomplète donc la
fonctionalité n'a pas été implémentée.
- Un joueur peut choisir le transport qu'il va prendre parmi les prochains départs, la fonctionnalité
n'est pas implémentée.
- Affichage de la liste de passage minutée d'un transport, la fonction n'a pas été implémentée.
- Un joueur peut décider de monter/sortir à un arrêt, la fonction est implémentée mais bugée.

## Difficultées rencontrées

Incompréhension du Broker (et client) donc une difficultée à débuter correctement le projet.
Première prise en main de javaFX avec très peu voire aucune connaissances de cette librairie également suivie
de bugs avec des "segmentation fault" sur toutes les plateformes. Nous avons tenté d'introduire une
carte GoogleMap dans la vue en vain.
L'utilisation de OpenData pour le réseau TPG fut fort compliquée, récupérer les arrêt du réseau TPG
demande beaucoup d'efforts.
A l'heure actuelle, nos différents modules sont trop dépendant les uns des autres. Nous aurions dû
nous pencher vers des modules plus indépendants afin de facilités les implémentations ou changements futurs.

## Bug connues
Lorsque les services de transport publique ne déservent plus un arret, un timestamp étant à "null" 
cause un problème de conversion en nombre entier dans l'api parser

