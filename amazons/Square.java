package amazons;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import static amazons.Piece.*;

import static amazons.Utils.*;

/** Represents a position on an Amazons board. Positions are numbered
 *  from 0 (lower-left corner) to 99 (upper-right corner).  Squares
 *  are immutable and unique: there is precisely one square created for
 *  each distinct position.  Clients create squares using the factory method
 *  sq, not the constructor.  Because there is a unique Square object for each
 *  position, you can freely use the cheap == operator (rather than the
 *  .equals method) to compare Squares, and the program does not waste time
 *  creating the same square over and over again.
 *  @author Netra Sathe
 */
final class Square {

    /** The regular expression for a square designation (e.g.,
     *  a3). For convenience, it is in parentheses to make it a
     *  group.  This subpattern is intended to be incorporated into
     *  other pattern that contain square designations (such as
     *  patterns for moves). */
    static final String SQ = "([a-j](?:[1-9]|10))";

    /** Return my row position, where 0 is the bottom row. */
    int row() {
        return _row;
    }

    /** Return my column position, where 0 is the leftmost column. */
    int col() {
        return _col;
    }

    /** Return my index position (0-99).  0 represents square a1, and 99
     *  is square j10. */
    int index() {
        return _index;
    }

    /** Return true iff THIS - TO is a valid queen move.
     * is Independent of obstables, only checks the direction,
     * none of the contents along the way */
    boolean isQueenMove(Square to) {
        if (to == null) {
            return false;
        }
        int deltaColumns = this._col - to._col;
        int deltaRows = this._row - to._row;
        if (!exists(to.col(), to.row())) {
            return false;
        }
        if (deltaColumns == 0 && deltaRows == 0) {
            return false;
        }
        boolean verticalMove = (deltaColumns == 0) && (deltaRows != 0);
        boolean horizontalMove = (deltaColumns != 0) && (deltaRows == 0);

        boolean diagonalMove = (Math.abs(deltaColumns) == Math.abs(deltaRows));

        return verticalMove || horizontalMove || diagonalMove;
    }

    /** Definitions of direction for queenMove.  DIR[k] = (dcol, drow)
     *  means that to going one step from (col, row) in direction k,
     *  brings us to (col + dcol, row + drow). */
    private static final int[][] DIR = {
            { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 },
            { 0, -1 }, { -1, -1 }, { -1, 0 }, { -1, 1 }
    };

    /** Return the Square that is STEPS>0 squares away from me in direction
     *  DIR, or null if there is no such square.
     *  DIR = 0 for north, 1 for northeast, 2 for east, etc., up to 7 for west.
     *  If DIR has another value, return null. Thus, unless the result
     *  is null the resulting square is a queen move away rom me. */
    Square queenMove(int dir, int steps) {
        if (dir < 0 || dir > 7) {
            return null;
        }
        int col = this._col;
        int row = this._row;
        if (dir == 0) {
            col += steps;
        } else if (dir == 1) {
            col += steps;
            row += steps;
        } else if (dir == 2) {
            row += steps;
        } else if (dir == 3) {
            col -= steps;
            row += steps;
        } else if (dir == 4) {
            col -= steps;
        } else if (dir == 5) {
            col -= steps;
            row -= steps;
        } else if (dir == 6) {
            row -= steps;
        } else {
            col += steps;
            row -= steps;
        }
        if (exists(col, row)) {
            return Square.sq(col, row);
        }
        return null;
    }

    /** Return the direction (an int as defined in the documentation
     *  for queenMove) of the queen move THIS-TO. */
    int direction(Square to) {
        assert isQueenMove(to);
        int deltaColumns = to._col - this._col;
        int deltaRows = to._row - this._row;
        if (deltaColumns > 0) {
            if (deltaRows < 0) {
                return 7;
            }
            if (deltaRows == 0) {
                return 0;
            }
            return 1;
        } else if (deltaColumns == 0) {
            if (deltaRows > 0) {
                return 2;
            }
            return 6;
        } else {
            if (deltaRows < 0) {
                return 5;
            } else if (deltaRows == 0) {
                return 4;
            }
            return 3;
        }
    }

