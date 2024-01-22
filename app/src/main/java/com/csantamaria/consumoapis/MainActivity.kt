package com.csantamaria.consumoapis

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    private lateinit var etEAN: EditText
    private lateinit var btnSearch: Button
    private lateinit var btnScan: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var rvResult: RecyclerView

    private lateinit var retrofit: Retrofit

    private lateinit var adapter: ProductAdapter

    private lateinit var barcodeLauncher: ActivityResultLauncher<ScanOptions>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        retrofit = getRetrofit()

        initUI()

    }

    private fun initUI() {

        etEAN = findViewById(R.id.etEAN)
        btnSearch = findViewById(R.id.btnSearch)
        btnScan = findViewById(R.id.btnScan)
        progressBar = findViewById(R.id.progressBar)
        rvResult = findViewById(R.id.rvResult)

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

        adapter = ProductAdapter ()
        rvResult.setHasFixedSize(true)
        rvResult.layoutManager = LinearLayoutManager(this)
        rvResult.adapter = adapter

        btnScan.setOnClickListener {
            etEAN.text = null

            val options = ScanOptions()
            options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
            options.setPrompt("Enfoca el código de barras a leer")
            options.setBeepEnabled(false)
            options.setBarcodeImageEnabled(true)
            options.setOrientationLocked(true)
            barcodeLauncher.launch(options)
        }

        btnSearch.setOnClickListener {
            hideKeyboard()
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

    private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun searchByName(query: String) {

        progressBar.isVisible = true

        CoroutineScope(Dispatchers.IO).launch {
            val myResponse: Response<ProductDataResponse> =
                retrofit.create(ApiService::class.java).getProductInfo(query)

            if (myResponse.isSuccessful) {
                Log.i("Consulta", "Funciona :)")
                val response: ProductDataResponse? = myResponse.body()
                if (response != null) {
                    Log.i("Cuerpo de la consulta", response.toString())
                    runOnUiThread {
                        adapter.updateList(response)
                        progressBar.isVisible = false
                    }
                }
            } else {
                Log.i("Consulta", "No funciona :(")
            }

        }

    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

}