package ro.pub.cs.systems.eim.exe_practicaltest02


import android.util.Log
import java.net.ServerSocket
import java.util.concurrent.ConcurrentHashMap
import java.io.IOException


class ServerThread(private val port : Int): Thread() {
    public var socket : ServerSocket? = null;
    private var data = ConcurrentHashMap<String, Information>();

    fun setData(s: String, inf : Information) : String?{
        if(data.get(s) == null)
        {
            data[s]=inf;
            return s;
        }
        else
            return null;
    }

    fun getData(s: String): Information? {
        return this.data[s]
    }

    override fun run(){
        try {
            socket = ServerSocket(port)
            Log.d(General.TAG, "[SERVER] A pornit pe portul $port")

            while(true){
                Log.i(General.TAG, "[SERVER] Asteptam clienti...")

                // Serverul se blochează aici până se conectează cineva
                val socket_client = socket!!.accept()
                Log.i(General.TAG, "[SERVER] S-a conectat cineva!")

                val communicationThread = CommunicationThread(this, socket_client)
                communicationThread.start()

                if(Thread.currentThread().isInterrupted)
                    break;
            }

        } catch (e : IOException){
            Log.e(General.TAG, "[SERVER] Eroare: " + e.message);
        }
    }

    fun stopThread(){
        interrupt()
        socket?.close()
    }




}