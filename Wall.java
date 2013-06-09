import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.BitSet;
import java.util.Comparator;

public class Wall {
	// does this give diffs ?
    private static final BitCompare _comparator = new BitCompare();
    
    private static class BitCompare implements Comparator<BitSet> {
	public int compare(BitSet a, BitSet b) {
	    final int sizeA = a.size();
	    final int sizeB = b.size();
	    if (sizeA!=sizeB) { return sizeA<sizeB ? -1:1; }
	    for(int j=0;j<sizeA;++j) {
		final boolean val = a.get(j);
		if (val!=b.get(j)) { return val ? -1:1; } }
	    return 0;
	}
    }

    private static final int[] _blocks = new int[] { 2, 3 };
    private BitSet[] _wall = null;
    private final int _width;
    private final int _height;
    private Map<Integer, Map<BitSet, Long>> _cache = null;
    private long _counter;

    public Wall(final int width, final int height) {
	_width = width;
	_height= height;
	_wall = new BitSet[_height+1];
	_cache = new HashMap<Integer, Map<BitSet, Long>>();
	for(int h=0;h<=_height;++h) {
	    _wall[h] = new BitSet(_width);
	    _cache.put(Integer.valueOf(h), new TreeMap<BitSet, Long>(_comparator) );
	}
    }

    public long count() {
	_counter = 0;
	for(int h=0;h<=_height;++h) {
	    _cache.get(Integer.valueOf(h)).clear();
	    BitSet row = _wall[h];
	    for(int j=0;j<_width;++j) { row.set(j,true); }
	}
	countRecursive(1, 0, _wall[0]);
	for(int h=0;h<=_height;++h) {
	    System.out.println("Height "+h+" has "+_cache.get(Integer.valueOf(h)).size()+" entries");
	}
	return _counter;
    }

    private void countRecursive(final int h, final int w, final BitSet previous) {
	if (h>_height) { _counter++; return; }
	BitSet current = _wall[h];
	for(int jump : _blocks) {
	    final int next = w+jump;
	    if (next>_width) { continue; }
	    if (next==_width) {
		final Integer key = Integer.valueOf(h);
		Map<BitSet, Long> cacheRow = _cache.get(key);
		if (cacheRow.containsKey(current)==false) {
		    final long counterOld = _counter;
		    countRecursive(h+1, 0, current);
		    cacheRow.put((BitSet)(current.clone()), Long.valueOf(_counter-counterOld));
		} else {
		    _counter += cacheRow.get(current).longValue();
		}
	    } else {
		if (previous.get(next)==true) {
		    current.set(next, false);
		    countRecursive(h, next, previous);
		    current.set(next, true);
		}
	    }
	}
    }

    public static void main(String[] args) {
	try {
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    String line = null;
	    System.out.print("Width=");
	    line = br.readLine();
	    final int width = Integer.parseInt(line);
	    System.out.print("Height=");
	    line = br.readLine();
	    final int height = Integer.parseInt(line);
	    Wall w = new Wall(width,height);

	    System.out.println("Created wall class");
	    long count = w.count();
	    System.out.println("There are "+count+" ways");
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
}
