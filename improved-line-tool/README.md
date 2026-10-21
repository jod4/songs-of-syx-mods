# Improved Line Tool

The line tool in the build menus (roads, walls, floors, ...) only draws lines in 45° and 90° angles.
This mod lets it draw straight lines in any angle, using [Bresenham's line algorithm](https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm).

* Drag with the line tool as usual, the line goes straight from the start tile to the mouse
* Hold **left ctrl** (the games MOD key) while dragging to get the vanilla line
* The line thickness (+ / - or ctrl + mouse wheel) works as before

The mod is active in new and existing saves as soon as it is enabled in the launcher.
There is no script to select when starting a new game. It doesn't store anything in save games, so it can be removed at any time.

Game version: V71

## How it works

The line tool is part of the games multi tile placement tool (`view.tool.PlacableMultiTool`).
`ImprovedLineMultiTool` wraps it and is swapped into the `ToolPlacer` of the settlement view, the world view, the world map editor and the world generator.
While the line tool is dragged, it replaces the painted area with the Bresenham line, everything else is passed through to the vanilla tool.

The class is in the games `view.tool` package to be able to access its package-private types.
If the game code changes in a way the mod can't handle, it logs an error and the vanilla line tool stays in place.
