/**
 * 
 */
package algo.knight;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ys
 *
 *	Knight object
 *	- provides the probability matrix for a knight at position (x,y) in mxm-board
 */
public class Knight {
	private static final List<int[]> _knightMoves;
	static {
		_knightMoves = new ArrayList<int[]>(8);		// knight can move 8 different positions max
		_knightMoves.add(new int[] {-1,-2});		// up -> upper left
		_knightMoves.add(new int[] {+1,-2});		// up -> upper right
		_knightMoves.add(new int[] {-2,-1});		// left -> upper left
		_knightMoves.add(new int[] {-2,+1});		// left -> lower left
		_knightMoves.add(new int[] {-1,+2});		// down -> lower left
		_knightMoves.add(new int[] {+1,+2});		// down -> lower right
		_knightMoves.add(new int[] {+2,-1});		// right -> upper right
		_knightMoves.add(new int[] {+2,+1});		// right -> lower right
	}
	
	private final int _x;		// x-coordinate of the knight
	private final int _y;		// y-coordinate of the knight
	private final int _m;		// size of the board; mxm
	
	private double[][]	_prob = null;	// probability transition matrix
	
	/**
	 * Constructor. Takes in the board size and the position of the knight
	 * @param m		board size
	 * @param x		x-coordinate
	 * @param y		y-coordinate
	 * @throws IllegalArgumentException
	 */
	public Knight(final int m, final int x, final int y)	throws IllegalArgumentException	{
		_m = m;
		_x = x;
		_y = y;
		if (m<3 || x<0 || x>=m || y<0 || y>=m) {
			throw new IllegalArgumentException("Invalid board size and/or xy-coordinate");
		}
	}
	
	public double[][] getProbabilityMatrix()	{
		if (_prob==null) {
			// lazy initialization
			// computes the probability transition matrix from the position (x,y)
			_prob = new double[_m][_m];
			for(int i=0;i<_m;++i) {
				for(int j=0;j<_m;++j) {
					_prob[i][j] = 0;	// initialize
				}
			}
			int totalMove = 0;
			for(int[] move : _knightMoves) {
				final int x_new = _x + move[0];
				final int y_new = _y + move[1];
				if (x_new>=0 && x_new<_m && y_new>=0 && y_new<_m) {	// valid move
					++totalMove;
					_prob[x_new][y_new] = 1;
				}
			}
			if (totalMove>1) {
				for(int i=0;i<_m;++i) {
					for(int j=0;j<_m;++j) {
						if (_prob[i][j]!=0) {
							_prob[i][j] /= totalMove;
						}
					}
				}
			}
		}
		return _prob;
	}
}
