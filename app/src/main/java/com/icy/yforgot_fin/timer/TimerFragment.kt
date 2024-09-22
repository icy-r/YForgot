package com.icy.yforgot_fin.timer

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.icy.yforgot_fin.R
import com.icy.yforgot_fin.services.TimerService
import java.util.concurrent.TimeUnit

class TimerFragment : Fragment() {
    private lateinit var hoursTextView: TextView
    private lateinit var minutesTextView: TextView
    private lateinit var secondsTextView: TextView
    private lateinit var millisecondsTextView: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var resetButton: Button

    private var timerService: TimerService? = null
    private var isBound = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateUIRunnable: Runnable

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TimerService.LocalBinder
            timerService = binder.getService()
            isBound = true
            updateButtonStates(isRunning = timerService?.isRunning == true)
            startUpdatingUI()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_timer, container, false)

        hoursTextView = rootView.findViewById(R.id.hours_text_view)
        minutesTextView = rootView.findViewById(R.id.minutes_text_view)
        secondsTextView = rootView.findViewById(R.id.seconds_text_view)
        millisecondsTextView = rootView.findViewById(R.id.milliseconds_text_view)
        startButton = rootView.findViewById(R.id.start_button)
        stopButton = rootView.findViewById(R.id.stop_button)
        resetButton = rootView.findViewById(R.id.reset_button)

        setupButtons()

        return rootView
    }

    override fun onStart() {
        super.onStart()
        Intent(requireContext(), TimerService::class.java).also { intent ->
            requireContext().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            requireContext().unbindService(connection)
            isBound = false
        }
        stopUpdatingUI()
    }

    private fun setupButtons() {
        startButton.setOnClickListener {
            startForegroundService()
            timerService?.startTimer()
            updateButtonStates(isRunning = true)
        }

        stopButton.setOnClickListener {
            timerService?.stopTimer()
            updateButtonStates(isRunning = false)
        }

        resetButton.setOnClickListener {
            timerService?.resetTimer()
            updateTimerText(0)
            updateButtonStates(isRunning = false)
        }
    }


    private fun updateButtonStates(isRunning: Boolean) {
        startButton.isEnabled = !isRunning
        stopButton.isEnabled = isRunning
        resetButton.isEnabled = !isRunning || (timerService?.getElapsedTime() ?: 0) > 0
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun updateTimerText(elapsedTime: Long) {
        val hours = TimeUnit.MILLISECONDS.toHours(elapsedTime)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60
        val milliseconds = (elapsedTime % 1000) / 10 // Convert to 0-100 range

        hoursTextView.text = String.format("%02d", hours)
        minutesTextView.text = String.format("%02d", minutes)
        secondsTextView.text = String.format("%02d", seconds)
        millisecondsTextView.text = String.format("%02d", milliseconds)
    }

    private fun startUpdatingUI() {
        updateUIRunnable = object : Runnable {
            override fun run() {
                val elapsedTime = timerService?.getElapsedTime() ?: 0
                updateTimerText(elapsedTime)
                updateButtonStates(isRunning = elapsedTime > 0 && timerService?.isRunning == true)
                handler.postDelayed(this, 10)
            }
        }
        handler.post(updateUIRunnable)
    }

    private fun stopUpdatingUI() {
        handler.removeCallbacks(updateUIRunnable)
    }

    private fun startForegroundService() {
        val serviceIntent = Intent(requireContext(), TimerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(serviceIntent)
        } else {
            requireContext().startService(serviceIntent)
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = TimerFragment()
    }
}