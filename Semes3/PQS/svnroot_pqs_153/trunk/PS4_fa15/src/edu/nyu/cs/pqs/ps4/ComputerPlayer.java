package edu.nyu.cs.pqs.ps4;

import java.util.UUID;
import java.util.Random;

/**
 * @author Keeyon
 * Computer player that places a spot if it will create a win
 * To run, Create a new object and call connectToServer
 */
public class ComputerPlayer implements ConnectFourPlayer {
  
  private BoardSpace myBoardSpace;
  private UUID myUUID;
  private String myName;
  private Server gameServer;
  private int totalColumns;
  private int totalRows;
  private Random randomColumnGenerator;
  private BoardSpace winningBoardSpace;

  /** Private constructor.  To get an object, call 
   * static factory method newInstance(String name)
   * Private so we can hide implementation
   * @param name
   */
  private ComputerPlayer(String name) {
    myName = name;
    gameServer = Server.getInstance();
    randomColumnGenerator = new Random();
    winningBoardSpace = BoardSpace.EMPTY;
  }
  
  /** Computer player Static factory method 
   * @param name
   * @return new Instantiated  Computer Object
   */
  public static ComputerPlayer newInstance(String name) {
    return new ComputerPlayer(name);
  }
  
  /**  Connection to the server.
   * Set many of our member variables
   */
  public void connectToServer() {
    myUUID = gameServer.registerPlayer(this, myName);
    myBoardSpace = gameServer.getMyBoardSpace(myUUID);
    totalColumns = gameServer.getTotalColumns();
    totalRows = gameServer.getTotalRows();
    sendReady();
  }
  
  @Override
  public void PlayerHasWon(String playerName, BoardSpace winningPlayer) {
    this.winningBoardSpace = winningPlayer;
  }
  
  /** Used to check the winning player
   * @return
   */
  public BoardSpace getWinningPlayer() {
    return winningBoardSpace;
  }

  @Override
  public void PlayerHasMadeMove(String playerName, BoardSpace playerMadeMove,
      int column) {
  }

  @Override
  public void PlayerTurnToMakeMove(String playerName, BoardSpace playersTurn) {
    if (playerName.equals(myName) && playersTurn == myBoardSpace) {
      makeMove();
    }
  }
  
  /**
   * Let the server know that we are ready to play
   */
  private void sendReady() {
    gameServer.playerIsReady(myUUID, true);
  }
  
  /** Logic to make a winning move
   * If no winning moves make random
   */
  private void makeMove() {
    if(!madeWinningMove()) {
      makeRandomMove();
    }
  }
  
  /** Random Valid Move implementation
   * 
   */
  private void makeRandomMove() {
    boolean validMove;
    int randomColumn;
    do {
      randomColumn = randomColumnGenerator.nextInt(totalColumns);
      validMove = gameServer.moveIsValid(randomColumn);
    } while (!validMove );
    sendMove(randomColumn);
  }
  
  /** This will check if there is a winning move, and make it if so
   * @return true if we make winning move, else false
   */
  private boolean madeWinningMove() {
    for (int columnAttempt = 0; columnAttempt < totalColumns; columnAttempt++) {
      if (gameServer.moveIsValid(columnAttempt)) {
        BoardSpace[][] newBoard = gameServer.getBoardLayout();
        placePiece(columnAttempt, myBoardSpace, newBoard);
        if (checkIfWinningMove(newBoard)) {
          sendMove(columnAttempt);
          return true;
        }
      }
    }
  
    return false;
  }
  
  /** Send move to the server
   * @param column
   */
  private void sendMove(int column) {
    gameServer.makeMoveAtZeroIndexedColumn(myUUID, column);
  }
  
  /**  This is how we check if a board contains a win
   * @param board
   * @return
   */
  private boolean checkIfWinningMove(BoardSpace[][] board) {
    return (verticleWinnerCheck(myBoardSpace, board) || 
        horizontalWinnerCheck(myBoardSpace, board) ||
        topLeftBottomRightDiagonalWinnerCheck(myBoardSpace, board) ||
        topRightBottomLeftDiagonalWinnerCheck(myBoardSpace, board));
  }
  
