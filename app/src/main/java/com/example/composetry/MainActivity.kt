package com.example.composetry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetry.ui.theme.ComposeTryTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeTryTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!", style = MaterialTheme.typography.h3)
}

@Preview(
    showBackground = true, showSystemUi = true,
    device = "spec:shape=Normal,width=1080,height=2340,unit=px,dpi=480"
)
@Composable
fun DefaultPreview() {
    val state = remember { mutableStateOf(0) }
    ComposeTryTheme {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Button(onClick = {
                    state.value++
                }) {
                    Text("Click me! ${state.value}")
                }
            }
            items(listOf("A", "B", "C")) {
                Greeting(it)
            }
//            items(10){
//                Greeting("Android $it")
//            }
//            for (i in 1..10) {
//                item { Greeting(name = "Android $i") }
//            }
        }
    }
}