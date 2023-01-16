package com.astudio.latolato

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.astudio.latolato.databinding.FragmentFirstBinding
import java.util.*
import kotlin.math.sqrt

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), SensorEventListener2 {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var sensormgr: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f
    var mediaPlayerDub: MediaPlayer? = null
    var playable = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sensormgr = requireActivity().getSystemService(SENSOR_SERVICE) as SensorManager
        Objects.requireNonNull(sensormgr)!!
            .registerListener(this, sensormgr!!
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)

        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { event ->
            // Fetching x,y,z values
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration

            // Getting current accelerations
            // with the help of fetched x,y,z values
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta
            Log.d("pokemon", "acceleration $acceleration")

            // Display a Toast message if
            // acceleration value is over 12
            if (acceleration > 2) {
                try {
                    if (playable) {
                        playable = false
                        mediaPlayerDub =
                            MediaPlayer.create(requireContext().applicationContext, R.raw.lato1)
                        mediaPlayerDub?.setOnCompletionListener {
                            it.release()
                        }
                        mediaPlayerDub?.start()
                    }
                } catch (e: Exception) {}
//                Toast.makeText(requireContext().applicationContext, "Shake event detected", Toast.LENGTH_SHORT).show()
            } else {
                playable = true
            }
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
//        TODO("Not yet implemented")
    }

    override fun onFlushCompleted(p0: Sensor?) {
//        TODO("Not yet implemented")
    }

    override fun onResume() {
        super.onResume()
        sensormgr?.registerListener(this, sensormgr!!.getDefaultSensor(
            Sensor .TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        sensormgr!!.unregisterListener(this)
        super.onPause()
    }
}