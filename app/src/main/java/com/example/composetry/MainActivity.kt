package com.example.composetry


import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.example.composetry.ui.theme.ComposeTryTheme

typealias mod = Modifier

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    override fun onStart() {
        super.onStart()
        MyService.startAdbService(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult: $requestCode ${permissions.contentToString()} ${grantResults.contentToString()}")
    }
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(this, arrayOf("android.permission.WRITE_SECURE_SETTINGS", "android.permission.MANAGE_DEBUGGING"), 111)
        val runningProcess = "cat /proc/net/unix "
        val process = Runtime.getRuntime().exec(runningProcess)
        val inputStream = process.inputStream
        val bufferedReader = inputStream.bufferedReader()
        val line = bufferedReader.readText()
        val errorStream = process.errorStream
        val errorBufferedReader = errorStream.bufferedReader()
        val errorLine = errorBufferedReader.readText()
        Log.d(TAG, "onCreate error: $errorLine")
        Log.d(TAG, "onCreate success: $line")

        Log.d(TAG, "onCreate ${Settings.Secure.getUriFor("adb_port")}")
        Log.d(TAG, "onCreate ${Settings.Secure.getUriFor(Settings.Secure.ADB_ENABLED)}")
        try {
            Log.d(TAG, "onCreate: ${Settings.Secure.getInt(contentResolver, "adb_port", -10)}")
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        try{
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
fun Greeting(name: String?) {
    Text(text = "Hello $name!", style = MaterialTheme.typography.h3)
}

@Composable
fun VerticalScrollView(content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        content()
    }
//    Column(modifier = Modifier.verticalScroll(state = rememberScrollState())) {
//        content()
//    }
}

//@OptIn(ExperimentalMaterialApi::class)
@Preview(
    showBackground = true, showSystemUi = true,
    device = "spec:shape=Normal,width=1080,height=2340,unit=px,dpi=480"
)


@Composable
fun DefaultPreview() {
    val c = LocalContext.current
//    val dr = c.getDrawable(R.drawable.ic_launcher_background)!!
//    dr.setTint(Color.Yellow.toArgb())
//    val bitmap = Bitmap.createBitmap(dr.intrinsicWidth, dr.intrinsicHeight, Bitmap.Config.RGB_565)
//    val canvas = Canvas(bitmap)
//    dr.setBounds(0, 0, dr.intrinsicWidth, dr.intrinsicHeight)
//    dr.draw(canvas)

    val liveData = MutableLiveData<String>()
    ComposeTryTheme {


//        VerticalScrollView {
//            Row(
//                modifier = Modifier
//                    .horizontalScroll(state = rememberScrollState())
//                    .fillMaxWidth()
//            ) {
//                for (i in 0..10) Text(text = "Hello $i",
//                    style = MaterialTheme.typography.h3,
//                    modifier = Modifier
//                        .padding(start = 16.dp, end = 16.dp)
//                        .clickable {
//                            Toast
//                                .makeText(c, "Hello $i", Toast.LENGTH_LONG)
//                                .show()
//                        })
//            }
//            Greeting(name = "Nas")
//            Spacer(
//                modifier = Modifier
//                    .height(30.dp)
//                    .background(Color.Black)
//                    .fillMaxSize()
//            )

//            //for (i in 0..20) Text(text = "Hello $i", style = MaterialTheme.typography.h3)
//            Spacer(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .height(50.dp)
//            )
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