package com.liabit.test

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class TestGuiActivity : AppCompatActivity() {

    var button: Button? = null
    var seekbar: SeekBar? = null
    var seekbar1: SeekBar? = null
    var seekbar2: SeekBar? = null
    var seekbar3: SeekBar? = null
    var discreteSeekbar: SeekBar? = null
    var switch1: Switch? = null
    var volumeRowSlider: SeekBar? = null
    var verticalRowSlider: SeekBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ui_seekbar_demo)

        seekbar = findViewById(R.id.seekbar)
        seekbar1 = findViewById(R.id.seekbar1)
        seekbar2 = findViewById(R.id.seekbar2)
        seekbar3 = findViewById(R.id.seekbar3)
        discreteSeekbar = findViewById(R.id.discrete_seekbar)
        switch1 = findViewById(R.id.switch1)
        volumeRowSlider = findViewById(R.id.volume_row_slider)
        verticalRowSlider = findViewById(R.id.vertical_row_slider)

        button = findViewById(R.id.button)
        button?.setOnClickListener {
            if ("Disable" == button?.text) {
                button?.text = "Enable"
                seekbar?.isEnabled = false
                seekbar1?.isEnabled = false
                seekbar2?.isEnabled = false
                seekbar3?.isEnabled = false
                discreteSeekbar?.isEnabled = false
                switch1?.isEnabled = false
                volumeRowSlider?.isEnabled = false
                verticalRowSlider?.isEnabled = false
            } else {
                button?.text = "Disable"
                seekbar?.isEnabled = true
                seekbar1?.isEnabled = true
                seekbar2?.isEnabled = true
                seekbar3?.isEnabled = true
                discreteSeekbar?.isEnabled = true
                switch1?.isEnabled = true
                volumeRowSlider?.isEnabled = true
                verticalRowSlider?.isEnabled = true
            }
        }
    }
}