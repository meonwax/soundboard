package de.meonwax.soundboard.sound;

import android.annotation.TargetApi;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public class SoundPoolBuilder {

    public static SoundPool build() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return buildModern();
        } else {
            return buildLegacy();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static SoundPool buildModern() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();
        return new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(audioAttributes)
                .build();
    }

    @SuppressWarnings("deprecation")
    private static SoundPool buildLegacy() {
        return new SoundPool(2, AudioManager.STREAM_MUSIC, 1);
    }
}
