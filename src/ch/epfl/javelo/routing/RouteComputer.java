package ch.epfl.javelo.routing;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.*;
import java.util.List;


public final class RouteComputer {
    private final Graph graph;
    private final CostFunction costFunction;

    private final static Float NEG_INFINITY = Float.NEGATIVE_INFINITY;
    /**
     * Construit un objet de la classe RouteComputer
     * @param graph graph sur lequel est la route
     * @param costFunction fonction de coût pour la route
     */
    public RouteComputer(Graph graph, CostFunction costFunction){

        this.graph = graph;
        this.costFunction = costFunction;
    }

    /**
     * Fonction qui construit un planificateur d'itinéraire pour le graphe et la fonction de coûts donnés.
     * @param startNodeId identité du premier noeud de l'itinéraire
     * @param endNodeId identité du dernier noeud de l'itinéraire
     * @return un planificateur d'itinéraire pour le graphe et la fonction de coûts donnés.
     */

    public Route bestRouteBetween(int startNodeId, int endNodeId) {
        Preconditions.checkArgument(startNodeId != endNodeId);
        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        PointCh endPoint = graph.nodePoint(endNodeId);
        int nodeCount = graph.nodeCount();
        double[] distances = new double[nodeCount];
        int[] predecesseur = new int[nodeCount];

        PriorityQueue<WeightedNode> queue = new PriorityQueue<>();

        queue.add(new WeightedNode(startNodeId, 0));

        Arrays.fill(distances, Double.POSITIVE_INFINITY);

        distances[startNodeId] = 0;

        do {
            assert queue.peek() != null;
            WeightedNode nodeMin = queue.remove();
            int nodeMinId = nodeMin.nodeId;

            if (distances[nodeMinId] != NEG_INFINITY) {
                if (nodeMinId == endNodeId) {
                    Deque<Edge> edgeDeck = new ArrayDeque<>();
                    int currentNodeId = nodeMinId;

                    do {
                        int nodePredecesseurId = Bits.extractUnsigned
                                (predecesseur[currentNodeId],0,28);

                        int edgeId = graph.nodeOutEdgeId(nodePredecesseurId,
                                Bits.extractUnsigned(predecesseur[currentNodeId],28,4));

                        edgeDeck.addFirst(Edge.of(graph, edgeId,
                                nodePredecesseurId, currentNodeId));
                        currentNodeId = nodePredecesseurId;
                    } while (currentNodeId != startNodeId);

                    List<Edge> edges = new ArrayList<>(edgeDeck);
                    return new SingleRoute(edges);
                }
                for (int i = 0; i < graph.nodeOutDegree(nodeMinId); i++) {
                    int edgeId = graph.nodeOutEdgeId(nodeMinId, i);
                    int nodeId = graph.edgeTargetNodeId(edgeId);
                    double d = (distances[nodeMinId] + (graph.edgeLength(edgeId) *
                            costFunction.costFactor(nodeMinId, edgeId)));

                    if (d < distances[nodeId]) {
                        distances[nodeId] = d;
                        predecesseur[nodeId] = (i<<28) | nodeMinId;
                        queue.add(new WeightedNode(nodeId,
                                (float) (d + endPoint.distanceTo(graph.nodePoint(nodeId)))));
                    }
                }
                distances[nodeMinId] = Float.NEGATIVE_INFINITY;
            }
        } while (!queue.isEmpty()) ;


        return null;
    }

}