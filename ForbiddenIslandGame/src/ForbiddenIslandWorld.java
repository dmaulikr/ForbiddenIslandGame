// Assignment 9  -  part 2
// Kim Austin
// KimA
// Pine Michelle
// pinemichelle63

import java.awt.Color;
import java.util.*;
import javalib.worldimages.*;
import javalib.impworld.*;

//to represent the world of the forbidden island game
class ForbiddenIslandWorld extends World  {
  // All the cells of the game, including the ocean
  IList<Cell> board;
  // the current height of the ocean
  int waterHeight; // in feet
  // Defines an int constant
  static final int ISLAND_SIZE = 64;
  // represents the scene of the world
  javalib.impworld.WorldScene w = this.getEmptyScene();
  // represents the list of doubles of the world
  ArrayList<ArrayList<Double>> doubleList;
  // represents the cellList of the world
  ArrayList<ArrayList<Cell>> cellList;
  // a shorter variable name for the island size
  int iSize = ForbiddenIslandWorld.ISLAND_SIZE;
  // the player of the game
  Player user;
  //the second player of the game
  Player user2;
  // the helicopter
  HelicopterPiece heli;
  // the targets in the game
  ArrayList<Target> targets;
  //the scuba piece of the game
  Scuba scuba;
  //the engineer power-up
  ArrayList<EngineerPower> engineer;
  // the scale of the game
  static final int scale = 10;
  // the count of ticks
  int count = 0;
  //the second count, to determine when cells should start being in danger of flooding
  int count2 = 0;
  //tells whether the game is over 
  boolean notGameOver = true;
  //tells the score of the game
  int score = 0; 
  //tells whether the player should be scuba diving
  boolean letScuba; 
  //counts how long a scuba ability has been working
  int scubaCount = 0;

  ForbiddenIslandWorld()  {
    this.doubleList = this.makeDoublesList();
    this.cellList = makeCellList(false);
    this.board = new MtList<Cell>().addToList(this.cellList);
    this.updateCells();
    this.user = new Player(this.cellList);
    this.user2 = new Player(this.cellList);
    this.heli = new HelicopterPiece(this.board);
    this.targets = this.generateTargets();
    this.scuba = new Scuba(this.cellList);
    this.engineer = this.generateEngs();

  }

  // on tick events, raises the water with every tick
  public void onTick()  {
    this.updateWater();
    this.changeScuba();
    this.count = this.count + 1;
    this.count2 = this.count2 + 1;
    this.scubaCount = this.scubaCount + 1;
  }

  // EFFECT: updates the water level
  public void updateWater()  {
    if (this.count == 10)  {
      this.waterHeight = this.waterHeight + 1;
      IListIterator<Cell> it = new IListIterator<Cell>(this.board);
      while (it.hasNext())  {
        Cell c = it.next();
        boolean notOcean = !c.isOcean();
        boolean oceanAround = c.top.isOcean() || c.top.isFlooded 
            || c.left.isOcean() || c.left.isFlooded
            || c.bottom.isOcean() || c.bottom.isFlooded 
            || c.right.isOcean() || c.right.isFlooded;
        if (notOcean && c.height <= this.waterHeight && oceanAround)  {
          c.isFlooded = true;
        }
      }
      IListIterator<Cell> it2 = new IListIterator<Cell>(this.board);
      while (it2.hasNext())  {
        Cell c = it2.next();
        if (c.y == user.y && c.x == user.x && c.isFlooded)  {
          this.user.isDead = true;
        }
        if (c.y == user2.y && c.x == user2.x && c.isFlooded)  {
          this.user2.isDead = true;
        }
      }
      this.count = 0;
    }    

  }

