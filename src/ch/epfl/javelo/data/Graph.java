package ch.epfl.javelo.data;


import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * Class Graph
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */
public final class Graph {
    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets ;
    /**
     * Constructeur public
     * @param nodes nodes à insérer dans le Graph
     * @param sectors secteurs à insérer dans le Graph
     * @param edges edges reliant les nodes du Graph
     * @param attributeSets attributs du Graph
     */
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = List.copyOf(attributeSets);

    }

    /**
     * Retourne le graphe JaVelo obtenu à partir des fichiers se trouvant dans le répertoire
     * dont le chemin d'accès est basePath
     * @param basePath chemin d'accès des fichiers
     * @return Graph avec tous ses arguments
     * @throws IOException en cas d'erreur d'entrée ou sortie, p.ex: si l'un des fichiers n'existe pas
     */
    public static Graph loadFrom(Path basePath) throws IOException {

        GraphNodes nodes1 = new GraphNodes(tryMethod("nodes.bin",basePath).asIntBuffer());
        GraphSectors sectors1 = new GraphSectors(tryMethod("sectors.bin", basePath));
        ByteBuffer edgesBuffer = tryMethod("edges.bin",basePath);
        IntBuffer profilesBuffer = tryMethod("profile_ids.bin",basePath).asIntBuffer();
        ShortBuffer elevationsBuffer = tryMethod("elevations.bin",basePath).asShortBuffer();
        GraphEdges edges1 = new GraphEdges(edgesBuffer,profilesBuffer,elevationsBuffer);
        List<AttributeSet> attributeSets1 = new ArrayList<>();
        LongBuffer attributesBuffer = tryMethod("attributes.bin",basePath).asLongBuffer();

        int lengthTab = attributesBuffer.capacity();
        for (int i = 0; i < lengthTab; i++) {
            attributeSets1.add(new AttributeSet(attributesBuffer.get(i)));
        }
        return new Graph(nodes1, sectors1, edges1, attributeSets1);

    }

    /**
     * Compte les nodes d'un graph
     * @return le nombre de nodes d'un graph
     */
    public int nodeCount() {
        return nodes.count();
    }

    /**
     * Retourne la position du noeud d'identité donnée
     * @param nodeId identité du noeud duquel on veut la position
     * @return PointCh qui est sur le noeud d'identité nodeId
     */
    public PointCh nodePoint(int nodeId) {
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    /**
     * Retourne le nombre d'arêtes sortantes d'un noeud
     * @param nodeId identité du noeud duquel on cherche le nombre d'arêtes sortantes
     * @return nombre d'arêtes sortantes du noeud d'identité nodeId
     */
    public int nodeOutDegree(int nodeId) {
        return nodes.outDegree(nodeId);
    }

    /**
     * Retourne l'identité de la edgeIndex-ième arête sortant du noeud d'identité edgeId
     * @param nodeId    identité du noeud duquel on cherche la edgeIndex-ième arête
     * @param edgeIndex index de l'edge sortante que l'on cherche
     * @return identité de la edgeIndex-ième arête sortant du noeud d'identité edgeId
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId, edgeIndex);
    }

    /**
     * retourne l'identité du nœud se trouvant le plus proche du point donné, à la distance maximale donnée
     * (en mètres), ou -1 si aucun nœud ne correspond à ces critères,
     * @param point          point duquel on cherche le noeud le plus proche
     * @param searchDistance rayon de recherche de noeud
     * @return le noeud le plus proche du point ou -1 si aucun noued ne correspond aux critères ci-dessus
     */
    public int nodeClosestTo(PointCh point, double searchDistance) {
        List<GraphSectors.Sector> inArea = sectors.sectorsInArea(point, searchDistance);
        double distanceMin = searchDistance * searchDistance;
        int nodeId = -1;
        for (GraphSectors.Sector sector : inArea) {
            int firstId = sector.startNodeId();
            int lastId = sector.endNodeId();
            for (int i = firstId; i < lastId; i++) {
                double distance = point.squaredDistanceTo(nodePoint(i));
                if (distance <= distanceMin) {
                    distanceMin = distance;
                    nodeId = i;
                }
            }
        }
        return nodeId;
    }

    /**
     * retourne l'identité du nœud destination de l'arête d'identité donnée
     * @param edgeId id de l'arête
     * @return l'identité du noeud destination de l'arête d'id edgeId
     */
    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }

    /**
     * retourne vrai si l'arête d'id edgeId va dans le sens contraire de la voie OSM
     * @param edgeId id de l'arête
     * @return vrai si l'arête d'id edgeId va dans le sens contraire de la voie OSM et faux sinon
     */
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }

    /**
     * retourn l'ensemble des attributs OSM attachés à l'arête d'id donné
     * @param edgeId id de l'arête
     * @return l'ensemble des attributs OSM attachés à l'arête
     */
    public AttributeSet edgeAttributes(int edgeId) {
        return attributeSets.get(edges.attributesIndex(edgeId));
    }

    /**
     * retourne la longuer en mètres de l'arête d'identité donnée
     * @param edgeId identité de l'arête
     * @return longueur de l'arête d'identité edgeId en mètres
     */
    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }

    /**
     * retourne le dénivelé positif total de l'arête d'identité donnée,
     * @param edgeId identité de l'arête
     * @return le dénivelé positif total de l'arête d'identité donnée
     */
    public double edgeElevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }

    /**
     * retourne le profil en long de l'arête d'identité donnée
     * @param edgeId identité de l'arête
     * @return le profil en long de l'arête ou la constante Double.NaN si l'arête ne possède pas de profil
     */
    public DoubleUnaryOperator edgeProfile(int edgeId) {

        return (edges.hasProfile(edgeId) ?
                Functions.sampled(edges.profileSamples(edgeId), edgeLength(edgeId)) :
                Functions.constant(Double.NaN));

    }

    /**
     * Méthode permettant de "try and open" un ficher en vérifiant si l'on peut l'accéder
     * @param nom nom du fichier
     * @param basePath chemin du fichier
     * @return Un ByteBuffer contenant les informations du fichier
     * @throws IOException Lance une exception si le fichier ne peut pas être ouvert
     */
    private static ByteBuffer tryMethod(String nom, Path basePath) throws IOException{

        Path path = basePath.resolve(nom);
        ByteBuffer buffer;
        try (FileChannel channel = FileChannel.open(path)) {
            buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0,
                    channel.size());
        }
        return buffer;
    }



}
