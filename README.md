# SameGame Java Implementation

This repository contains a Java implementation of the classic puzzle game **SameGame** (also known as Chain Shot or Bubble Popper in variations). The game involves a grid of colored blocks (represented by symbols like `@`, `=`, `^`, `+`) where players select and remove groups of adjacent blocks of the same type to score points. As blocks are removed, the grid collapses vertically and horizontally, potentially creating new opportunities for moves. The game ends when no more valid moves are possible.

This project was developed as a programming assignment to practice concepts such as 2D array manipulation, BFS for connected component detection, file I/O for high scores, and game logic implementation.

## Features
- **Randomized Board Generation**: Fills a 10x26 grid with random symbols (excluding empty spaces initially).
- **Move Validation and Selection**: Uses BFS to identify and mark connected groups of 2+ identical symbols. Invalid moves are handled gracefully.
- **Block Removal and Board Collapse**: After removal, blocks shift up to fill gaps, and empty columns are removed, shrinking the board leftward.
- **Scoring System**: Base score is `n * (n + 1)` for `n` removed blocks, plus 10 bonus points per fully emptied column.
- **Commands and Tips**:
  - Select moves in format `A-5` (column-row).
  - `h`: Help menu.
  - `q`: Quit.
  - `r`: Restart.
  - `t`: Auto-select the largest possible group as a tip.
- **High Score Tracking**: Saves top 5 scores to `top_scores.txt` with player names (1-3 uppercase letters). Displays leaderboard at game end.
- **Game Over Detection**: Checks if no valid moves remain.

## How to Run
1. Ensure you have Java JDK installed (version 8+ recommended).
2. Compile the code: `javac SameGame.java`.
3. Run the game: `java SameGame`.
4. Follow on-screen prompts to play.

## Game Rules
- Select a block by entering its coordinates (e.g., `A-5`).
- Only groups of 2+ adjacent (up/down/left/right) identical symbols can be removed.
- After confirmation, removed blocks score points, and the board collapses.
- Aim for the highest score by strategically clearing the board.
- The board starts at 10 rows x 26 columns but can shrink as columns empty.

## Limitations and Notes
- The game uses console input/output (no GUI).
- Symbols are limited to those defined in `SYMBOLS` array (configurable for testing).
- High scores are stored in a plain text file in the current directory.
- This implementation avoids recursion for selection (uses iterative BFS for efficiency).

For questions or improvements, feel free to open an issue!
