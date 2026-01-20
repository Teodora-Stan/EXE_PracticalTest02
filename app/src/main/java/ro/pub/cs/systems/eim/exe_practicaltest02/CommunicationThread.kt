package ro.pub.cs.systems.eim.exe_practicaltest02

import android.annotation.SuppressLint
import android.util.Log
import java.net.Socket
import java.net.URL

import java.io.IOException
import java.io.InputStreamReader

import org.json.JSONObject

import java.io.BufferedWriter
import java.io.BufferedReader
import java.io.OutputStreamWriter

import javax.net.ssl.HttpsURLConnection

class CommunicationThread(private val serverThread: ServerThread, private val clientSocket: Socket) : Thread() {
    
    var prt_log: Boolean = true

    fun prt_debug(msg: String) {
        if (prt_log)
            Log.i(General.TAG, "[COMMUNICATION THREAD ${this.id}] $msg")
    }



    override fun run() {
        prt_debug("A început comunicarea cu un client.")

        try {
            val inputStream  = BufferedReader(InputStreamReader  (clientSocket.getInputStream()))
            val outputStream = BufferedWriter(OutputStreamWriter(clientSocket.getOutputStream()))
//            val printWriter = PrintWriter(socket.getOutputStream(), true)

            prt_debug("Waiting param from client...");

            val str = inputStream.readLine()
            val tip = inputStream.readLine()
            
            if (str.isNullOrEmpty()) {
                outputStream.write("Error: No string provided")
                outputStream.newLine()
                outputStream.flush()
                return
            }
            if(tip.isNullOrEmpty()){
                outputStream.write("Error: No type provided")
                outputStream.newLine()
                outputStream.flush()
                return
            }
            prt_debug("$str, $tip")
            
            prt_debug("Requested key -- s: $str")

            var information = serverThread.getData(str)
            
            if (information == null) {
                prt_debug("Cache miss, fetching from API...")
                information = fetchData(str)
                
                if (information != null) {
                    serverThread.setData(str, information)
                }
            } else {
                prt_debug("Cache hit!")
            }

            if (information != null) {
//                outputStream.write(information.toString())
                val result = when (tip) {
                    "all" -> information.toString()
                    "temp" -> information.temperature
                    "wind" -> information.windSpeed
                    "condition" -> information.condition
                    "humidity" -> information.humidity
                    "pressure" -> information.pressure
                    else -> "Wrong information type!"
                }
                outputStream.write(result);
                outputStream.newLine()
                outputStream.flush()
                prt_debug("Am trmimis:$result");
            } else {
                outputStream.write("Error: Could not fetch weather data")
                outputStream.newLine()
                outputStream.flush()
            }
            
        } catch (e: IOException) {
            prt_debug("Error: ${e.message}")
        } finally {
            try {
                clientSocket.close()
            } catch (e: IOException) {
                prt_debug("Error closing socket: ${e.message}")
            }
        }
    }
    
    @SuppressLint("DefaultLocale")
    private fun fetchData(s_key_for_info: String): Information? {
        return try {
            val url = URL(
                             General.WEB_SERVICE_ADRESS +
                        "?q="     + s_key_for_info +
                        "&appid=" + General.WEB_SERVICE_API_KEY + ""
            )

            val url_connection : HttpsURLConnection = url.openConnection() as HttpsURLConnection
            url_connection.setRequestMethod("GET");

            val reader = BufferedReader(InputStreamReader(url_connection.getInputStream()))
            val response = StringBuilder()
            var line: String? = ""
            while (true) {
                line = reader.readLine()
                if(line == null)
                    break;
                response.append(line)
            }
            reader.close()
            
            val json  = JSONObject(response.toString())
            val main = json.getJSONObject("main")
            val wind = json.getJSONObject("wind")
            val weather = json.getJSONArray("weather").getJSONObject(0)
            
            Information(
                temperature = String.format("%.2f", (main.getDouble("temp")-272.15)) + "°C",
                windSpeed = wind.getDouble("speed").toString() + " m/s",
                condition = weather.getString("description"),
                pressure = main.getInt("pressure").toString() + " hPa",
                humidity = main.getInt("humidity").toString() + "%"
            )
        } catch (e: Exception) {
            prt_debug("API Error: ${e.message}")
            null
        }
    }
}
