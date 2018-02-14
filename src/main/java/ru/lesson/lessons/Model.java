package ru.lesson.lessons;

import java.util.*;

/**
 * Created by levin on 11.09.2017.
 */
public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile gameTiles[][];
    int score;
    int maxTile;

    private boolean isSaveNeeded = true;
    private Stack<Tile[][]> previousStates;
    private Stack<Integer> previousScores;

    public Model() {
        resetGameTiles();
        this.score = 0;
        this.maxTile = 2;
        this.previousStates = new Stack<Tile[][]>();
        this.previousScores = new Stack<Integer>();
    }

    private void addTile() {
        List<Tile> tileList = getEmptyTiles();
        if (tileList != null && tileList.size() !=0) {
            tileList.get((int) (tileList.size() * Math.random())).setValue((Math.random() < 0.9 ? 2 : 4));
        }
    }

    private List<Tile> getEmptyTiles() {
        List<Tile> emptyTiles = new ArrayList<Tile>();
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles.length; j++) {
                if (gameTiles[i][j].value == 0) {
                    emptyTiles.add(gameTiles[i][j]);
                }
            }
        }
        return emptyTiles;
    }

    protected void resetGameTiles() {
        this.gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles.length; j++) {
                this.gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    private boolean compressTiles(Tile[] tiles) {
        boolean isChanged = false;
        Tile temp;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tiles[j].getValue() == 0 && tiles[j + 1].getValue() != 0) {
                    temp = tiles[j];
                    tiles[j] = tiles[j + 1];
                    tiles[j + 1] = temp;
                    isChanged = true;
                }
            }
        }
        return isChanged;
    }

    private boolean mergeTiles(Tile[] tiles) {
        boolean isChanged = false;
        for (int i = 0; i < 3; i++) {
            if (tiles[i].getValue() != 0 && tiles[i].getValue() == tiles[i + 1].getValue()) {
                tiles[i].setValue(tiles[i].getValue() * 2);
                tiles[i + 1].setValue(0);
                if (tiles[i].getValue() > maxTile)
                    maxTile = tiles[i].getValue();
                score += tiles[i].getValue();
                isChanged = true;
            }
        }
        compressTiles(tiles);
        return isChanged;
    }

    public void left() {
        if (isSaveNeeded)
            saveState(gameTiles);

        boolean isChanged = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                isChanged = true;
            }
        }
        if (isChanged) {
            addTile();
        }
        isSaveNeeded = true;
    }

    public void right() {
        saveState(gameTiles);
        rotate();
        rotate();
        left();
        rotate();
        rotate();
    }

    public void down() {
        saveState(gameTiles);
        rotate();
        left();
        rotate();
        rotate();
        rotate();
    }

    public void up() {
        saveState(gameTiles);
        rotate();
        rotate();
        rotate();
        left();
        rotate();
    }

    private void rotate() {
        Tile tmp[][] = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                tmp[i][j] = gameTiles[FIELD_WIDTH - 1 - j][i];
            }
        }
        gameTiles = tmp;
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public boolean canMove() {
        if (!getEmptyTiles().isEmpty())
            return true;

        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 1; j < gameTiles.length; j++) {
                if (gameTiles[i][j].value == gameTiles[i][j - 1].value)
                    return true;
            }
        }

        for (int j = 0; j < gameTiles.length; j++) {
            for (int i = 1; i < gameTiles.length; i++) {
                if (gameTiles[i][j].value == gameTiles[i - 1][j].value)
                    return true;
            }
        }
        return false;
    }

    private void saveState(Tile[][] tiles) {
        Tile[][] tilesToSave = new Tile[tiles.length][tiles[0].length];
        int scoreToSave = score;

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                tilesToSave[i][j] = new Tile(tiles[i][j].getValue());
            }
        }

        previousStates.push(tilesToSave);
        previousScores.push(scoreToSave);
        isSaveNeeded = false;
    }

    public void rollback() {
        if (!previousScores.isEmpty() && !previousStates.isEmpty()) {
            this.gameTiles = previousStates.pop();
            this.score = previousScores.pop();
        }
    }

    public void randomMove() {
        int n = (int) (((Math.random() * 100)) % 4);
        switch (n) {
            case 0:
                left();
                break;
            case 1:
                right();
                break;
            case 2:
                up();
                break;
            case 3:
                down();
                break;
        }
    }

    public boolean hasBoardChanged() {
        Tile tmp[][] = previousStates.peek();
        int tmpSum = 0;
        int currentSum = 0;
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[0].length; j++) {
                tmpSum += tmp[i][j].getValue();
                currentSum += gameTiles[i][j].getValue();
            }
        }
        return currentSum != tmpSum;
    }

    public MoveEfficiency getMoveEfficiency(Move move) {
        MoveEfficiency moveEfficiency;
        move.move();
        if (hasBoardChanged())
            moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
        else
            moveEfficiency = new MoveEfficiency(-1, 0, move);
        rollback();
        return moveEfficiency;
    }

    public void autoMove() {
        PriorityQueue<MoveEfficiency> queue = new PriorityQueue(4, Collections.reverseOrder());
        queue.offer(getMoveEfficiency(new Move() {
//            @Override
            public void move() {
                left();
            }
        }));

        queue.offer(getMoveEfficiency(new Move() {
//            @Override
            public void move() {
                right();
            }
        }));

        queue.offer(getMoveEfficiency(new Move() {
//            @Override
            public void move() {
                up();
            }
        }));

        queue.offer(getMoveEfficiency(new Move() {
//            @Override
            public void move() {
                down();
            }
        }));

        queue.peek().getMove().move();
    }
}