  //EFFECT: resets the values to correct island type
  public void reset(boolean rand, boolean terr)  {
    this.score = 0;
    this.notGameOver = true;
    this.waterHeight = 0;
    this.count = 0;
    this.count2 = 0;
    if (terr) {
      this.doubleList = this.makeDoubleTerrainStart();
      this.makeDoubleTerrain(new Posn(0, 0), new Posn(this.iSize, 0), new Posn(0, this.iSize),
          new Posn(this.iSize, this.iSize), false);
    }
    if (!terr)  {
      this.doubleList = this.makeDoublesList(); 
    }
    if (rand)  {
      this.cellList = makeCellList(true);

    }
    if (!rand)  {
      this.cellList = makeCellList(false);
    }
    this.updateCells();
    this.board = new MtList<Cell>().addToList(this.cellList);
    this.user = new Player(this.cellList);
    this.engineer = this.generateEngs();
    this.user2 = new Player(this.cellList);
    this.heli = new HelicopterPiece(this.board);
    this.targets = this.generateTargets();  
    this.scuba = new Scuba(this.cellList);

  }

  //EFFECT: updates the scuba quality of the player
  public void changeScuba()  {
    if (this.scubaCount == 10)  {
      this.user.canScuba = false;
      this.user2.canScuba = false;
      this.user.img = new RectangleImage(10, 10, OutlineMode.SOLID, Color.YELLOW);
      this.user2.img = new RectangleImage(10, 10, OutlineMode.SOLID, Color.YELLOW);
    }
  }

  // handles keystrokes
  //EFFECT: resets all values to those of the right island type
  public void onKeyEvent(String k)  {

    if (k.equals("m"))  {
      this.reset(false, false);
    } 
    else if (k.equals("r"))  {
      this.reset(true, false);
    } 
    else if (k.equals("t"))  {
      this.reset(false, true);
    } 
    else if (k.equals("s") && (this.user.collision(this.scuba) 
        || this.user2.collision(this.scuba)))  {
      if (this.user.collision(this.scuba) && this.scuba.canGet)  {
        this.user.canScuba = true;
        this.user.img = new RectangleImage(10, 10, OutlineMode.SOLID, Color.orange);
        this.scuba.canGet = false;
        this.scuba.img = new EmptyImage();
        this.scubaCount = 0;
      }
      if (this.user2.collision(this.scuba) && this.scuba.canGet)  {
        this.user2.canScuba = true;
        this.user2.img = new RectangleImage(10, 10, OutlineMode.SOLID, Color.orange);
        this.scuba.canGet = false;
        this.scuba.img = new EmptyImage();
        this.scubaCount = 0;
      }
    }
    else if (k.equals("b") && this.user.isEngineer && this.notGameOver) {
      this.rebuild();
      score = score + 1;
    }
    else if (this.notGameOver) {
      Player changedPlayer2 = new Player(this.cellList);
      Player changedPlayer = new Player(this.cellList);
      changedPlayer.x = this.user.x;
      changedPlayer.y = this.user.y;
      changedPlayer.canScuba = this.user.canScuba;
      changedPlayer.img = this.user.img;
      changedPlayer2.x = this.user2.x;
      changedPlayer2.y = this.user2.y;
      changedPlayer2.canScuba = this.user2.canScuba;
      changedPlayer2.img = this.user2.img;
      if (this.user.canScuba)  {
        changedPlayer.movePlayerScuba(k, this.board, "right", "left", "up", "down");
        Player original = this.user;
        if (!changedPlayer.collision(original))  {
          score = score + 1;
        }
        this.user = changedPlayer;
      }
      if (this.user2.canScuba)  {
        changedPlayer2.movePlayerScuba(k, this.board, "d", "a", "w", "s");
        Player original2 = this.user2;
        if (!changedPlayer2.collision(original2))  {
          score = score + 1;
        }
        this.user2 = changedPlayer2;
      }
      if (!this.user.canScuba)  {
        changedPlayer.movePlayer(k, this.board, "right", "left", "up", "down");
        Player original = this.user;
        if (!changedPlayer.collision(original))  {
          score = score + 1;
        }
        this.user = changedPlayer;
      }
      if (!this.user2.canScuba)  {
        changedPlayer2.movePlayer(k, this.board, "d", "a", "w", "s");
        Player original2 = this.user2;
        if (!changedPlayer2.collision(original2))  {
          score = score + 1;
        }
        this.user2 = changedPlayer2;
      }
    }

  }

