# Robot-Game

The  game  is  played  on  a  9×9 grid,  which is  initially  empty  except  for  a  single square  at the  centre,  
called  the  “citadel”,  that  the player  must defend.

Every  1500 milliseconds,  a  new  robot  appears  randomly  in one  of the  four  corners  of  the  grid  
(unless all  four corners  are  already  occupied  by  other  robots).

Each  robot  attempts  to move  to a  new  grid  square  every  𝑑 milliseconds. A  delay  value  𝑑,  
randomly  chosen from  between 500–2000  milliseconds.

If  any  robot  moves into  the citadel  square,  the  game  ends,  and  the player  receives their  final  score.

The  player  can  click  on an  unoccupied square  to  build  a  “fortress  wall”.  Only one  fortress wall  can  be  built  
per  2000  milliseconds, but  a player  can  click  on  several  squares  in quick  succession to  queue  up  wall-building  commands.  
Corresponding  walls will  then be  built  at  a  rate  of  one per  2000 milliseconds.

Walls  can  be  destroyed  by  robots.


# How to Run

gradlew run (In windows)

./gradlew run (In Linux)