  /** Pass in a player.  This returns if the passed in player has won
   * with a verticle winning position 
   * @param checkingPlayer
   * @return if the passed in player has won
   */
  private boolean verticleWinnerCheck(BoardSpace checkingPlayer, BoardSpace[][] board) {
    int maxRowToCheck = this.totalRows - 4;
    for (int column = 0; column < this.totalColumns; column++) {
      for (int row = 0; row < maxRowToCheck + 1; row++) {
        if (board[row][column] == checkingPlayer &&
            board[row + 1][column] == checkingPlayer &&
            board[row + 2][column] == checkingPlayer &&
            board[row + 3][column] == checkingPlayer) {
          return true;
        }
      }
    }
    
    return false;
  }
  
  /** This is how we place pieces into a board to test if we have a winning move
   * @param column
   * @param currentBoardSpace
   * @param board
   */
  private void placePiece(int column, BoardSpace currentBoardSpace, BoardSpace[][] board) {
    for (int row = totalRows - 1; row >= 0; row--) {
      if (board[row][column] == BoardSpace.EMPTY) {
        board[row][column] = currentBoardSpace;
        return;
      }
    }
    
    throw new IllegalStateException("We were not able to place a spot in given board");
  }

  /** Check if we have a board with a horizontal win condition met
   * @param checkingPlayer
   * @param board
   * @return
   */
  private boolean horizontalWinnerCheck(BoardSpace checkingPlayer, BoardSpace[][] board) {
    int maxColumnToCheck = this.totalColumns - 4;
    
    for (int row = 0; row < this.totalRows; row++) {
      for (int column = 0; column < maxColumnToCheck + 1; column++) {
        if (board[row][column] == checkingPlayer &&
            board[row][column + 1] == checkingPlayer &&
            board[row][column + 2] == checkingPlayer &&
            board[row][column + 3] == checkingPlayer) {
          return true;
        }
      }
    }
    
    return false;
  }
  
  /** Helper method to determine if a certain player has won.  This checks
   * left to right diagonal going down
   * @param checkingPlayer
   * @return If the passed in player has won
   */
  private boolean topLeftBottomRightDiagonalWinnerCheck(BoardSpace checkingPlayer, BoardSpace[][] board) {
    int maxColumnToCheck = this.totalColumns - 4;
    int maxRowToCheck = this.totalRows - 4;
    for (int row = 0; row < maxRowToCheck + 1; row++) {
      for (int column = 0; column < maxColumnToCheck + 1; column++) {
        if (board[row][column] == checkingPlayer &&
            board[row + 1][column + 1] == checkingPlayer &&
            board[row + 2][column + 2] == checkingPlayer &&
            board[row + 3][column + 3] == checkingPlayer) {
          return true;
        }
      }
    }
    
    return false;
  }
  
  /** Helper method to determine if a certain player has won.  This checks
   * left to right diagonal going up
   * @param checkingPlayer
   * @return boolean determining if the passed in player has won.
   */
  private boolean topRightBottomLeftDiagonalWinnerCheck(BoardSpace checkingPlayer, BoardSpace[][] board) {
    int maxRowToCheck = this.totalRows - 4;
    int startColumn = 3;
    
    for (int column = startColumn; column < this.totalColumns; column++) {
      for (int row = 0; row < maxRowToCheck + 1; row++) {
        if (board[row][column] == checkingPlayer &&
            board[row + 1][column - 1] == checkingPlayer &&
            board[row + 2][column - 2] == checkingPlayer &&
            board[row + 3][column - 3] == checkingPlayer) {
          return true;
        }
      }
    }
    
    return false;
  }

  /** myBoardSpace getter
   * @return
   */
  public BoardSpace getMyBoardSpace() {
    return myBoardSpace;
  }

  /** UUID getter
   * @return
   */
  public UUID getMyUUID() {
    return myUUID;
  }

  /** Name getter
   * @return
   */
  public String getMyName() {
    return myName;
  }
}

