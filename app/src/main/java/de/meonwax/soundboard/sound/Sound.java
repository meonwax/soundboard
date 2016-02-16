package de.meonwax.soundboard.sound;

public class Sound {

    private final int id;
    private final String name;

    public Sound(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
