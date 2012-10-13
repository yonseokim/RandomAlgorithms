/**
   Generalized Hanoi Tower problem
   - Given P pegs, N discs, initial/final arrangements
   - find minimal sequence of moves that achieves initial->final arrangement

   I believe there is clean recursive algorithm to solve this,
   but cannot come up with one at the moment.
   Here, I resort to brute-force algorithm.
 */
import java.io.BufferedReader;
import java.io.PrintStream;
import java.io.InputStreamReader;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.HashSet;

import java.util.Map;
import java.util.HashMap;

import java.util.List;
import java.util.LinkedList;

import java.util.Queue;

public class hanoiBFS {

	// first test of reviewboard !! yay ~~
    public class pair {
	public final Integer from;
	public final Integer to;
	public pair(final Integer f, final Integer t) { from = f; to = t; }
    }

    public class peg implements Comparable<peg> {
	private final Integer _id;
	private final SortedSet<Integer> _data;
	public int compareTo(peg p) {
	    if (_data.size()!=p._data.size()) { return _data.size()>p._data.size() ? 1 : -1; }
	    for(Integer key : _data) { if (p._data.contains(key)==false) { return -1;} }
	    return 0;
	}
	public void print(PrintStream out) { for(Integer disc : _data) { out.print(disc+" "); } }
	public peg(final int id) { _id = Integer.valueOf(id); _data = new TreeSet<Integer>(); }
	public peg(final peg p) { // deep copy
	    _id = p._id; _data = new TreeSet<Integer>(p._data);	}
	public boolean addDisc(final int disc) {
	    Integer d = Integer.valueOf(disc);
	    if (_data.contains(d)) { return false; }
	    _data.add(d); return true; }
	public boolean addDisc(final Integer disc) {
	    if (_data.contains(disc)) { return false; }
	    _data.add(disc); return true;
	}
	public Integer getID() { return _id; }
	@Override
	public boolean equals(Object a) {
	    peg p = (peg)(a);
	    return this._data.size()==p._data.size() &&
		this._data.containsAll(p._data) && p._data.containsAll(this._data);
	}
	public boolean isEmpty() { return this._data.isEmpty(); }
	public Integer pop() throws IllegalStateException {
	    if (this._data.isEmpty()) { throw new IllegalStateException("Empty..."); }
	    Integer disc = this._data.first();
	    this._data.remove(disc);
	    return disc;
	}
	public Integer peek() { return this._data.first(); }
	public boolean push(Integer disc) throws IllegalStateException {
	    if (disc==null || this._data.contains(disc)) {
		throw new IllegalStateException("Already in the peg...");
	    }
	    if (!this._data.isEmpty() && this._data.first().compareTo(disc)<0) {
		System.out.println("Attempting to put "+disc+" on top of "+this._data.first());
		return false; }
	    this._data.add(disc);
	    return true;
	}
    }

    public class state implements Comparable<state> {
	private final int _P;
	private final Integer[] _keys;
	private final Map<Integer, peg> _data;
	public state(final int P) {
	    _P = P; _keys = new Integer[_P];
	    _data = new HashMap<Integer, peg>();
	    for(int j=1;j<=P;++j) { peg p = new peg(j);
		_data.put(p.getID(), p); _keys[j-1] = p.getID(); } }
	public state(final state a) {
	    // deep copy
	    _P = a._P; _keys = a._keys;
	    _data = new HashMap<Integer, peg>();
	    for(Integer key : a._data.keySet()) { _data.put(key, new peg(a._data.get(key))); } }
	