  //EFFECT: Rebuild 5x5 blocks around the player by 5
  void rebuild() {
    for (Cell t: this.board) {
      if (this.user.x - 2 <= t.x && this.user.x + 2 >= t.x
          && this.user.y - 2 <= t.y && this.user.y + 2 >= t.y) {
        if (t.isFlooded) {
          t.height = this.waterHeight + 5;
          if (t.height > this.waterHeight) {
            t.isFlooded = false;
          }
        }
      }
    }
  }

  // EFFECT: handles key releases, changes attributes of players
  public void onKeyReleased(String k)  {
    this.removeEng();
    this.removeTargets();
    if (targets.size() == 0 && !this.heli.canGet)  {
      this.heli.canGet = true;
    }
    if (this.user.collision(this.heli) && this.heli.canGet)  {
      this.user.won = true;
    }
    if (this.user2.collision(this.heli) && this.heli.canGet)  {
      this.user2.won = true;
    }
  }




  // handles won or lost scenarios
  //EFFECT: changes game over to true if certain conditions are met
  public WorldScene wonOrLost()  {
    if (this.user.won && this.user2.won)  {
      this.notGameOver = false;
      return this.displayText("You escaped!");
    } 
    else if (this.user.isDead || this.user2.isDead)  {
      this.notGameOver = false;
      return this.displayText("You lost!");
    }
    else  {
      return this.w;
    }
  }

  // creates the end scene of the game
  public WorldScene displayText(String s)  {
    w.placeImageXY(new TextImage(s, 20, FontStyle.BOLD, Color.RED), this.iSize * 10 / 2, 27);
    return w;
  }

  //EFFECT: removes targets which collide with the user
  void removeTargets()  {
    for (Iterator<Target> iter = targets.iterator(); iter.hasNext();)  {
      Target t = iter.next();
      if (this.user.collision(t) || this.user2.collision(t))  {
        iter.remove();
      }
    }
    if (targets.size() == 0)  {
      this.heli.canGet = true;
    }
  }

  //EFFECT: removes engineer pieces
  void removeEng() {
    for (Iterator<EngineerPower> iter = engineer.iterator(); iter.hasNext();) {
      EngineerPower t = iter.next();
      if (this.user.collision(t) || this.user2.collision(t)) {
        iter.remove();
      }
    }
    if (engineer.size() == 0) {
      this.user.isEngineer = true;
    }
  }

  // makes the scene
  public WorldScene makeScene()  {
    this.w = this.getEmptyScene();
    int shift = scale / 2;
    IListIterator<Cell> it = new IListIterator<Cell>(this.board);
    while (it.hasNext())  {
      Cell c = it.next();
      w.placeImageXY(c.oneCell(this.waterHeight, this.count2),
          c.x * scale + shift, c.y * scale + shift);
    }
    w.placeImageXY(this.user.displayPiece(), 
        this.user.x * scale + shift, this.user.y * scale + shift);
    w.placeImageXY(this.user2.displayPiece(), 
        this.user2.x * scale + shift, this.user2.y * scale + shift);
    w.placeImageXY(this.heli.displayPiece(),
        this.heli.x * scale + shift, this.heli.y * scale + shift);
    for (Target t : this.targets)  {
      w.placeImageXY(t.displayPiece(), 
          t.x * scale + shift, t.y * scale + shift);
    }
    w.placeImageXY(new TextImage("MOVES: " + score, 20,FontStyle.BOLD, Color.RED), 
        this.iSize * 10 - 70, this.iSize * 10 - 40);
    w.placeImageXY(scuba.displayPiece(), scuba.x * scale + shift, scuba.y * scale + shift);
    for (EngineerPower t : this.engineer) {
      w.placeImageXY(t.displayPiece(), t.x * scale + shift, t.y * scale + shift);
    }
    return this.wonOrLost();
  }

  // generates an ArrayList of randomly placed targets
  ArrayList<Target> generateTargets()  {
    ArrayList<Target> tgts = new ArrayList<Target>();
    for (int i = 0; i < 6; i = i + 1)  {
      tgts.add(new Target(this.cellList));
    }
    return tgts;
  }

