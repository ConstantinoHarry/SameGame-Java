import java.util.*;
import java.io.*;
/**
 * @author: ______Constantino Harry Alexander (25206605)_________
 */
public class SameGame {

    //Total 6 different types of symbols, including EMPTY
    static final char EMPTY = ' ';
    static final char SELECTED = '*';
    static final char[] SYMBOLS = {EMPTY, '@', '=', '^', '+'};

    //for testing purpose we might change to the following, or other arrays
//    static final char[] SYMBOLS = {EMPTY, '@', '='};

    static final int MAX_ROW = 10;
    static final int MAX_COL = 26;
    // Store the top score file in this file at your current path
    // Create such file if it does not exist
    static final String TOP_SCORE_FILE = "top_scores.txt";

    public static void main(String[] args) {
        new SameGame().startGame();
    }
    void startGame() {
        char[][] gameboard = new char[MAX_ROW][MAX_COL]; //
        randomizeBoard(gameboard);
        printHelp();

        Scanner scanner = new Scanner(System.in);
        int score = 0;
        char[][] selectedBoard;
        while (!isGameOver(gameboard)) {
            printBoard(gameboard);
            System.out.print("Enter your move in the format column-row, e.g. A-5, or press 'h' for help, 'q' to quit: ");
            String input = scanner.nextLine().trim();
            selectedBoard = null;
            switch (input) {
                case "h":
                    printHelp();
                    break;
                case "q":
                    System.out.println("Your score is: " + score);
                    System.out.println("Thank you for playing SameGame!");
                    return;
                case "r":
                    randomizeBoard(gameboard);
                    score = 0; // Reset score on restart
                    printBoard(gameboard);
                    break;
                case "t":
                    System.out.println("Tips: selected the biggest segment of blocks for you..");
                    selectedBoard = selectBiggestSegment(gameboard);
                    //continue in default case
                default:
                    // Check if the input is in the format of "A-5" or similar
                    // Then call the appropriate methods to
                    // 1. Validate the selection if it is a valid selection
                    // 2. If valid, print the number of blocks being selected and
                    //    copy the gameboard to the variable selectedBoard
                    // 3. If it is invalid, print an error message and continue to the next iteration
                    // 4. If the selectedBoard is set, either through the "t" command or a valid selection,
                    //    print the selectedBoard and ask for confirmation to remove the selected blocks
                    // 5. If confirmed, increase the score and remove the selected blocks from the gameboard
                    // 6. If not confirmed, print a message and continue to the next iteration

                    if (input.length() >= 3 && input.contains("-")) {
                        String[] parts = input.split("-");
                        if (parts.length == 2) {
                            try {
                                String colStr = parts[0].toUpperCase();
                                char colChar = colStr.charAt(0);
                                int row = Integer.parseInt(parts[1]);

                                // Validate column format (A-Z)
                                if (colStr.length() != 1 || colChar < 'A' || colChar > 'Z') {
                                    System.out.print("\nInvalid Input please try again!");
                                    printHelp();
                                    break;
                                }
                                // Validate row format (0-9)
                                if (row < 0 || row >= MAX_ROW) {
                                    System.out.print("\nInvalid Input please try again!");
                                    printHelp();
                                    break;
                                }
                                // Check if selection is valid
                                if (isValidSelection(gameboard, row, colChar)) {
                                    // Create a copy for selection preview
                                    selectedBoard = copyArray(gameboard);
                                    int blocksSelected = select(selectedBoard, row, colChar);
                                    System.out.println("\nYou selected " + blocksSelected + " blocks");
                                } else {
                                    System.out.println("Invalid selection! No adjacent blocks with same symbol.");
                                    break;
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid row format! Please use numbers like 0, 1, 2, etc.");
                                break;
                            }
                        } else {
                            System.out.println("Invalid format! Please use format like A-5");
                            break;
                        }
                    } else if (!input.equals("t")) {
                        System.out.println("Invalid input! Please use format like A-5 or press 'h' for help.");
                        break;
                    }

                    // Handle the selected board (either from "t" command or valid selection)
                    if (selectedBoard != null) {
                        printBoard(selectedBoard);
                        System.out.print("Please confirm if you want to remove the selected blocks (y/n): ");
                        String confirm = scanner.nextLine().trim().toLowerCase();

                    if (confirm.equals("y") || confirm.equals("yes")) {
                        // Calculate score before removal
                        int roundScore = computeScore(selectedBoard);

                        // Remove selected blocks from the actual gameboard
                        gameboard = removeSelected(selectedBoard);

                        // Update total score
                        score += roundScore;
                        System.out.println("\nYour current total score is: " + score);
                    }
                }
                break;
            }
        }
        System.out.println("\n\n");
        printBoard(gameboard); //after the game is over, print the final board again.

        System.out.println("Game over! Your final score is: " + score);
        topscorer(score); //to display the top scores and save the current score if applicable
    }
    /**
     * This method is to load the top score and update the top score to a file.
     * 
     * The method shall read from the file TOP_SCORE_FILE and load the top score
     * display the top five scores including the score from the current game on screen.
     * Then it shall save the top five scores to the file TOP_SCORE_FILE.
     * 
     * The format of the file shall be
     * KEV 1100
     * JON 900
     * SAN 884
     * JIM 700
     * CH 500
     * 
     * Each line contains an upper case name with at least 1 character and at most 3 characters,
     * followed by a space and then the score which is an integer. The file shall be sorted
     * by the score in descending order. It is possible that the file contains less than five lines.
     * 
     * When the player's score is in the top five, the player will be prompted to enter their name
     * to save the score. The name must be an upper case string with at least 1 character and 
     * at most 3 characters or it will be rejected. If the name is valid, the score will 
     * be saved to the file.
     * 
     * 
     * @param score is the score to be saved.
     */
    void topscorer(int score) {
        // Step 1: Read existing scores from file
        String[] names = new String[6]; // 5 existing + current player
        int[] scores = new int[6];
        int count = 0;

        // Read existing top scores
        try {
            Scanner fileScanner = new Scanner(new File(TOP_SCORE_FILE));
            while (fileScanner.hasNextLine() && count < 5) {
                String line = fileScanner.nextLine().trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split(" ");
                    if (parts.length >= 2) {
                        names[count] = parts[0];
                        scores[count] = Integer.parseInt(parts[1]);
                        count++;
                    }
                }
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            // File doesn't exist yet, that's OK - we'll start with empty list
        }

        // Step 2: Add current score to the list
        names[count] = ""; // Empty name for current player
        scores[count] = score;
        count++;

        // Step 3: Sort the scores in descending order
        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - i - 1; j++) {
                if (scores[j] < scores[j + 1]) {
                    // Swap scores
                    int tempScore = scores[j];
                    scores[j] = scores[j + 1];
                    scores[j + 1] = tempScore;

                    // Swap names
                    String tempName = names[j];
                    names[j] = names[j + 1];
                    names[j + 1] = tempName;
                }
            }
        }

