Tictactoe
=========

This is a Java implementation of a networked Tic-tac-toe. It's a bit different from regular Tic-tac-toe since after you've placed three markers you get to move one instead of filling the board in a draw. This leads to hours of continuous drawing instead. It basically becomes an endurance game instead, since it's now about who gives up first. How fun. It was created for the purpose of implementing a very simple self-written "protocol" in ABNF for a networking course (included below).

```
coordinate = %d1-9
place      = "place" SP coordinate
replace    = "replace" SP coordinate SP coordinate
```
