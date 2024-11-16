package mobiledev.unb.ca.soundpooldemo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.media.SoundPool
import android.os.Bundle
import android.media.AudioAttributes
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private var soundPool: SoundPool? = null
    private var sound1 = 0
    private var sound2 = 0
    private var sound3 = 0
    private var sound4 = 0
    private var sound5 = 0
    private var sound6 = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSound(applicationContext)

        val buttonSound1: Button = findViewById(R.id.button_sound1)
        buttonSound1.setOnClickListener {
            soundPool!!.autoPause()  // Pause all other sounds
            playSound(sound1)
        }

        val buttonSound2: Button = findViewById(R.id.button_sound2)
        buttonSound2.setOnClickListener {
            playSound(sound2)
        }

        val buttonSound3: Button = findViewById(R.id.button_sound3)
        buttonSound3.setOnClickListener {
            playSound(sound3)
        }

        val buttonSound4: Button = findViewById(R.id.button_sound4)
        buttonSound4.setOnClickListener {
            playSound(sound4)
        }

        val buttonSound5: Button = findViewById(R.id.button_sound5)
        buttonSound5.setOnClickListener {
            playSound(sound5)
        }

        val buttonSound6: Button = findViewById(R.id.button_sound6)
        buttonSound6.setOnClickListener {
            playSound(sound6)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (null != soundPool) {
            soundPool!!.unload(sound1)
            soundPool!!.unload(sound2)
            soundPool!!.unload(sound3)
            soundPool!!.unload(sound4)
            soundPool!!.unload(sound5)
            soundPool!!.unload(sound6)

            soundPool!!.release()
            soundPool = null
        }
    }

    private fun initSound(context: Context) {
        soundPool = createNewSoundPool()
        sound1 = loadSound(context, R.raw.crackling_fireplace)
        sound2 = loadSound(context, R.raw.thunder)
        sound3 = loadSound(context, R.raw.formula1)
        sound4 = loadSound(context, R.raw.airplane_landing)
        sound5 = loadSound(context, R.raw.steam_train_whistle)
        sound6 = loadSound(context, R.raw.tolling_bell)
    }

    private fun createNewSoundPool(): SoundPool {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        return SoundPool.Builder()
            .setMaxStreams(SOUND_POOL_MAX_STREAMS)
            .setAudioAttributes(audioAttributes)
            .build()
    }

    private fun loadSound(context: Context, resourceId: Int, priority: Int = 1): Int {
        return soundPool!!.load(context, resourceId, priority)
    }

    private fun playSound(soundId: Int) {
        soundPool?.play(soundId, 1f, 1f, 1, 0, 1.0f)
    }

    companion object {
        private const val SOUND_POOL_MAX_STREAMS = 6
    }
}