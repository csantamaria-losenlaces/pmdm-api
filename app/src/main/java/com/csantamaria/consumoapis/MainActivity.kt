package com.csantamaria.consumoapis

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.csantamaria.consumoapis.databinding.ActivityResultBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    private lateinit var etEAN: EditText
    private lateinit var btnSearch: Button
    private lateinit var btnScan: Button

    private lateinit var retrofit: Retrofit // PASAR A LA OTRA PANTALLA

    private lateinit var barcodeLauncher: ActivityResultLauncher<ScanOptions>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        // binding.rvResult.adapter = ...
        // binding.progressBar.visibility = ...

        retrofit = getRetrofit() // PASAR A LA OTRA PANTALLA??

        initUI()

    }

    private fun initUI() {

        btnScan = findViewById(R.id.btnScan)
        btnSearch = findViewById(R.id.btnSearch)
        etEAN = findViewById(R.id.etEAN)

        btnSearch.isEnabled = false
        btnSearch.isClickable = false

        barcodeLauncher = registerForActivityResult(
            ScanContract()
        ) { result: ScanIntentResult ->
            if (result.contents == null) {
                Toast.makeText(this, "Se ha cancelado el escaneo", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    this,
                    "Texto leído: " + result.contents,
                    Toast.LENGTH_LONG
                ).show()
                etEAN.setText(result.contents)
                searchByName(etEAN.text.toString())
            }
        }

        btnScan.setOnClickListener {
            val options = ScanOptions()
            options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
            options.setPrompt("Enfoca el código de barras a leer")
            options.setBeepEnabled(false)
            options.setBarcodeImageEnabled(true)
            barcodeLauncher.launch(options)
        }

        btnSearch.setOnClickListener {
            searchByName(etEAN.text.toString())
        }

        etEAN.doAfterTextChanged {
            if (!etEAN.text.isNullOrEmpty()) {
                btnSearch.isEnabled = true
                btnSearch.isClickable = true
            } else {
                btnSearch.isEnabled = false
                btnSearch.isClickable = false
            }
        }

    }

    // PASAR A LA OTRA PANTALLA??
    private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // PASAR A LA OTRA PANTALLA??
    private fun searchByName(query: String) {

        CoroutineScope(Dispatchers.IO).launch {
            val myResponse: Response<ProductDataResponse> =
                retrofit.create(ApiService::class.java).getProductInfo(query)

            if (myResponse.isSuccessful) {
                Log.i("Consulta", "Funciona :)")
            } else {
                Log.i("Consulta", "No funciona :(")
            }

        }

    }

}