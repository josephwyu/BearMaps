package bearmaps.proj2c;

import bearmaps.MyTrieSet;
import bearmaps.hw4.streetmap.Node;
import bearmaps.hw4.streetmap.StreetMapGraph;
import bearmaps.proj2ab.Point;
import bearmaps.proj2ab.WeirdPointSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An augmented graph that is more powerful that a standard StreetMapGraph.
 * Specifically, it supports the following additional operations:
 *
 * @author Alan Yao, Josh Hug, ________
 */
public class AugmentedStreetMapGraph extends StreetMapGraph {
    private HashMap<Point, Node> m;
    private HashMap<String, ArrayList<String>> clean;
    private HashMap<String, ArrayList<Node>> nameNode;
    private WeirdPointSet w;
    private MyTrieSet t;

    public AugmentedStreetMapGraph(String dbPath) {
        super(dbPath);
        // You might find it helpful to uncomment the line below:
        List<Node> nodes = this.getNodes();
        List<Point> points = new ArrayList<>();
        m = new HashMap<>();
        clean = new HashMap<>();
        t = new MyTrieSet();
        nameNode = new HashMap<>();
        for (Node n : nodes) {
            m.put(new Point(n.lon(), n.lat()), n);
            if (neighbors(n.id()).size() > 0) {
                points.add(new Point(n.lon(), n.lat()));
            }
        }

        w = new WeirdPointSet(points);

        for (Node n : nodes) {
            if(n.name() != null ) {
                t.add(cleanString(n.name()));
                if (!clean.containsKey(cleanString(n.name()))) {
                    ArrayList<String> z = new ArrayList<>();
                    z.add(n.name());
                    clean.put(cleanString(n.name()), z);
                } else {
                    clean.get(cleanString(n.name())).add(n.name());
                }
            }

        }

        for (Node n : nodes) {
            if(n.name() != null) {
                if (!nameNode.containsKey(cleanString(n.name()))) {
                    ArrayList<Node> u = new ArrayList<>();
                    u.add(n);
                    nameNode.put(cleanString(n.name()), u);
                } else {
                    nameNode.get(cleanString(n.name())).add(n);
                }

            }
         }


    }


    /**
     * For Project Part II
     * Returns the vertex closest to the given longitude and latitude.
     *
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    public long closest(double lon, double lat) {
        return m.get(w.nearest(lon, lat)).id();
    }


    /**
     * For Project Part III (gold points)
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     *
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public List<String> getLocationsByPrefix(String prefix) {
        ArrayList<String> a = new ArrayList<>();
        String cleaned = cleanString(prefix);
        if(cleaned.length() < 1) {
            cleaned += " ";
        }
        if(t.keysWithPrefix(cleaned) != null) {
            for (String cle : t.keysWithPrefix(cleaned)) {
                if(clean.containsKey(cle)) {
                    for (String c : clean.get(cle)) {
                        a.add(c);
                    }
                }
            }
        }
        return a;
    }

    /**
     * For Project Part III (gold points)
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     *
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" -> Number, The latitude of the node. <br>
     * "lon" -> Number, The longitude of the node. <br>
     * "name" -> String, The actual name of the node. <br>
     * "id" -> Number, The id of the node. <br>
     */
    public List<Map<String, Object>> getLocations(String locationName) {
        ArrayList<Map<String, Object>> ml = new ArrayList<>();
        String cleaned = cleanString(locationName);
        for (Node n : nameNode.get(cleaned)) {
            HashMap<String, Object> qa = new HashMap<>();
            qa.put("lat", n.lat());
            qa.put("lon", n.lon());
            qa.put("name", n.name());
            qa.put("id", n.id());
            ml.add(qa);
        }
        return ml;

    }


    /**
     * Useful for Part III. Do not modify.
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     *
     * @param s Input string.
     * @return Cleaned string.
     */
    private static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

}
