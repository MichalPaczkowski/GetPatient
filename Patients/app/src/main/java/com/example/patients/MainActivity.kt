package com.example.patients

import android.R.attr.text
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.example.patients.ui.theme.PatientsTheme
import com.google.firebase.database.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = FirebaseDatabase.getInstance().reference

        setContent {
            PatientsTheme {
                FirebaseDataScreen(database)
            }
        }
    }
}

@Composable
fun FirebaseDataScreen(database: DatabaseReference) {
    var firebaseData by remember { mutableStateOf("Ładowanie danych...") }
    var route by remember { mutableStateOf("") }
    var patientID by remember {mutableStateOf("")}
    var patientIDg by remember {mutableStateOf("")}

    var ifClick by remember {mutableStateOf(false)}
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        ){
        OutlinedTextField(
            value = patientIDg,
            onValueChange = {patientIDg = it
                ifClick = false},
            label = {Text("ID pacjenta")}
        )
        Button(onClick = {ifClick = true
                            patientID = patientIDg
        }){
            Text("Pobierz pacjenta")
        }
        if(ifClick){
            LaunchedEffect(Unit) {
                database.child("Patient/$patientIDg").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val age = snapshot.child("age").getValue(String::class.java) ?: "Brak wieku"
                        val name = snapshot.child("name").getValue(String::class.java) ?: "Brak imienia"
                        val middleName = snapshot.child("middleName").getValue(String::class.java) ?: ""
                        val surename = snapshot.child("surename").getValue(String::class.java) ?: ""

                        firebaseData = "Pacjent:\n$name $middleName $surename,\nwiek: $age"
                    }

                    override fun onCancelled(error: DatabaseError) {
                        firebaseData = "Błąd odczytu: ${error.message}"
                        Log.e("Firebase", error.message)
                    }
                })
            }
            ShowData(dataToShow = firebaseData)

        }




    }



}

@Composable
fun ShowData(dataToShow: String) {
    Column {
        Text(text = dataToShow)
    }
}
