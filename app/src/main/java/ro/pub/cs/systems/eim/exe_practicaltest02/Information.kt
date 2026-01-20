package ro.pub.cs.systems.eim.exe_practicaltest02

class Information(
val temperature: String,
val windSpeed: String,
val condition: String,
val pressure: String,
val humidity: String
)
{

    override fun toString() : String{
        return "Temp: $temperature\nVant: $windSpeed\nStare: $condition\nPresiune: $pressure\nUmiditate: $humidity";
    }
}