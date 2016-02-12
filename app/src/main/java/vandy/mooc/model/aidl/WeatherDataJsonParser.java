package vandy.mooc.model.aidl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import vandy.mooc.model.aidl.WeatherData.Main;
import vandy.mooc.model.aidl.WeatherData.Sys;
import vandy.mooc.model.aidl.WeatherData.Weather;
import vandy.mooc.model.aidl.WeatherData.Wind;
import android.util.JsonReader;
import android.util.JsonToken;

/**
 * Parses the Json weather data returned from the Weather Services API
 * and returns a List of WeatherData objects that contain this data.
 */
public class WeatherDataJsonParser {
    /**
     * Used for logging purposes.
     */
    private final String TAG =
        this.getClass().getCanonicalName();

    /**
     * Parse the @a inputStream and convert it into a List of JsonWeather
     * objects.
     */
    public List<WeatherData> parseJsonStream(InputStream inputStream)
        throws IOException {

        // TODO -- you fill in here.
        // Create a JsonReader for the inputStream.
        try (JsonReader reader =
                     new JsonReader(new InputStreamReader(inputStream,
                             "UTF-8"))) {
            // Log.d(TAG, "Parsing the results returned as an array");

            // Handle the array returned from the Acronym Service.
            return parseJsonWeatherDataArray(reader);
        }

    }

    /**
     * Parse a Json stream and convert it into a List of WeatherData
     * objects.
     */
    public List<WeatherData> parseJsonWeatherDataArray(JsonReader reader)
        throws IOException {
        // TODO -- you fill in here.
        reader.beginObject();
        //reader.beginArray();

        if (reader.peek() == JsonToken.END_ARRAY)
            return null;

        // Create a WeatherData object for each element in the
        // Json array.
        final List<WeatherData> weatherData = new ArrayList<WeatherData>();

        while(reader.hasNext())
        {
            weatherData.add(parseJsonWeatherData(reader));
        }
        //reader.endArray();
        reader.endObject();
        return weatherData;
    }

    /**
     * Parse a Json stream and return a WeatherData object.
     */
    public WeatherData parseJsonWeatherData(JsonReader reader) 
        throws IOException {

        // TODO -- you fill in here.
        String mName = "";
        long mDate = System.currentTimeMillis();
        long mCod = 0;
        List<Weather> mWeathers = new ArrayList<Weather>();
        Sys mSys = null;
        Main mMain = null;
        Wind mWind = null;
//        String mMessage = "";

        reader.beginObject();

        while(reader.hasNext()) {
            String name = reader.nextName();
            if(name.equals(WeatherData.name_JSON))
            {
                mName = reader.nextString();
            }
            else if(name.equals(WeatherData.dt_JSON))
            {
                mDate = reader.nextLong();
            }
            else if(name.equals(WeatherData.cod_JSON))
            {
                mCod = reader.nextLong();
            }
            else if(name.equals(WeatherData.weather_JSON)  && reader.peek() != JsonToken.NULL)
            {
                mWeathers = parseWeathers(reader);
            }
            else if(name.equals(WeatherData.sys_JSON))
            {
                mSys = parseSys(reader);
            }
            else if(name.equals(WeatherData.main_JSON))
            {
                mMain = parseMain(reader);
            }
            else if(name.equals(WeatherData.wind_JSON))
            {
                mWind = parseWind(reader);
            }
//            else if(name.equals(WeatherData.message_JSON))
//            {
//                mMessage = reader.nextString();
//            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new WeatherData(mName, mDate, mCod, mSys, mMain, mWind, mWeathers);
    }
    
    /**
     * Parse a Json stream and return a List of Weather objects.
     */
    public List<Weather> parseWeathers(JsonReader reader) throws IOException {
        // TODO -- you fill in here.
        List<Weather> weathers = new ArrayList<>();

        reader.beginArray();
        while(reader.hasNext()) {
            weathers.add(parseWeather(reader));
        }
        reader.endArray();
        return weathers;
    }

    /**
     * Parse a Json stream and return a Weather object.
     */
    public Weather parseWeather(JsonReader reader) throws IOException {
        // TODO -- you fill in here.private long mId;
        long mId = new Random().nextInt(100000000);
        String mMain = "";
        String mDescription = "";
        String mIcon = "";

        reader.beginObject();
        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals(Weather.id_JSON))
            {
                mId = reader.nextLong();
            }
            else if(name.equals(Weather.main_JSON))
            {
                mMain = reader.nextString();
            }
            else if(name.equals(Weather.description_JSON))
            {
                mDescription = reader.nextString();
            }
            else if(name.equals(Weather.icon_JSON))
            {
                mIcon = reader.nextString();
            }
            else
            {
                reader.skipValue();
            }
        }
        reader.endObject();

        return new Weather(mId, mMain, mDescription, mIcon);
    }

    /**
     * Parse a Json stream and return a Main Object.
     */
    public Main parseMain(JsonReader reader) 
        throws IOException {
        // TODO -- you fill in here.
        double mTemp = 0.0;
        long mHumidity = 0;
        double mPressure = 0.0;

        reader.beginObject();
        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals(Main.temp_JSON)
                    || name.equals(Main.tempMax_JSON)
                    || name.equals(Main.tempMin_JSON))
            {
                mTemp = reader.nextDouble();
            }
            else if(name.equals(Main.humidity_JSON))
            {
                mHumidity = reader.nextLong();
            }
            else if(name.equals(Main.pressure_JSON))
            {
                mPressure = reader.nextDouble();
            }
            else
            {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Main(mTemp, mHumidity, mPressure);
    }

    /**
     * Parse a Json stream and return a Wind Object.
     */
    public Wind parseWind(JsonReader reader) throws IOException {
        // TODO -- you fill in here.
        double mSpeed = 0.0;
        double mDeg = 0.0;

        reader.beginObject();
        while(reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals(Wind.speed_JSON))
            {
                mSpeed = reader.nextDouble();
            }
            else if(name.equals(Wind.deg_JSON))
            {
                mDeg = reader.nextDouble();
            }
            else
            {
                reader.skipValue();
            }
        }

        reader.endObject();

        return new Wind(mSpeed, mDeg);
    }

    /**
     * Parse a Json stream and return a Sys Object.
     */
    public Sys parseSys(JsonReader reader)
        throws IOException {
        // TODO -- you fill in here.
        long mSunrise = 0;
        long mSunset = 0;
        String mCountry = "";
        double mMessage = 0.0;

        reader.beginObject();
        while (reader.hasNext())
        {
            String name = reader.nextName();
            if(name.equals(Sys.sunrise_JSON))
            {
               mSunrise = reader.nextLong();
            }
            else if(name.equals(Sys.sunset_JSON))
            {
                mSunset = reader.nextLong();
            }
            else if(name.equals(Sys.country_JSON))
            {
                mCountry = reader.nextString();
            }
            else
            {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Sys(mSunrise, mSunset, mCountry);
    }
}
