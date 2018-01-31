package nl.tudelft.jpacman.board;

import nl.tudelft.jpacman.sprite.Sprite;

/**
 * Basic implementation of unit.
 *
 * @author Jeroen Roosen 
 */
class BasicUnit extends Unit {

    /**
     * Creates a new basic unit.
     */
    BasicUnit(String id) {
        super(id);
    }

    @Override
    @SuppressWarnings("return.type.incompatible")
    public Sprite getSprite() {
        return null;
    }
}
