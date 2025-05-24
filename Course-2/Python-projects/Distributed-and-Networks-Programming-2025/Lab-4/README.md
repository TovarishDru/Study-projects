# Lab 4 - gRPC

> Distributed and Networking Programming – Spring 2025

## Overview

In this lab, you will implement a **multiplayer Connect Four** game using **gRPC**.

### 1. Create `game.proto`

Write the game.proto file describing the structure of the messages and services used in the game. The client interacts with the server using this protocol definition.

### 2. Implement `server.py`

Your server must:

- Accept a port number via CLI argument (e.g., `python server.py 50051`).
- Listen on `0.0.0.0:<port>` and register the gRPC servicer.
- Implement full game logic:
  - Validate moves.
  - Track game state.
  - Determine the winner or a draw.
- Log all received requests in the console (including errors).

## Implement `client.py`

The client handles all user interactions and calls the remote methods from the server using gRPC.

It supports:

- Creating a new game.
- Connecting to an existing game.
- Playing interactively or in an automated mode (`--automate`).
- Choosing a player mark (`--player R|Y`).
- Connecting to a specific game by ID (`--game_id`).

### Error Handling Requirements

- `GetGame`: `NOT_FOUND` if game doesn’t exist.
- `MakeMove`:  
  - `NOT_FOUND`: game not found.  
  - `INVALID_ARGUMENT`: invalid column.  
  - `FAILED_PRECONDITION`: game is finished.  
  - `FAILED_PRECONDITION`: not player’s turn.  
  - `FAILED_PRECONDITION`: column is full.