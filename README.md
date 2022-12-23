# AI-Final-Project
COMP 424 - Project Specification:

Pentago-Swap:

Code due: Wednesday, April 10, 2019

Report due: Thursday, April 11, 2019

Goal:

The main goal of the project for this course is to give you a chance to play around with some of the AI
algorithms discussed in class, in the context of a fun, large-scale problem. This year we will be working on
a game called Pentago-Swap, a variation on a more popular game called Pentago. Matt Grenander is the
TA in charge of the project and should be the first contact about any bugs in the provided code. General
questions should be posted in the project section in MyCourses.

Rules:

Pentago-Swap falls into the Moku family of games. Other popular games in this category include Tic-Tac-
Toe and Connect-4, although Pentago-Swap has significantly more complexity. The biggest difference in
Pentago-Swap is that the board is divided into quadrants, which can be swapped around during the game.
Setup. Pentago-Swap is a two-player game played on a 6×6 board, which consists of four 3×3 quadrants.
To begin, the board is empty. The first player plays as white and the other plays as black.
Objective. In order to win, each player tries to achieve 5 pieces in a row before their opponent does. A
winning row can be achieved horizontally, vertically or diagonally. If all spaces on the board are occupied
without a winner then a draw is declared. If swapping two quadrants results in a five-in-a-row for both
players, the game also ends in a draw.
Playing. Moves consist of two phases: placing and swapping. On a given player’s turn, a piece is first
placed in an empty slot on the board. The player then selects two quadrants, which switch position. A
complete move therefore consists of placing a piece, then selecting two quadrants to swap.
Strategy. Allowing quadrants to be swapped introduces significant complexity and your AI agent
will need to contend with this high branching complexity. Since quadrants can be swapped, blocking an
opponent’s row is not as easy as simply placing an adjacent piece. A good AI agent might consider balancing
seeking to win with preventing their opponent from achieving the same.
We will hold a competition between all the programs submitted by students in the class, with every
submitted program playing one match against every other program. Each match will consist of 2 Pentago-
Swap games, giving both programs the opportunity to play first
