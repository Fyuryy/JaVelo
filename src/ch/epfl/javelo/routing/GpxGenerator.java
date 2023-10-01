package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.Writer;

/**
 * Classe GpxGenerator
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */

public class GpxGenerator {


    /**
     * Constructeur privé, classe non instantiable
     */
    private GpxGenerator() {
    }


    /**
     * Méthode retournant un document GPX décrivant la route et son profile
     * @param route à décrire
     * @param profile profile de la route à décrire
     * @return document décrivant la route
     */
    public static Document createGpx(Route route, ElevationProfile profile) {
        Document doc = newDocument();
        Element root = doc.createElementNS(
                "http://www.topografix.com" +
                        "/GPX/1/1", "gpx");
        doc.appendChild(root);

        root.setAttributeNS("http://www.w3.org" +
                        "/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");

        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");
        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);
        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        Element rte = doc.createElement("rte");
        root.appendChild(rte);
        double position = 0;
        for(int i = 0; i < route.points().size(); i++) {
            Element rtept = doc.createElement("rtept");
            rte.appendChild(rtept);
            PointCh pt = route.points().get(i);
            rtept.setAttribute("lat", String.valueOf(Math.toDegrees(pt.lat())));
            rtept.setAttribute("lon", String.valueOf(Math.toDegrees(pt.lon())));
            Element ele = doc.createElement("ele");
            rtept.appendChild(ele);
            if (i != 0) {
                position += pt.distanceTo(route.points().get(i - 1));
            }
            ele.setTextContent(String.valueOf(profile.elevationAt(position)));

        }
        return doc;
    }


    /**
     * Méthode qui écrit un document GPX correspondant à la route et au profile passés en argument
     * @param fileName nom du document
     * @param route itinéraire à representer dans le document
     * @param profile profile de l'itinéraire à representer dans le document
     * @throws Error en cas d'erreur d'entrée/sortie
     */
    public static void writeGpx(String fileName, Route route, ElevationProfile profile) throws Error {
        Document doc = createGpx(route, profile);

        try {
            Writer w = new FileWriter(fileName);

            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));

        }catch (Exception e){
            throw new Error(e);
        }

    }

    /**
     * Méthode auxiliaire permettant de créer un nouveau document
     * @return nouveau document
     */
    private static Document newDocument() {
        try {
            return DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); // Should never happen
        }
    }
}