  // generates an ArrayList of randomly placed targets
  ArrayList<EngineerPower> generateEngs() {
    ArrayList<EngineerPower> tgts = new ArrayList<EngineerPower>();
    for (int i = 0; i < 1; i = i + 1) {
      tgts.add(new EngineerPower(this.cellList));
    }
    return tgts;
  }

  // makes a list of doubles to represent the heights of
  // the cells in the mountain island
  ArrayList<ArrayList<Double>> makeDoublesList()  {
    ArrayList<ArrayList<Double>> a = new ArrayList<ArrayList<Double>>();
    for (int i = 0; i <= this.iSize; i = i + 1)  {
      a.add(this.makeDoublesRow(i));
    }
    return a;
  }

  // makes a row of doubles in a mountain island
  ArrayList<Double> makeDoublesRow(int rowNum)  {
    ArrayList<Double> a = new ArrayList<Double>();
    double x1 = this.iSize / 2;
    for (int j = 0; j <= this.iSize; j++)  {
      double x0 = j;
      double y0 = rowNum;
      double manDist = (Math.abs(y0 - x1) + Math.abs(x0 - x1));
      a.add(this.iSize - manDist - this.iSize / 2);
    }
    return a;
  }

  // makes a list of cells in a mountain island
  ArrayList<ArrayList<Cell>> makeCellList(boolean rand)  {
    ArrayList<ArrayList<Cell>> a = new ArrayList<ArrayList<Cell>>();
    for (int i = 0; i <= this.iSize; i = i + 1)  {
      if (a.size() <= 1)  {
        if (rand)  {
          a.add(this.makeCellRow(i, null, true));
        } else  {
          a.add(this.makeCellRow(i, null, false));
        }
      } else if (a.size() > 1)  {
        if (rand)  {
          a.add(this.makeCellRow(i, a.get(a.size() - 1), true));
        } else  {
          a.add(this.makeCellRow(i, a.get(a.size() - 1), false));
        }
      }
    }
    return a;
  }

  // makes a row of cells in a mountain island
  ArrayList<Cell> makeCellRow(int rowNum, ArrayList<Cell> above, boolean rand)  {
    ArrayList<Cell> a = new ArrayList<Cell>();
    for (int i = 0; i <= this.iSize; i = i + 1)  {
      double e = this.doubleList.get(rowNum).get(i);
      if (!rand)  {
        if (e <= 0)  {
          OceanCell c = new OceanCell(e, i, rowNum);
          a.add(c);
        } else  {
          Cell c = new Cell(e, i, rowNum, false);
          a.add(c);
        }
      } else  {
        double r = new Random().nextInt(this.iSize / 2);
        if (e <= 0)  {
          OceanCell c = new OceanCell(0, i, rowNum);
          a.add(c);
        } else  {
          Cell c = new Cell(r, i, rowNum, false);
          a.add(c);
        }
      }
    }
    return a;
  }

  // EFFECT: updates all links for the cells
  void updateCells()  {
    for (int i = 0; i <= (this.cellList.size() - 1); i = i + 1)  {
      for (int j = 0; j <= (this.cellList.get(0).size() - 1); j = j + 1)  {
        if (j >= 1)  {
          this.cellList.get(i).get(j).left = this.cellList.get(i).get(j - 1);
          this.cellList.get(i).get(j - 1).right = this.cellList.get(i).get(j);
        }
        if (i >= 1)  {
          this.cellList.get(i - 1).get(j).bottom = this.cellList.get(i).get(j);
          this.cellList.get(i).get(j).top = this.cellList.get(i - 1).get(j);
        }
        this.cellList.get(0).get(i).bottom = this.cellList.get(1).get(i);
        this.cellList.get(1).get(i).top = this.cellList.get(0).get(i);
        this.cellList.get(this.cellList.size() - 1).get(i).top = 
            this.cellList.get(this.cellList.size() - 2).get(i);
        this.cellList.get(this.cellList.size() - 2).get(i).bottom = 
            this.cellList.get(this.cellList.size() - 1).get(i);

      }
    }
  }