	public void add(Integer peg, Integer disc) throws IllegalArgumentException {
	    if (_data.containsKey(peg)==false) { throw new IllegalArgumentException("No such peg"); }
	    if (_data.get(peg).addDisc(disc)==false) { throw new IllegalArgumentException("Invalid disc..."); }
	}
	public Integer pop(final Integer from) { return _data.get(from).pop(); }
	@Override
	public boolean equals(Object s) {
	    state a = (state)(s);
	    for(Integer key : _data.keySet()) {
		if (this._data.get(key).equals(a._data.get(key))==false) { return false; } }
	    return true;
	}
	public int compareTo(state a) {
	    for(int j=0;j<_keys.length;++j) {
		peg pegA = _data.get(_keys[j]); peg pegB = a._data.get(_keys[j]);
		final int c = pegA.compareTo(pegB); if (c!=0) { return c; } }
	    return 0;
	}
	public Set<pair> getAllMoves() {
	    Set<pair> result = new HashSet<pair>();
	    for(int i=0;i<_P;++i) {
		Integer keyA = _keys[i]; peg pegA = _data.get(keyA); if (pegA.isEmpty()) { continue; }
		Integer discA = pegA.peek();
		for(int j=0;j<_P;++j) { if (i==j) { continue; }
		    Integer keyB = _keys[j]; peg pegB = _data.get(keyB);
		    if (pegB.isEmpty() || pegB.peek().compareTo(discA)>0) {
			result.add(new pair(keyA,keyB)); }
		} }
	    return result;
	}
	public state evolve(Integer from, Integer to) throws IllegalStateException {
	    // check first
	    if (_data.containsKey(from)==false || _data.containsKey(to)==false || _data.get(from).isEmpty()==true
		|| (_data.get(to).isEmpty()==false && _data.get(to).peek().compareTo(_data.get(from).peek())<=0)) {
		throw new IllegalStateException("Invalid move...");
	    }
	    state copy = new state(this); Integer disc = copy.pop(from);
	    copy._data.get(to).push(disc); return copy;
	}
	public void print(PrintStream out) {
	    for(Integer key : _data.keySet()) {
		out.print(key+" : "); _data.get(key).print(out); out.print("\n"); }
	}
    }
    private state initialState = null;
    private state finalState = null;
    private int bound = -1;
    private final Set<state> paths = new HashSet<state>();

    public hanoiBFS() throws Exception {
	String line=null;
	try {
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    System.out.println("N K"); line = br.readLine();
	    String[] nk = line.split(" "); int N = Integer.parseInt(nk[0]); int P = Integer.parseInt(nk[1]);
	    System.out.println("Got P="+P);
	    initialState = new state(P); finalState = new state(P);
	    getInputFromConsole("Input Initial State...", initialState, br);
	    System.out.println("Got initial state as follows;");
	    initialState.print(System.out);
	    getInputFromConsole("Input Final State...", finalState, br);
	    System.out.println("Got final state as follows;");
	    finalState.print(System.out);
	    System.out.println("Solution bound ?");
	    line = br.readLine();
	    try { final int b = Integer.parseInt(line);
		if (b>0) { bound = b; System.out.println("Bound set to "+bound);
		} else { System.out.println("Bound not set"); }
	    } catch(Exception e) { System.out.println("Bound not set"); }
	} catch(IllegalStateException e) { e.printStackTrace(System.out);
	} catch(Exception e) { throw e;	}
    }

    public void getInputFromConsole(String msg, state s, BufferedReader br) throws Exception {
	System.out.println(msg); String line = null; line = br.readLine();
	String[] token = line.split(" ");
	for(int j=0;j<token.length;++j) {
	    Integer disc = Integer.valueOf(j+1);
	    Integer peg = Integer.valueOf(token[j]);
	    s.add(peg,disc); }
	System.out.println("Done getting input.");
    }
    public static void main(String[] args) {
	try {
	    hanoiBFS h = new hanoiBFS();
	    path sols = h.solveBFS();
	} catch(Exception e) {
	    e.printStackTrace(System.out);
	}
    }
    /**
       main solver module
     */
    public class path {
	public final List<pair> moves;
	public final List<state> states;
	public path() {
	    moves = new LinkedList<pair>();
	    states = new LinkedList<state>(); }
	public path copy() {
	    path p = new path(); p.moves.addAll(moves);
	    states.addAll(states); return p; }
	public void print(PrintStream out) {
	    for(pair move : moves) { out.print(" : "+move.from+"->"+move.to); }
	    out.println(); }
    }
    private SortedSet<state> visited = null;
    private path solution = null;
    private Queue<path> workPath = null;
    public path solveBFS() throws Exception {
	workPath = new LinkedList<path>();
	visited = new TreeSet<state>();
	solution = null;

	visited.add(initialState);
	workPath.offer(new path());

	while(workPath.isEmpty()==false) {
	    path p = workPath.poll();
	    if (bound>0 && bound<p.moves.size()) { continue; }
	    if (solution!=null && solution.moves.size()<=p.moves.size()) { continue; }
	    state s = (p.states.size()>0) ? p.states.get(p.states.size()-1):initialState;
	    if (s.equals(finalState)) {
		System.out.println("Found better solution");
		p.print(System.out); solution = p.copy();
	    }
	    visited.add(s); Set<pair> moves = s.getAllMoves();
	    for(pair move : moves) {
		if (p.moves.size()>0) { pair lastMove = p.moves.get(p.moves.size()-1);
		    if (lastMove.from.equals(move.to) && lastMove.to.equals(move.from)) { continue; } }
		state next = s.evolve(move.from, move.to);
		if (visited.contains(next)==false) {
		    visited.add(next); path pc = p.copy();
		    pc.moves.add(move); pc.states.add(next);
		    workPath.offer(pc);
	} } }
	return solution;
    }
}
