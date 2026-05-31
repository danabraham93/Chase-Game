# 2D Java Chase Game

A real-time 2D game built in Java using Swing, featuring player movement, enemy AI behavior, power-ups, and a multi-level progression system.

---

## Features

- Player-controlled character (circle)
- Enemy AI that actively chases the player
- Collision detection for player and enemies
- Power-ups:
  - Speed boost
  - Enemy size modifier
- Timer-based level system (survive 30 seconds per level)
- Multi-level progression (increasing difficulty)
- Game states:
  - Main menu
  - Gameplay
  - Credits screen
  - Game over screen
  - Victory screen

---

## Technologies Used

- Java
- Swing (JPanel, JFrame)
- Java AWT (Graphics, KeyListener, ActionListener)
- Timer-based game loop
- Object-oriented programming

---

## How It Works

- A game loop updates every 20ms using a Swing Timer
- Player movement is controlled via arrow keys
- Enemies continuously move toward the player's position
- Collision with enemies ends the game
- Surviving for 30 seconds advances to the next level
- Power-ups spawn randomly and temporarily modify gameplay

---

## Key Concepts Demonstrated

- Game loop design (real-time updates)
- Event-driven programming
- Basic AI behavior (enemy pursuit logic)
- Collision detection using bounding rectangles
- State management (menu, gameplay, win/lose screens)
- Object-oriented design principles

---

## How to Run

1. Compile the program:
2. Run the program:

---

## Future Improvements

- Add scoring system
- Add sound effects and background music control
- Improve enemy AI behavior (pathfinding)
- Add levels with different maps
- Refactor into multiple classes for scalability