  //EFFECT: makes a double list for a random terrain, by averaging and randomly nudging midpoint
  //values
  void makeDoubleTerrain(Posn tl, Posn tr, Posn bl, Posn br, boolean startSetting)  {
    int midX = tl.x + ((tr.x - tl.x) / 2);
    int midY = tl.y + ((bl.y - tl.y) / 2);  
    boolean equality = tl.x + 1 >= tr.x || tl.y + 1 >= bl.y;
    if (!equality && !startSetting)  {  
      this.makeDoubleTerrain(new Posn(tl.x, tl.y), new Posn(midX, tl.y), new Posn(tl.x, midY),
          new Posn(midX, midY), true);
      this.makeDoubleTerrain(new Posn(midX, tr.y), new Posn(tr.x, tr.y), new Posn(midX, midY),
          new Posn(tr.x, midY), true);
      this.makeDoubleTerrain(new Posn(bl.x, midY), new Posn(midX, midY), new Posn(bl.x, bl.y),
          new Posn(midX, bl.y), true);
      this.makeDoubleTerrain(new Posn(midX, midY), new Posn(br.x, midY), new Posn(midX, br.y),
          new Posn(br.x, br.y), true);
    }
    if (!equality && startSetting)  {
      double tlI = this.doubleList.get(tl.y).get(tl.x);
      double trI = this.doubleList.get(tr.y).get(tr.x);
      double blI = this.doubleList.get(bl.y).get(bl.x);
      double brI = this.doubleList.get(br.y).get(br.x);
      double calcTop = new Random().nextInt(4) - 3 + (tlI + trI) / 2;
      double calcBottom = new Random().nextInt(4) - 3 + (blI + brI) / 2;
      double calcLeft = new Random().nextInt(4) - 3 + (tlI + blI) / 2;
      double calcRight = new Random().nextInt(4) - 3 + (trI + brI) / 2;
      double calcMid = new Random().nextInt(4) - 3 + (tlI + trI + blI + brI) / 4;
      this.doubleList.get(tl.y).set(midX, calcTop);
      this.doubleList.get(bl.y).set(midX, calcBottom);
      this.doubleList.get(midY).set(tl.x, calcLeft);
      this.doubleList.get(midY).set(tr.x, calcRight);
      this.doubleList.get(midY).set(midX, calcMid);
      this.makeDoubleTerrain(new Posn(tl.x, tl.y), 
          new Posn(midX, tl.y), 
          new Posn(tl.x, midY),
          new Posn(midX, midY), true);
      this.makeDoubleTerrain(new Posn(midX, tr.y), 
          new Posn(tr.x, tr.y), 
          new Posn(midX, midY),
          new Posn(tr.x, midY), true);
      this.makeDoubleTerrain(new Posn(bl.x, midY), 
          new Posn(midX, midY), 
          new Posn(bl.x, bl.y),
          new Posn(midX, bl.y), true);
      this.makeDoubleTerrain(new Posn(midX, midY), 
          new Posn(br.x, midY), 
          new Posn(midX, br.y),
          new Posn(br.x, br.y), true);
    }
  }

  // helps make a double list for a random terrain
  ArrayList<ArrayList<Double>> makeDoubleTerrainStart()  {
    ArrayList<ArrayList<Double>> a = new ArrayList<ArrayList<Double>>();
    for (int i = 0; i < ISLAND_SIZE + 1; i = i + 1)  {
      ArrayList<Double> r = new ArrayList<Double>();
      for (int j = 0; j < ISLAND_SIZE + 1; j = j + 1)  {
        r.add(0.0);
      }
      a.add(r);
    }
    a.get(this.iSize / 2).set(this.iSize / 2, 31.0); // mid
    a.get(this.iSize / 2).set(this.iSize, 1.0); // r
    a.get(0).set(this.iSize / 2, 1.0); // t
    a.get(this.iSize).set(this.iSize / 2, 1.0); // b
    a.get(this.iSize / 2).set(0, 1.0); // l

    return a;
  }
}