        // Step 4: Display top scores
        System.out.println("\n=== TOP SCORES ===");
        for (int i = 0; i < Math.min(count, 5); i++) {
            String displayName = names[i].isEmpty() ? "---" : names[i];
            System.out.println((i + 1) + ". " + displayName + " " + scores[i]);
        }

        // Step 5: Check if current score made it to top 5 and get player name
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < Math.min(count, 5); i++) {
            if (names[i].isEmpty() && scores[i] == score) {
                System.out.print("\nCongratulations! You made it to the top 5!");
                System.out.print(" Enter your name (1-3 uppercase letters): ");
                String playerName = scanner.nextLine().trim().toUpperCase();

                // Validate name
                if (playerName.matches("[A-Z]{1,3}")) {
                    names[i] = playerName;
                    System.out.println("Score saved!");
                } else {
                    System.out.println("Invalid name! Must be 1-3 uppercase letters. Score not saved.");
                    // Remove the empty entry by shifting array
                    for (int j = i; j < count - 1; j++) {
                        names[j] = names[j + 1];
                        scores[j] = scores[j + 1];
                    }
                    count--;
                }
                break;
            }
        }

        // Step 6: Write top 5 scores back to file
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(TOP_SCORE_FILE));
            for (int i = 0; i < Math.min(count, 5); i++) {
                if (!names[i].isEmpty()) { // Only write entries with valid names
                    writer.println(names[i] + " " + scores[i]);
                }
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving scores to file: " + e.getMessage());
        }
    }

    /**
     * This method is to compute the score based on the number of blocks removed.
     * 
     * The score is computed based on the number of blocks removed. The score is
     * calculated as follows:
     *  Let n be the number of blocks removed. The base score is n * (n + 1). For
     * each column of blocks that is completely removed, an additional 10 points 
     * are added to the score. 
     *
     * @param gameboard is a 2D char array that is always non-null and pointed to a
     *                   rectangular area.
     * @return the score
     */
    int computeScore(char[][] gameboard) {
        // count number of blocks slected (n)
        int n = 0;
        for(int i = 0; i < gameboard.length; i++ ){
            for (int j = 0; j < gameboard[i].length; j++){
                if(gameboard[i][j] == SELECTED){
                    n++;
                }
            }
        }
        //count number of empty columns that had been removed
        int emptyCols = 0;
        for(int i = 0; i < gameboard.length; i++){
           // logical value to return true if for empty or Selected rows
            boolean colsEmpty = true;
            for(int j = 0; j < gameboard[i].length; j++){
                if(gameboard[i][j] != EMPTY && gameboard[i][j] != SELECTED){
                    colsEmpty = false;
                    break;
                }
            }
            if(colsEmpty){
                emptyCols++;
            }
        }

        return (n * (n + 1)) + (emptyCols * 10);
    }
    /**
     * This method is to select the biggest segment of blocks that can be selected.
     * 
     * The method will iterate through the gameboard and find the biggest segment of 
     * blocks that can be selected. It will return a new gameboard with the selected 
     * blocks marked as SELECTED. If there are multiple segments with the same size,
     * any one of them can be returned.
     * 
     * @param gameboard is a 2D char array that is always non-null and pointed to a
     *                   rectangular area.
     * @return a new gameboard with the selected blocks marked as SELECTED.
     */
    char[][] selectBiggestSegment(char[][] gameboard) {
        char[][] result = copyArray(gameboard);
        int maxSize = 0;
        int bestRow = -1;
        int bestCol = -1;

        // Track visited positions to avoid re-checking
        boolean[][] visited = new boolean[gameboard.length][gameboard[0].length];

        // Check every position on the board
        for (int i = 0; i < gameboard.length; i++) {
            for (int j = 0; j < gameboard[i].length; j++) {
                // Skip empty positions and already visited positions
                if (gameboard[i][j] != EMPTY && !visited[i][j]) {
                    // Count the size of this segment using BFS
                    int segmentSize = countSegmentSize(gameboard, i, j, visited);

                    // If this is the biggest segment found so far, remember its position
                    if (segmentSize > maxSize) {
                        maxSize = segmentSize;
                        bestRow = i;
                        bestCol = j;
                    }
                }
            }
        }

        // If found a segment (maxSize > 1), select it on the result board
        if (maxSize > 1 && bestRow != -1 && bestCol != -1) {
            select(result, bestRow, (char)('A' + bestCol));
        }

        return result;
    }
    /**
     * Helper method to count the size of a connected segment using BFS
     */
    private int countSegmentSize(char[][] gameboard, int startRow, int startCol, boolean[][] visited) {
        char targetSymbol = gameboard[startRow][startCol];
        int count = 0;

        // Manual queue implementation
        int maxSize = gameboard.length * gameboard[0].length;
        int[] queueRow = new int[maxSize];
        int[] queueCol = new int[maxSize];
        int front = 0, rear = 0;

        // Local visited array for this BFS
        boolean[][] localVisited = new boolean[gameboard.length][gameboard[0].length];

        // Start with initial position
        queueRow[rear] = startRow;
        queueCol[rear] = startCol;
        rear++;
        localVisited[startRow][startCol] = true;
        visited[startRow][startCol] = true; // Mark as visited in global array too

        while (front < rear) {
            int currentRow = queueRow[front];
            int currentCol = queueCol[front];
            front++;
            count++;

            // Check all 4 directions
            // Check UP
            if (currentRow > 0 && !localVisited[currentRow-1][currentCol] &&
                    gameboard[currentRow-1][currentCol] == targetSymbol) {
                queueRow[rear] = currentRow - 1;
                queueCol[rear] = currentCol;
                rear++;
                localVisited[currentRow-1][currentCol] = true;
                visited[currentRow-1][currentCol] = true;
            }

            // Check DOWN
            if (currentRow < gameboard.length-1 && !localVisited[currentRow+1][currentCol] &&
                    gameboard[currentRow+1][currentCol] == targetSymbol) {
                queueRow[rear] = currentRow + 1;
                queueCol[rear] = currentCol;
                rear++;
                localVisited[currentRow+1][currentCol] = true;
                visited[currentRow+1][currentCol] = true;
            }

            // Check LEFT
            if (currentCol > 0 && !localVisited[currentRow][currentCol-1] &&
                    gameboard[currentRow][currentCol-1] == targetSymbol) {
                queueRow[rear] = currentRow;
                queueCol[rear] = currentCol - 1;
                rear++;
                localVisited[currentRow][currentCol-1] = true;
                visited[currentRow][currentCol-1] = true;
            }

            // Check RIGHT
            if (currentCol < gameboard[0].length-1 && !localVisited[currentRow][currentCol+1] &&
                    gameboard[currentRow][currentCol+1] == targetSymbol) {
                queueRow[rear] = currentRow;
                queueCol[rear] = currentCol + 1;
                rear++;
                localVisited[currentRow][currentCol+1] = true;
                visited[currentRow][currentCol+1] = true;
            }
        }

        return count;
    }
    /**
     * Copy the 2D char array to a new 2D char array.
     * 
     * This method will create a new 2D char array that is the same size as the
     * parameter src. It will copy the content of the src to the new array.
     * 
     * @param src is a 2D char array that is always non-null and pointed to a
     *             rectangular area.
     * @return a new 2D char array that is a copy of the src.
     */

    char[][] copyArray(char[][] src) {
        char[][] copyArr = new char[src.length][src[0].length];
        for(int i = 0; i < src.length; i++){
            for(int j = 0; j < src[i].length; j++){
                copyArr[i][j] = src[i][j];
            }
        }
        return copyArr;
    }

    /**
     * This method is to print the help menu.
     * 
     * By referring to the startGame method, create a proper printHelp method
     *     // Check if the input is in the format of "A-5" or similar
     *                     // Then call the appropriate methods to
     *                     // 1. Validate the selection if it is a valid selection
     *                     // 2. If valid, print the number of blocks being selected and
     *                     //    copy the gameboard to the variable selectedBoard
     *                     // 3. If it is invalid, print an error message and continue to the next iteration
     *                     // 4. If the selectedBoard is set, either through the "t" command or a valid selection,
     *                     //    print the selectedBoard and ask for confirmation to remove the selected blocks
     *                     // 5. If confirmed, increase the score and remove the selected blocks from the gameboard
     *                     // 6. If not confirmed, print a message and continue to the next iteration
     */
    void printHelp() {
        System.out.println("\n===== Same Game Help =====");
        System.out.println("HOW TO PLAY:");
        System.out.println("- Select groups of adjacent blocks with the same symbol");
        System.out.println("- Blocks must be connected up, down, left, or right");
        System.out.println("- At least 2 blocks must be selected");
        System.out.println("- Empty columns will be removed and columns will shift left");

        System.out.println("\nVALID INPUT FORMAT:");
        System.out.println("- Column must be a single letter: A to Z");
        System.out.println("- Row must be a number: 0 to 9");

        System.out.println("\nAVAILABLE COMMANDS:");
        System.out.println("A-5    - Select block at column A, row 5");
        System.out.println("h      - Show this help message");
        System.out.println("q      - Quit the game");
        System.out.println("r      - Restart the game");
        System.out.println("t      - Get tip for biggest segment");

        System.out.println("\nSCORING:");
        System.out.println("- Base score: n × (n + 1) where n = blocks removed");
        System.out.println("- Bonus: +10 points for each completely empty column");
        System.out.println("==========================\n");
    }
    /**
     * This method is to print the game board with the coordinate labels.
     * 
     * The first column and the last column should be the y-coordinate labeled from "0" to "9".
     * The first row and the last row should be the x-coordinate labeled from "A" to "?" where "?"
     * is the n-th character counts from "A" and n is the total number of column of the gameboard.
     * 
     * At the beginning, the game board has MAX_COL (which is 26) columns. However, when the 
     * game keep playing, the game board may shrink horizontally. The game board will never shrink 
     * vertically, i.e., it will always display MAX_ROW (which is 10) rows of blocks even if some 
     * rows are empty.
     * 
     * The content of the game board should be printed inside the coordinate borders. The symbols of the label
     * should be referred to the constant SYMBOLS.
     * 
     * We can assume that the size of the gameboard will never be null and is always pointed 
     * to a rectangular char array.
     * 
     * If the player plays very well, the game board may be empty at the end of the game, i.e., 
     * it has no columns. In this case, the method should print a message saying "Gameboard is empty."
     */
    void printBoard(char[][] gameboard) {
        //Print Header
        System.out.print(" "); // Hard code spce
        for(int i = 0; i < gameboard[0].length; i++){
            char ch = (char)(i + 65);
            System.out.print(ch); // int i incrementing + 65 equal to A to Z
        }
        for(int i = 0; i < gameboard.length; i++){
            System.out.println();
            System.out.print(i);
            for(int j = 0; j < gameboard[i].length; j++){
                System.out.print(gameboard[i][j]);
            }
            System.out.print(i);
        }
        System.out.println();
        System.out.print(" "); // Hard code spce
        for(int i = 0; i < gameboard[0].length; i++){
            char ch = (char)(i + 65);
            System.out.print(ch); // int i incrementing + 65 equal to A to Z
        }
        System.out.println();
    }
    /**
     * This method determine if the position that being selected is a valid selection
     * 
     * - If the position being selected is out of the board, it is invalid. i.e., it must be a valid 
     * coordinate with row between 0 to 9, and column between 'A' to 'Z'.
     * - If the position being selected does not contain a block, it is invalid.
     * - If the block on the selected position does not have the same type of block in its immediate 
     * up/down/left/right position, it is invalid. That means, this is an isolated block that cannot
     * be cancelled with another block.
     * - Otherwise, it is a valid selection.
     * 
     *
     * @param gameboard is a 2D char array that is always non-null and pointed to a
     *                   rectangular area.
     * @param row is the row of the selected block, which should be between 0 and 9.
     * @param column is the column of the selected block, which should be between 'A' and 'Z'.
     * @return The method should return true if it is a valid selection, false if it is not.
     *
     */
    boolean isValidSelection(char[][] gameboard, int row, char column) {
        //local variable to re convert the column input into an int for the index
        // example 'A' - 'A', gives the index 0 and so on
        int col = column - 'A';

        //check if both row and column position is within board
        if(row < 0 || row >= gameboard.length || col < 0 || col >= gameboard[0].length)
            return false;

        // check if the position contains a block
        if(gameboard[row][col] == EMPTY)
            return false;

        //Check if any adjacent(up, down, left, right) positions have the same symbol of the position

        //Check up: first simply check if it's below at least col then simply check the symbol above
        if(row > 0 && gameboard[row][col] == gameboard[row - 1][col] )
            return true;

        // Check down: first check if position is above at least one row, then check the symbol below.
        if(row < gameboard.length - 1 && gameboard[row][col] == gameboard[row+1][col])
            return true;

        // Check left
        if (col > 0 && gameboard[row][col] == gameboard[row][col-1] )
            return true;

        //Check right
        if (col < gameboard[0].length -1  && gameboard[row][col] == gameboard[row][col+1])
            return true;

        return false; // if none of the above conditions are me, simply return false.

    }

    /**
     * This method change the gameboard by turning the selected blocks to the symbol SELECT and 
     * returns the number of blocks that are selected.
     * 
     * We assume that the selected position is valid when we call this method. (Valid, please refer to 
     * the description of is ValidSelection). This method will create a new 2D char array that is the
     * same size as the parameter gameboard. It turns the selected blocks and its adjacent blocks that 
     * share the same type to the symbol SELECT. 
     * 
     * You are expected to implement this method without using recursion.
     * 
     * 
     * @param gameboard the input gameboard
     * @param row the row of the selected block
     * @param column the column of the selected block
     * 
     * @return the number of blocks that are selected, which is the number of blocks that are turned to SELECTED.
     */
    int select(char[][] gameboard, int row, char column) {
        int col = column - 'A';
        char targetSymbol = gameboard[row][col];
        int count = 0;

        // We need arrays to act as a queue
        int maxSize = gameboard.length * gameboard[0].length;
        int[] queueRow = new int[maxSize];
        int[] queueCol = new int[maxSize];
        int front = 0, rear = 0;

        // Track visited positions
        boolean[][] visited = new boolean[gameboard.length][gameboard[0].length];

        // Start with initial position
        queueRow[rear] = row;
        queueCol[rear] = col;
        rear++;
        visited[row][col] = true;

        // Process the queue
        while (front < rear) {
            // Get next position from queue
            int currentRow = queueRow[front];
            int currentCol = queueCol[front];
            front++;

            // Mark this position as SELECTED
            gameboard[currentRow][currentCol] = SELECTED;
            count++;

            // Check all 4 directions
            // Check UP
            if (currentRow > 0 && !visited[currentRow-1][currentCol] &&
                    gameboard[currentRow-1][currentCol] == targetSymbol) {
                queueRow[rear] = currentRow - 1;
                queueCol[rear] = currentCol;
                rear++;
                visited[currentRow-1][currentCol] = true;
            }

            // Check DOWN
            if (currentRow < gameboard.length-1 && !visited[currentRow+1][currentCol] &&
                    gameboard[currentRow+1][currentCol] == targetSymbol) {
                queueRow[rear] = currentRow + 1;
                queueCol[rear] = currentCol;
                rear++;
                visited[currentRow+1][currentCol] = true;
            }

            // Check LEFT
            if (currentCol > 0 && !visited[currentRow][currentCol-1] &&
                    gameboard[currentRow][currentCol-1] == targetSymbol) {
                queueRow[rear] = currentRow;
                queueCol[rear] = currentCol - 1;
                rear++;
                visited[currentRow][currentCol-1] = true;
            }

            // Check RIGHT
            if (currentCol < gameboard[0].length-1 && !visited[currentRow][currentCol+1] &&
                    gameboard[currentRow][currentCol+1] == targetSymbol) {
                queueRow[rear] = currentRow;
                queueCol[rear] = currentCol + 1;
                rear++;
                visited[currentRow][currentCol+1] = true;
            }
        }

        return count;
    }
    /**
     * This method is to remove the selected blocks from the gameboard. Please refer to the rule
     * of the game on how blocks are removed and the subsequent shrinking of the gameboard, if any.
     * 
     * After the method, the content of the original gameboard (pointed by the parameter) is 
     * not important anymore. You are free to modify the content of the original gameboard.
     * 
     * @param gameboard
     * @return the new gameboard after removing the selected blocks.
     */
    char[][] removeSelected(char[][] gameboard) {

        //Define the SElECTED elements as EMPTY
        for (int i = 0; i < gameboard.length; i++) {
            for (int j = 0; j < gameboard[i].length; j++) {
                if (gameboard[i][j] == SELECTED) {
                    gameboard[i][j] = EMPTY;
                }
            }
        }
        // Shift blocks up within each column
        // When blocks are empty, blocks below should move up to fill empty spaces
        for (int column = 0; column < gameboard[0].length; column++) {
            // We'll use a "bubble up" approach - move non-empty blocks up
            for (int row = 0; row < gameboard.length - 1; row++) {
                if (gameboard[row][column] == EMPTY) {
                    // Found an empty space, look for the next non-empty block below to move up
                    for (int searchRow = row + 1; searchRow < gameboard.length; searchRow++) {
                        if (gameboard[searchRow][column] != EMPTY) {
                            // Move this block up to fill the empty space
                            gameboard[row][column] = gameboard[searchRow][column];
                            gameboard[searchRow][column] = EMPTY;
                            break;
                        }
                    }
                }
            }
        }
        // Check for completely empty columns
        boolean [] emptyCols = new boolean[gameboard[0].length];
        int emptyColumns = 0;

        // Identify the columns that are EMPTY
        for(int column = 0; column < gameboard[0].length; column++){
            boolean isEmpty = true;
            for(int row = 0; row < gameboard.length; row++){
                if(gameboard[row][column] != EMPTY){
                    isEmpty = false;
                    break;
                }
            }
            emptyCols[column] = isEmpty;
            if(isEmpty){
                emptyColumns ++;
            }

        }
        // If there are empty columns, shift columns left
        if(emptyColumns > 0){
            // Calculate new width (current width minus empty columns)
            int newWidth = gameboard[0].length - emptyColumns;
            // Create new board with REDUCED width
            char[][] newGameboard = new char[gameboard.length][newWidth];

            //Copy the non-empty elements to the left
            for(int row = 0; row < gameboard.length; row++){
                int newColumn = 0;
                for(int column = 0; column < gameboard[0].length; column++){
                    if(!emptyCols[column]){
                        newGameboard[row][newColumn] = gameboard[row][column];
                        newColumn++;
                    }
                }

            }
            return newGameboard;
        }


        return gameboard;

    }

        /**
         * This method is to check if the game is over.
         *
         * The game is over when there is no valid selection left on the gameboard.
         *
         * @param gameboard
         * @return true if the game is over, false otherwise.
         */
        boolean isGameOver ( char[][] gameboard){
            // Check every position on the board
            for (int i = 0; i < gameboard.length; i++) {
                for (int j = 0; j < gameboard[i].length; j++) {
                    // Convert column index to letter (0='A', 1='B', etc.)
                    char column = (char) ('A' + j);

                    // If this position has a valid selection, game is NOT over
                    if (isValidSelection(gameboard, i, column)) {
                        return false;  // Found at least one valid move
                    }
                }
            }
            // If we checked all positions and found no valid selections, game is over
            return true;
        }
    /**
     * This method is to randomize the gameboard with the symbols defined in SYMBOLS.
     * 
     * The method will fill the gameboard with random symbols from SYMBOLS except for the EMPTY symbol.
     * @param gameboard is a 2D char array that is always non-null and pointed to a
     *                   rectangular area.
     */
    void randomizeBoard(char[][] gameboard) {
        //TODO
        Random random = new Random();
        int rdx = 1;
        for(int i =0; i < gameboard.length; i++){
            for(int j = 0; j< gameboard[i].length; j++){
                rdx = random.nextInt(4) + 1; // make sure not 0.
            gameboard[i][j] = SYMBOLS[rdx];
             }

        }

    }
}