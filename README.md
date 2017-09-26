# The Forbidden Island
by Michelle Pine and Austin Kim

Escape the island before it's too late! This game, coded in Java, tests the player's survival skills in a race against time as the waters rise.

To start, place all files into a new Java Project's src folder. When running the program, note that there are a lot of tests, and that they test the lists of cells extensively, so they might take a little while to finish. 

This is a multiplayer version of the game which also implements the scuba capability, the engineer capability, and a score (aka the number of moves). A lower number of moves is a better score.  

There are two players, each indicated by a yellow square. One square moves via the key arrows. The other square can be moved with the "a" key for left, "s" key for down, "d" key for right, and "w" key for up. Without a scuba suit, a user cannot move onto flooded or ocean cells. If the cell on which either user is resting floods, the game ends, and both players lose. 

With every 10 ticks, the water level will rise. Cells that are flooded turn blue. Non-flooded cells are green to white, with lighter greens/whites indicating greater heights. Cells that are dangerously close to flooding turn a range from green to red, with red indicating that the cell is ten ticks from being below water level. The darker green the cell, the closer it is to flooding. 

The helicopter is the helicopter image, and is always at the highest point of the island. Targets are the pink circles dispersed around the island. Targets can be collected by colliding the player squares with the Targets. All Targets must be collected and removed from the island in order for the users to be able to board the helicopter. To leave the island, BOTH users have to board the helicopter. Boarding the helicopter means having both users collide with it. 

Either player can be used to get the scuba suit, but only one player will receive the capability. A scuba suit is activated when a user collides with the suit and then presses "s". If the second user presses "s" while already colliding with the scuba suit, it will be activated, and the user will NOT move downwards as it usually does. A scuba suit allows the user to swim through flooded cells, but NOT OceanCells. A user will turn orange if the scuba suit is activated. The scuba suit lasts for 10 ticks. Then the user's color and movement capability return to normal. There is only one scuba suit per board, and once it is used, it cannot be used again.  

The gears on the island represent the Engineer Power. A player must collect every gear on the island by colliding with it. Once there are no more gears left, the player can press "b", which will rebuild a 5x5 square of flooded cells surrounding the user at a height of 5 feet above the water level. Only the user that moves using the arrows can build a 5x5 square around themselves. 
 
