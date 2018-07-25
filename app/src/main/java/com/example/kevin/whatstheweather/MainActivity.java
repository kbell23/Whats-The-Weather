package com.example.kevin.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity
{

    // declares UI
    EditText userInput;
    String userInputString;
    TextView resultTextView;

    /* getWeather is the function used to gather the information
       provided by the user's input
     */
    public void getWeather(View view)
    {
        try
        {
            // attempts to gather the JSON data given the user's city and converts it to a string
            InformationGather info = new InformationGather();
            userInputString = userInput.getText().toString();
            String result = "";

            // sanity check for potential spaces
            String encodedCityName = URLEncoder.encode(userInputString, "UTF-8");

            try
            {
                // attempts to get the JSON data
                result = info.execute("https://openweathermap.org/data/2.5/weather?q=" + userInputString + "&appid=b6907d289e10d714a6e88b30761fae22").get();
                Log.i("JSON: ", result);
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            // hides the user's keyboard after they click the button
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Couldn't find weather for the given location.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userInput = (EditText)findViewById(R.id.userInput);

        resultTextView = (TextView)findViewById(R.id.resultTextView);

    }

    // class to gather the JSON data provided a valid string
    public class InformationGather extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            String result = "";
            URL url = null;
            HttpURLConnection connection = null;

            // attempts connection
            try
            {
                url = new URL(urls[0]);
                connection = (HttpURLConnection)url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                char current;

                while(data != -1){
                    current = (char)data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Couldn't find weather for the given location.", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            // given the JSON data, convert the JSON into objects to be displayed to the user
            try
            {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("Weather content", weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);
                JSONObject obj;

                String message = "";
                String main = "";
                String description = "";
                /* loops through the data and finds the keywords for main and description to be
                   displayed to the user */
                for(int i = 0; i < arr.length(); i++)
                {
                    obj = arr.getJSONObject(i);

                    main = obj.getString("main");
                    description = obj.getString("description");

                    if(!main.equals("") && !description.equals(""))
                    {
                        message += main + ": " + description + "\r\n";
                    }
                    Log.i("message", message);
                    Log.i("description", description);
                }

                // sanity check
                if(!message.equals(""))
                {
                    resultTextView.setText(message);
                }else
                    {
                    Toast.makeText(getApplicationContext(), "Couldn't find weather for the given location.", Toast.LENGTH_SHORT).show();
                }
            }catch(Exception e)
            {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Couldn't find weather for the given location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
