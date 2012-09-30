import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

/**
 * 
 * @author	Yon-Seo Kim
 * @date	March 23, 2012
 *
 */
public class HanoiSolver {
	private final SortedMap<Integer, Vector<Integer>> _pegsStart;
	private final SortedMap<Integer, Vector<Integer>> _pegsEnd;
	private final int _P;
	private final int _N;
	
	@SuppressWarnings("unchecked")
	public HanoiSolver(final int P, final int N) throws IllegalArgumentException {
		if (P<3 || N<P) { throw new IllegalArgumentException("OMG..."); }
		_P = P;
		_N = N;
		_pegsStart = new TreeMap<Integer, Vector<Integer>>();
		_pegsEnd = new TreeMap<Integer, Vector<Integer>>();
		for(int p=0;p<_P;++p) {
			Integer peg = Integer.valueOf(p);
			_pegsStart.put(peg, new Vector<Integer>());
			_pegsEnd.put(peg, new Vector<Integer>());
		}
	}
	
	public void printState(final SortedMap<Integer, Vector<Integer>> pegs) throws IllegalArgumentException {
		if (pegs.size()!=_P) { throw new IllegalArgumentException("Wrong size..."); }
		System.out.println("=======================");
		for(Integer peg : pegs.keySet()) {
			System.out.print(peg.toString()+"-th peg:");
			Iterator<Integer> iter = pegs.get(peg).iterator();
			while(iter.hasNext()) {
				System.out.print(" "+iter.next().toString());
			}
			System.out.println();
		}
		System.out.println("***********************");
	}
	
	public void insertDisc(final SortedMap<Integer, Vector<Integer>> pegs, Integer pegNum, Integer discNum) throws IllegalArgumentException {
		if (pegNum<0 || pegNum>=_P || discNum<=0 || pegs.size()!=this._P || pegs.containsKey(pegNum)==false) {
			throw new IllegalArgumentException("Invalid argument");
		}
		Vector<Integer> vec = pegs.get(pegNum);
		if (vec.size()==0 || discNum.compareTo(vec.get(0))>=0) {
			this.printState(pegs);
			throw new IllegalArgumentException("Invalid disc size");
		}
		vec.add(0, discNum);
	}
	public void inserDisc2Start(final int pegNum, final int discNum) throws IllegalArgumentException {
		this.insertDisc(this._pegsStart, Integer.valueOf(pegNum), Integer.valueOf(discNum));
	}
	public void inserDisc2End(final int pegNum, final int discNum) throws IllegalArgumentException {
		this.insertDisc(this._pegsEnd, Integer.valueOf(pegNum), Integer.valueOf(discNum));
	}
	
	public void solve() throws IllegalStateException {
		// integrity check
		
		for(int p=0;p<this._P;++p) {
			if (this._pegsStart[p].size()!=this._pegsEnd[p].size()) {
			}
		}
	}
}
