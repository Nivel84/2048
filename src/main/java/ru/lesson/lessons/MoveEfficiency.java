package ru.lesson.lessons;

/**
 * Created by levin on 13.09.2017.
 */
public class MoveEfficiency implements Comparable<MoveEfficiency> {
    private int numberOfEmptyTiles;
    private int score;
    private Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

//    @Override
    public int compareTo(MoveEfficiency moveEfficiency) {
        int result = 0;
        if (this.numberOfEmptyTiles > moveEfficiency.numberOfEmptyTiles)
            result = 1;
        if (this.numberOfEmptyTiles < moveEfficiency.numberOfEmptyTiles)
            result = -1;
        if (this.numberOfEmptyTiles == moveEfficiency.numberOfEmptyTiles) {
            if (this.score > moveEfficiency.score)
                result = 1;
            if (this.score < moveEfficiency.score)
                result = -1;
            if (this.score == moveEfficiency.score)
                result = 0;
        }
        return result;
    }
}
