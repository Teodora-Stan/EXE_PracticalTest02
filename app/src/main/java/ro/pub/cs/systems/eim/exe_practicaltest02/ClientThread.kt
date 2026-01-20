package ro.pub.cs.systems.eim.exe_practicaltest02

import android.media.MediaParser
import android.util.Log
import android.widget.TextView
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class ClientThread(
    private val address : String,
    private val port : Int,
    private val s : String,
    private val infoType : String,
    private val responseTextView: TextView // Primim TextView-ul ca să scriem în el
    ) : Thread()
{

    override fun run() {
        try {
            val socket = Socket(address, port);
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val writer = PrintWriter(socket.getOutputStream(), true)
            writer.println(s)
            writer.println(infoType)
            val raspuns = StringBuilder()
            var line: String? = ""
            while (true) {
                line = reader.readLine()
                if (line == null)
                    break;
                raspuns.append(line + "\n");
            }
            Log.d(General.TAG, "[CLIENT THREAD ${this.id}]Am primit: $raspuns");
            socket.close()
            responseTextView.post {
                responseTextView.text = raspuns.toString()
            }
        } catch (e: Exception) {
            Log.e(General.TAG, "[CLIENT THREAD] An exception has occurred: " + e.message)
            responseTextView.post {
                responseTextView.text = "Error: " + e.message
            }
        }
    }
}