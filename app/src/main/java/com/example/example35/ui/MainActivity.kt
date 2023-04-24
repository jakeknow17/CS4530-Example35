package com.example.example35.ui

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.TextView
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.example35.*
import com.example.example35.database.weather.WeatherTable
import com.example.example35.repository.WeatherViewModel
import com.example.example35.repository.WeatherViewModelFactory
import com.example.example35.utils.JSONWeatherUtils
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    // Initialize the view model here. One per activity.
    // While initializing, we'll also inject the repository.
    // However, standard view model constructor only takes a context to
    // the activity. We'll need to define our own constructor, but this
    // requires writing our own view model factory.
    private val mWeatherViewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory((application as WeatherApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainActivityScreen()
        }
    }

    @Composable
    fun MainActivityScreen() {
        var location by remember { mutableStateOf(TextFieldValue()) }
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Enter location:")
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = {
                    val tempLocation = location.text.replace(' ', '&')
                    loadWeatherData(tempLocation)
                }) {
                    Text(text = "Submit")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            val currentWeather by mWeatherViewModel.data.observeAsState()
            if (currentWeather != null) {
                Text(text = "Temperature: " + (currentWeather!!.temperature.temp - 273.15).roundToInt() + " C")
                Text(text = "Pressure: " + currentWeather!!.currentCondition.pressure + " hPa")
                Text(text = "Humidity: " + currentWeather!!.currentCondition.humidity + "%")
                Text(text = "Wind speed: " + currentWeather!!.wind.speed + "m/s")
                Text(text = "Wind degrees: " + currentWeather!!.wind.deg)
            }

            val weatherTableList by mWeatherViewModel.allCityWeather.observeAsState()
            if (weatherTableList != null) {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(weatherTableList!!) { item ->
                        WeatherListItem(weatherItem = item)
                    }
                }
            }

        }
    }

    @Composable
    fun WeatherListItem(weatherItem: WeatherTable) {
        Row(modifier = Modifier.padding(16.dp)) {
            Text(text = weatherItem.location)
            Text(text = "" + (JSONWeatherUtils.getWeatherData(weatherItem.weatherJson).temperature.temp - 273.15).roundToInt() + "C")
        }
    }

    private fun loadWeatherData(location: String?) {
        //pass the location in to the view model
        mWeatherViewModel.setLocation(location!!)
    }
}