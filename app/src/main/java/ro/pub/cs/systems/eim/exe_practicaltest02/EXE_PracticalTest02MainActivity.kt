package ro.pub.cs.systems.eim.exe_practicaltest02

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EXE_PracticalTest02MainActivity : AppCompatActivity() {

    // Referință către ServerThread pentru a-l putea opri la final
    private var serverThread: ServerThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exe_practical_test02_main)

        // --- SERVER ------------------------------------------------------------------------------------------------------
        val serverPortEditText = findViewById<EditText>(R.id.serverPortEditText)
        val connectButton = findViewById<Button>(R.id.connectButton)

        connectButton.setOnClickListener {
            val serverPort = serverPortEditText.text.toString()
            if (serverPort.isEmpty()) {
                Toast.makeText(this, "Server port should be filled!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            serverThread = ServerThread(serverPort.toInt())
            serverThread!!.start()
        }
        // ------------------------------------------------------------------------------------------------------------------

        // --- CLIENT -------------------------------------------------------------------------------------------------------
        val clientAddressEditText = findViewById<EditText>(R.id.clientAddressEditText)
        val clientPortEditText = findViewById<EditText>(R.id.clientPortEditText)
        val cityEditText = findViewById<EditText>(R.id.cityEditText)
        val informationTypeSpinner = findViewById<Spinner>(R.id.infoTypeSpinner)
        val getWeatherButton = findViewById<Button>(R.id.getWeatherButton)
        val weatherTextView = findViewById<TextView>(R.id.resultTextView)

        getWeatherButton.setOnClickListener {
            val clientAddress = clientAddressEditText.text.toString()
            val clientPort = clientPortEditText.text.toString()
            val city = cityEditText.text.toString()
            val informationType = informationTypeSpinner.selectedItem.toString()

            if (clientAddress.isEmpty() || clientPort.isEmpty() || city.isEmpty() || informationType.isEmpty()) {
                Toast.makeText(this, "Fill in all client fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            weatherTextView.text = "Waiting for data..."

            val clientThread = ClientThread(
                clientAddress,
                clientPort.toInt(),
                city,
                informationType,
                weatherTextView
            )
            clientThread.start()
        }
        // -------------------------------------------------------------------------------------------------------
    }

    override fun onDestroy() {
        Log.i(General.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked")
        serverThread?.stopThread()
        super.onDestroy()
    }
}