    @Override
    public String toString() {
        return _str;
    }

    /** Return true iff COL ROW is a legal square. */
    static boolean exists(int col, int row) {
        return row >= 0 && col >= 0 && row < Board.SIZE && col < Board.SIZE;
    }

    /** Return the (unique) Square denoting COL ROW. */
    static Square sq(int col, int row) {
        if (!exists(row, col)) {
            throw error("row or column out of bounds");
        }
        return SQUARES[(row * 10) + col];
    }

    /** Return the (unique) Square denoting the position with index INDEX. */
    static Square sq(int index) {
        return SQUARES[index];
    }

    /** Return the (unique) Square denoting the position COL ROW, where
     *  COL ROW is the standard text format for a square (e.g., a4). */
    static Square sq(String col, String row) {
        int colAsInt = getIntFromColumn(col);
        int rowAsInt = Integer.parseInt(row) - 1;
        return Square.sq(colAsInt, rowAsInt);
    }

    /** Return the (unique) Square denoting the position in POSN, in the
     *  standard text format for a square (e.g. a4). POSN must be a
     *  valid square designation. */
    static Square sq(String posn) {
        assert posn.matches(SQ);
        String col = String.valueOf(posn.charAt(0));
        String row = posn.substring(1);
        return Square.sq(col, row);
    }

    /** Return an iterator over all Squares. */
    static Iterator<Square> iterator() {
        return SQUARE_LIST.iterator();
    }

    /** Return the Square with index INDEX. */
    private Square(int index) {
        _index = index;
        _row = index / 10;
        _col = index % 10;
        StringBuilder sb = new StringBuilder();
        sb.append(getColumn(_col));
        sb.append(getRow(_row));
        _str = sb.toString();
    }

    /**
     * Gets the String corresponding to the col.
     * @param col col
     * @return
     */
    private static String getColumn(int col) {
        if (col == 0) {
            return "a";
        } else if (col == 1) {
            return "b";
        } else if (col == 2) {
            return "c";
        } else if (col == 3) {
            return "d";
        } else if (col == 4) {
            return "e";
        } else if (col == 5) {
            return "f";
        } else if (col == 6) {
            return "g";
        } else if (col == 7) {
            return "h";
        } else if (col == 8) {
            return  "i";
        }
        return "j";
    }

    /**
     * Gets the int corresponding to the col.
     * @param col col
     * @return
     */
    private static int getIntFromColumn(String col) {
        if (col.equals("a")) {
            return 0;
        } else if (col.equals("b")) {
            return 1;
        } else if (col.equals("c")) {
            return 2;
        } else if (col.equals("d")) {
            return 3;
        } else if (col.equals("e")) {
            return 4;
        } else if (col.equals("f")) {
            return 5;
        } else if (col.equals("g")) {
            return 6;
        } else if (col.equals("h")) {
            return 7;
        } else if (col.equals("i")) {
            return 8;
        }
        return 9;
    }

    /**
     * Gets the String corresponding to the row.
     * @param row row
     * @return
     */
    private static String getRow(int row) {
        return Integer.toString(row + 1);
    }

    /** The cache of all created squares, by index. */
    private static final Square[] SQUARES =
            new Square[Board.SIZE * Board.SIZE];

    /** SQUARES viewed as a List. */
    private static final List<Square> SQUARE_LIST = Arrays.asList(SQUARES);

    static {
        for (int i = Board.SIZE * Board.SIZE - 1; i >= 0; i -= 1) {
            SQUARES[i] = new Square(i);
        }
    }

    /** My index position. */
    private final int _index;

    /** My row and column (redundant, since these are determined by _index). */
    private final int _row, _col;

    /** My String denotation. */
    private final String _str;
}
