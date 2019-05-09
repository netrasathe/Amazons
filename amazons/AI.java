package amazons;

import java.util.Iterator;

import static amazons.Piece.*;

/** A Player that automatically generates moves.
 *  @author Netra Sathe
 */

class AI extends Player {

    /**
     * Hardcoding value for ten. This is used in staticScore
     */
    private static final int TEN = 10;

    /**
     * A position magnitude indicating a win (for white if positive, black
     * if negative).
     */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /**
     * A magnitude greater than a normal value.
     */
    private static final int INFTY = Integer.MAX_VALUE;

    /**
     * A new AI with no piece or controller (intended to produce
     * a template).
     */
    AI() {
        this(null, null);
    }

    /**
     * A new AI playing PIECE under control of CONTROLLER.
     */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move move = findMove();
        _controller.reportMove(move);
        return move.toString();
    }

    /**
     * Return a move for me from the current position, assuming there
     * is a move.
     */
    private Move findMove() {
        Board b = new Board(board());
        if (_myPiece == WHITE) {
            findMove(b, maxDepth(b), true, 1, -INFTY, INFTY);
        } else {
            findMove(b, maxDepth(b), true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /**
     * The move found by the last call to one of the ...FindMove methods
     * below.
     */
    private Move _lastFoundMove;

    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _lastFoundMove iff SAVEMOVE. The move
     * should have maximal value or have value > BETA if SENSE==1,
     * and minimal value or value < ALPHA if SENSE==-1. Searches up to
     * DEPTH levels.  Searching at level 0 simply returns a static estimate
     * of the board value and does not set _lastMoveFound.
     */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        Iterator<Move> iterf = board.legalMoves();

        Move curr = null;
        int optimalv;
        if (sense == 1) {
            optimalv = -INFTY;
        } else {
            optimalv = INFTY;
        }

        if (sense == -1) {
            if (depth == 0 || board.winner() != null) {
                return staticScore(board);
            }
            while (iterf.hasNext()) {
                Move nexxt = iterf.next();
                board.makeMove(nexxt);
                int rxn = findMove(board, depth - 1, saveMove,
                        -sense, alpha, beta);

                board.undo();
                if (rxn <= optimalv) {
                    curr = nexxt;
                    optimalv = rxn;
                    beta = Math.min(beta, rxn);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
        } else if (sense == 1) {
            if (depth == 0 || board.winner() != null) {
                return staticScore(board);
            }
            while (iterf.hasNext()) {
                Move nexxt = iterf.next();
                board.makeMove(nexxt);
                int reacc = findMove(board, depth - 1, false,
                        -sense, alpha, beta);
                board.undo();
                if (reacc >= optimalv) {
                    curr = nexxt;
                    optimalv = reacc;
                    alpha = Math.max(alpha, reacc);

                    if (beta <= alpha) {
                        break;
                    }
                }
            }
        }
        if (saveMove) {
            _lastFoundMove = curr;
        }
        return optimalv;
    }


    /**
     * Return a heuristically determined maximum search depth
     * based on characteristics of BOARD.
     */
    private int maxDepth(Board board) {
        int n = board.numMoves();
        if (n < 2 * TEN) {
            return 1;
        } else if (n < 3 * TEN) {
            return 2;
        } else if (n < 4 * TEN) {
            return 3;
        } else if (n < 5 * TEN) {
            return 4;
        } else {
            return 5;
        }
    }


    /**
     * Return a heuristic value for BOARD.
     */

    private int staticScore(Board board) {
        Piece winner = board.winner();
        if (winner == WHITE) {
            return WINNING_VALUE;
        } else if (winner == BLACK) {
            return -WINNING_VALUE;
        } else {
            int white = 0;
            Iterator<Move> current = board.legalMoves();
            while (current.hasNext()) {
                current.next();
                white += 1;
            }

            return white;
        }
    }
}

