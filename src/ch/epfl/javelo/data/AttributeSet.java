package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;
import org.w3c.dom.Attr;

import java.util.StringJoiner;

/**
 * Enregistrement AttributeSet
 *
 * @author Pedro Gouveia (345768)
 * @author Idriss Mimet (324424)
 */

public record  AttributeSet(long bits) {

    /**
     * Constructeur compact
     * @param bits répresente le contenu de l'ensemble au moyen d'un bit par valeur possible
     *             (bit d'index b de cette valeur vaut 1 si et seulement si l'attribut b est contenu dans l'ensemble)
     * @throws IllegalArgumentException si la valeur passée au constructeur contient
     *                                  un bit à 1 qui ne correspond à aucun attribut valide.
     */
    public AttributeSet {
        Preconditions.checkArgument((bits >>> Attribute.COUNT) == 0);
    }

    /**
     * Retourne un ensemble contenant uniquement les attributs donnés en argument
     *
     * @param attributes attributs donnés
     * @return un ensemble contenant les attributs donnés en argument
     */
    public static AttributeSet of(Attribute... attributes) {
        long bits = 0L;
        for (Attribute att : attributes) {
            bits |= 1L << att.ordinal();
        }
        return new AttributeSet(bits);



    }

    /**
     * Retourne vrai si et seulement si l'ensemble récepteur (this) contient l'attribut donné
     * @param attribute attribut donné
     * @return vrai si l'ensemble this contient l'attribut et faux sinon
     */
    public boolean contains(Attribute attribute) {
        long mask = 1L << attribute.ordinal();
        return ((mask & bits) == mask);

    }

    /**
     * Retourne vrai si et seulement si l'intersection de l'ensemble récepteur (this) avec
     * celui passé en argument n'est pas vide
     * @param that attribut donné
     * @return vrai si l'intersection de l'ensemble
     * récepteur avec celui passé en argument n'est pas vide
     */
    public boolean intersects(AttributeSet that) {
        return ((this.bits & that.bits) != 0L);
    }

    /**
     * Redifinition de la méthode toString
     * @return une string contenant les keyvalues des attributs
     */
    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(",", "{", "}");
        for (Attribute attr : Attribute.ALL) {
            if (this.contains(attr)) {
                j.add(attr.toString());
            }
        }
        return j.toString();
    }


}
