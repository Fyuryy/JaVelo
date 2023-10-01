package ch.epfl.javelo.gui;



import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Classe TileManager
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */
public final class TileManager {

    /**
     * Capacité maximale du cache mémoire
     */
    private final int MAX_CAPACITY = 100;
    private final Path path;
    private final String serverName;
    private final Map<TileId, Image> memory = new LinkedHashMap<>(MAX_CAPACITY, 0.75f, true);


    /**
     * Constructeur public
     * @param path chemin d'accès du répertoire contenant le cache disque
     * @param serverName nom du serveur des tuiles
     */
    public TileManager(Path path, String serverName) {
        this.path = path;
        this.serverName = serverName;
    }


    /**
     * Méthode prenant en argument l'id d'une tuile et retournant l'image de la tuile
     * @param id id de la tuile
     * @return image de la tuile
     * @throws IOException en cas d'erreur d'entrée/sortie
     */
    public Image imageForTileAt(TileId id) throws IOException {

        if (!memory.containsKey(id)) {
            cacheDisk(id);
        }

        return memory.get(id);
    }

    /**
     * Méthode auxiliaire permettant de cherhcer les images dans le cache disque
     * @param id id de la tuile à chercher
     * @throws IOException en cas d'erreur d'entrée/sortie
     */
    private void cacheDisk(TileId id) throws IOException {
        Path diskpath = path.resolve(String.valueOf(id.zoom)).
                resolve(String.valueOf(id.X)).
                resolve((String.valueOf(id.Y)) + ".png");

        if (!Files.exists(diskpath)) {
            downloader(id);
        }
        try(InputStream i = new FileInputStream(diskpath.toString())) {
            addImage(id, new Image(i));
        }
    }

    /**
     * Méthode auxiliaire permettant de télécharger les images des tuiles depuis le serveur
     * @param id id de la tuile à télécharger
     * @throws IOException en cas d'erreur d'entrée/sortie
     */
    private void downloader(TileId id) throws IOException {
        Files.createDirectories(Path.of(path.toString() +
                '/' + id.zoom + '/' + id.X));
        URL u = new URL("https://" + serverName +
                '/' + id.zoom + '/' + id.X + '/' + id.Y + ".png");
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "JaVelo");

        try (InputStream i = c.getInputStream()) {
            OutputStream o = new FileOutputStream(path.toString() +
                    '/' + id.zoom + '/' + id.X + '/' + id.Y + ".png");
            i.transferTo(o);
        }
    }

    /**
     * Méthode auxiliaire permettant d'ajouter les tuiles dans le cache mémoire
     * @param id id de la tuile à ajouter
     * @param image image de la tuile respective
     */
    private void addImage(TileId id, Image image){
        if(memory.size()==MAX_CAPACITY){
            Iterator<TileId> it = memory.keySet().iterator();
            memory.remove(it.next());
        }
        memory.put(id, image);
    }


    /**
     * Enregistrement TileId (represente une tuile avec son niveau de zoom et ses coordonnées x et y)
     */
    public record TileId(int zoom, int X, int Y) {

        /**
         * Méthode permettant de vérifier la validité d'une tuile par rapport à la carte
         * @param zoom niveau de zoom dans lequel on veut vérifier la tuile
         * @param X coordonnée x de la tuile
         * @param Y coordonnée y de la tuile
         * @return true si la tuile est valide, sinon retourne false
         */
        public static boolean isValid(int zoom, int X, int Y) {
            double lim = Math.pow(2, zoom);
            return (X>=0 && Y>=0) && (X<lim && Y<lim);


        }
    }
}

