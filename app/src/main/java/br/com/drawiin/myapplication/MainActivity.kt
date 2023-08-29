package br.com.drawiin.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import br.com.drawiin.myapplication.data.Result
import br.com.drawiin.myapplication.data.Service
import br.com.drawiin.myapplication.ui.theme.MyApplicationTheme
import coil.compose.AsyncImage
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    private lateinit var service: Service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        service = Retrofit.Builder()
            .baseUrl("https://rickandmortyapi.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Service::class.java)

        setContent {
            MyApplicationTheme {
                var charactersStateList = remember {
                    mutableStateListOf<Result>()
                }
                var isDetailsBeingShown = remember {
                    mutableStateOf(false)
                }
                var character = remember {
                    mutableStateOf<Result?>(null)
                }

                LaunchedEffect(keys = emptyArray()) {
                    kotlin.runCatching {
                        service.getCharacters()
                    }
                        .onSuccess {
                            charactersStateList.addAll(it.results)
                        }
                        .onFailure {
                            it.printStackTrace()
                        }
                }
                LazyColumn {
                    items(charactersStateList.toList()) { result ->
                        Text(text = result.name, modifier = Modifier
                            .padding(16.dp)
                            .clickable {
                                character.value = result
                                isDetailsBeingShown.value = true
                            }
                        )
                    }
                }

                if (isDetailsBeingShown.value) {
                    Dialog(
                        onDismissRequest = { isDetailsBeingShown.value = false }
                    ) {
                        character.value?.let {
                            DetailsScreen(character = it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailsScreen(character: Result) {
    Surface {
        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp)) {
            AsyncImage(
                model = character.image,
                contentDescription = null
            )
            Text(text = "Name: ${character.name}", style = MaterialTheme.typography.headlineMedium)
            Text(text = "Status: ${character.status}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Origin: ${character.origin}", style = MaterialTheme.typography.bodyMedium)
        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}