package de.meonwax.soundboard.sound;

public class Sound {

    private int id;

    private String name;

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
