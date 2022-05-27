package com.example.composetry


import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetry.ui.theme.ComposeTryTheme


typealias mod = Modifier

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    override fun onStart() {
        super.onStart()
        MyService.startAdbService(this)
    }

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        try {
//            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
//            pm.reboot(null)
//        } catch (e: Exception) {
//            Log.e(TAG, "Failed to reboot", e)
//            e.printStackTrace()
//        }

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

@Composable
fun VerticalScrollView(content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.verticalScroll(state = rememberScrollState())) {
        content()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(
    showBackground = true, showSystemUi = true,
    device = "spec:shape=Normal,width=1080,height=2340,unit=px,dpi=480"
)
@Composable
fun DefaultPreview() {
    val c = LocalContext.current
    val dr = c.getDrawable(R.drawable.ic_launcher_background)!!
    //dr.setTint(Color.Yellow.toArgb())
    val bitmap = Bitmap.createBitmap(dr.intrinsicWidth, dr.intrinsicHeight, Bitmap.Config.RGB_565)
    val canvas = Canvas(bitmap)
    dr.setBounds(0, 0, dr.intrinsicWidth, dr.intrinsicHeight)
    dr.draw(canvas)

    ComposeTryTheme {
        VerticalScrollView {
            Row(
                modifier = Modifier
                    .horizontalScroll(state = rememberScrollState())
                    .fillMaxWidth()
            ) {
                for (i in 0..10) Text(text = "Hello $i",
                    style = MaterialTheme.typography.h3,
                    modifier = Modifier
                        .clickable {
                            Toast
                                .makeText(c, "Hello $i", Toast.LENGTH_LONG)
                                .show()
                        }
                        .padding(start = 16.dp, end = 16.dp))
            }
            Greeting(name = "as")
            Spacer(
                modifier = Modifier
                    .height(30.dp)
                    .background(Color.Black)
                    .fillMaxSize()
            )
            //for (i in 0..20) Text(text = "Hello $i", style = MaterialTheme.typography.h3)
            Card(onClick = { /*TODO*/ }, modifier = mod.padding(6.dp), contentColor = Color.Cyan) {
                Row {
                    Text(text = "Hello", mod.padding(16.dp))
                    Image(
                        bitmap.asImageBitmap(), null, modifier =
                        Modifier.size(100.dp, 100.dp)
                    )
                }
            }
        }
    }
}

//        LazyColumn(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.spacedBy(16.dp),
//) {
//            item {
//                Button(onClick = {
//                    state++
//                }) {
//                    Text("Click me! $state")
//                }
//            }
//            items(listOf("A", "B", "C","B", "C", "as")) {
//                Greeting(it)
//            }
//            items(10){
//                Greeting("Android $it")
//            }
//            for (i in 1..10) {
//                item { Greeting(name = "Android $i") }
//            }