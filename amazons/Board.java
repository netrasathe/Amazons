package amazons;
import javax.print.attribute.AttributeSetUtilities;
import java.util.Stack;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;

import static amazons.Piece.*;
import static amazons.Move.mv;


/** The state of an Amazons Game.
 *  @author Netra Sathe
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 10;
    /** Board representation of the Amazons game. */
    protected Piece[][] amazonboard;
    /**The stack that stores all the moves.*/
    protected Stack<Move> stacc;
    /** Integer that tells you the number of moves. */
    protected int movez;
    /** Last index of Squares array.*/
    private static final int END = 99;


    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        if (model == this) {
            return;
        } else {
            init();

            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    this.amazonboard[i][j] = model.amazonboard[i][j];
                }
            }
            this._turn = model._turn;
            this._winner = model._winner;
            this.movez = model.movez;
            this.stacc = model.stacc;
        }
    }

    /** Clears the board to the initial position. */
    void init() {
        _turn = WHITE;
        _winner = EMPTY;
        movez = 0;
        stacc = new Stack<>();
        amazonboard = new Piece[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                amazonboard[i][j] = EMPTY;
            }
        }

        amazonboard[6][0] = BLACK;
        amazonboard[9][3] = BLACK;
        amazonboard[9][6] = BLACK;
        amazonboard[6][9] = BLACK;
        amazonboard[0][3] = WHITE;
        amazonboard[0][6] = WHITE;
        amazonboard[3][0] = WHITE;
        amazonboard[3][9] = WHITE;
    }

    /** Return the Piece whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the number of moves (that have not been undone) for this
     *  board. */
    int numMoves() {
        return this.movez;
    }

    /** Return the winner in the current position, or null if the game is
     *  not yet finished. */
    Piece winner() {
        if (!legalMoves(_turn).hasNext()) {
            return _turn.opponent();
        } else {
            return null;
        }
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        return this.amazonboard[row][col];
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        put(p, s.col(), s.row());
    }

    /** Set square (COL, ROW) to P. */
    final void put(Piece p, int col, int row) {
        amazonboard[row][col] = p;
        _winner = winner();
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, col - 'a', row - '1');
    }

    /** Return true iff FROM - TO is an unblocked queen move on the current
     *  board, ignoring the contents of ASEMPTY, if it is encountered.
     *  For this to be true, FROM-TO must be a queen move and the
     *  squares along it, other than FROM and ASEMPTY, must be
     *  empty. ASEMPTY may be null, in which case it has no effect. */
    boolean isUnblockedMove(Square from, Square to, Square asEmpty) {
        if (!from.isQueenMove(to)) {
            return false;
        } else {
            int direction = from.direction(to);
            Square once = from;
            while (once != to) {
                once = once.queenMove(direction, 1);
                if (this.get(once) != EMPTY) {
                    if (once == asEmpty) {
                        continue;
                    } else {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        if (from.index() < 0 || from.index() > END) {
            return false;
        } else {
            return (amazonboard[from.row()][from.col()] == this._turn);
        }
    }

    /** Return true iff FROM-TO is a valid first part of move, ignoring
     *  spear throwing. */
    boolean isLegal(Square from, Square to) {
        if (isLegal(from)) {
            if (isUnblockedMove(from, to, null)) {
                return true;
            }
        }
        return false;
    }

    /** Return true iff FROM-TO(SPEAR) is a legal move in the current
     *  position. */
    boolean isLegal(Square from, Square to, Square spear) {
        if (isLegal(from, to)) {
            if (isUnblockedMove(to, spear, from)) {
                return true;
            }
        }
        return false;
    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return move != null
                && isLegal(move.from(), move.to(), move.spear());
    }

    /** This will switch the turn. */
    void change() {
        if (turn() == WHITE) {
            this._turn = BLACK;
        } else {
            this._turn = WHITE;
        }
    }


    /** Move FROM-TO(SPEAR), assuming this is a legal move. */
    void makeMove(Square from, Square to, Square spear) {
        if (isLegal(from, to, spear)) {
            Piece mooove = amazonboard[from.row()][from.col()];
            put(mooove, to);
            put(EMPTY, from);
            put(SPEAR, spear);
            Move stax = mv(from, to, spear);
            this.stacc.push(stax);
            change();
            this.movez++;

        }
    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        if (isLegal(move)) {
            makeMove(move.from(), move.to(), move.spear());
        }
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        Move fix = this.stacc.pop();
        Square spearfix = fix.spear();
        Square queenfix = fix.to();
        Square fixall = fix.from();

        change();
        put(EMPTY, spearfix);
        put(get(queenfix), fixall);
        put(EMPTY, queenfix);
        this.movez -= 1;
    }

    /** Return an Iterator over the Squares that are reachable by an
     *  unblocked queen move from FROM. Does not pay attention to what
     *  piece (if any) is on FROM, nor to whether the game is finished.
     *  Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     *  feature is useful when looking for Moves, because after moving a
     *  piece, one wants to treat the Square it came from as empty for
     *  purposes of spear throwing.) */
    Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /** Return an Iterator over all legal moves on the current board. */
    Iterator<Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /** Return an Iterator over all legal moves on the current board for
     *  SIDE (regardless of whose turn it is). */
    Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    /** An iterator used by reachableFrom. */
    private class ReachableFromIterator implements Iterator<Square> {

        /** Iterator of all squares reachable by queen move from FROM,
         *  treating ASEMPTY as empty. */
        ReachableFromIterator(Square from, Square asEmpty) {
            _from = from;
            _dir = 0;
            _steps = 0;
            _asEmpty = asEmpty;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _dir < 8;
        }

        @Override
        public Square next() {
            Square nexttt = this._from.queenMove(_dir, _steps);
            toNext();
            return nexttt;
        }

        /** Advance _dir and _steps, so that the next valid Square is
         *  _steps steps in direction _dir from _from. */
        private void toNext() {
            if (!isUnblockedMove(this._from, this._from.queenMove(_dir,
                    _steps + 1), _asEmpty)) {
                _steps = 1;
                _dir += 1;
                while (!isUnblockedMove(this._from, this._from.queenMove(_dir,
                        _steps), _asEmpty) && _dir < 8) {
                    _dir += 1;
                }
            } else {
                _steps += 1;
            }
        }

        /** Starting square. */
        private Square _from;
        /** Current direction. */
        private int _dir;
        /** Current distance. */
        private int _steps;
        /** Square treated as empty. */
        private Square _asEmpty;
    }

    /** An iterator used by legalMoves. */
    /** An iterator used by legalMoves. */
    private class LegalMoveIterator implements Iterator<Move> {

        /** All legal moves for SIDE (WHITE or BLACK). */
        LegalMoveIterator(Piece side) {
            _startingSquares = Square.iterator();
            _spearThrows = NO_SQUARES;
            _pieceMoves = NO_SQUARES;
            _fromPiece = side;
            queenz = 0;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _startingSquares.hasNext();
        }

        @Override
        public Move next() {
            Move nextagain = Move.mv(_start, _nextSquare, spearz);
            toNext();
            return nextagain;
        }

        /** Advance so that the next valid Move is
         *  _start-_nextSquare(sp), where sp is the next value of
         *  _spearThrows. */
        private void toNext() {
            if (!_spearThrows.hasNext()) {
                if (!_pieceMoves.hasNext()) {
                    while (_startingSquares.hasNext()) {
                        Square netrasquare = _startingSquares.next();
                        if (amazonboard[netrasquare.row()]
                                [netrasquare.col()] == _fromPiece) {
                            _start = netrasquare;
                            _pieceMoves = reachableFrom
                                    (_start, null);
                            _nextSquare = _pieceMoves.next();
                            if (_nextSquare == null) {
                                continue;
                            }
                            _spearThrows = reachableFrom(_nextSquare,
                                    _start);
                            spearz = _spearThrows.next();
                            break;
                        }
                    }
                } else {
                    _nextSquare = _pieceMoves.next();
                    _spearThrows = reachableFrom(_nextSquare, _start);
                    spearz = _spearThrows.next();
                }
            } else {
                spearz = _spearThrows.next();
            }
        }



        /**The number of side pieces that we have returned legal moves for. */
        private int queenz;
        /** The current square which the spear is thrown to.*/
        private Square spearz;
        /** The colour of the side we are iterating. */
        private Piece _fromPiece;
        /** Current starting square. */
        private Square _start;
        /** Remaining starting squares to consider. */
        private Iterator<Square> _startingSquares;
        /** Current piece's new position. */
        private Square _nextSquare;
        /** Remaining moves from _start to consider. */
        private Iterator<Square> _pieceMoves;
        /** Remaining spear throws from _piece to consider. */
        private Iterator<Square> _spearThrows;
    }


    @Override
    public String toString() {
        StringBuilder stringg = new StringBuilder();
        for (int i = 9; i >= 0; i--) {
            stringg.append("   ");
            for (int j = 0; j < 9; j++) {
                stringg.append(amazonboard[i][j] + " ");
            }
            stringg.append(amazonboard[i][9]);
            stringg.append("\n");
        }
        return stringg.toString();
    }

    /** An empty iterator for initialization. */
    private static final Iterator<Square> NO_SQUARES =
            Collections.emptyIterator();

    /** Piece whose turn it is (BLACK or WHITE). */
    private Piece _turn;
    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;
}
