package com.csantamaria.consumoapis

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration

class MainActivity : AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var adapter: ProductAdapter
    private lateinit var barcodeLauncher: ActivityResultLauncher<ScanOptions>

    private lateinit var productDataResponse: ProductDataResponse

    private lateinit var etEAN: EditText
    private lateinit var btnSearch: Button
    private lateinit var btnScan: Button
    private lateinit var rvResult: RecyclerView

    private lateinit var ivNutriscore: ImageView

    private lateinit var cvFatsLow: CardView
    private lateinit var cvFatsMedium: CardView
    private lateinit var cvFatsHigh: CardView

    private lateinit var cvSaturatedFatsLow: CardView
    private lateinit var cvSaturatedFatsMedium: CardView
    private lateinit var cvSaturatedFatsHigh: CardView

    private lateinit var cvSaltLow: CardView
    private lateinit var cvSaltMedium: CardView
    private lateinit var cvSaltHigh: CardView

    private lateinit var cvSugarLow: CardView
    private lateinit var cvSugarMedium: CardView
    private lateinit var cvSugarHigh: CardView

    private lateinit var progressBar: ProgressBar

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

        rvResult = findViewById(R.id.rvResult)

        ivNutriscore = findViewById(R.id.ivNutriscore)

        progressBar = findViewById(R.id.progressBar)

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

        adapter = ProductAdapter()
        rvResult.setHasFixedSize(true)
        rvResult.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
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
            val response: Response<ProductDataResponse> =
                retrofit.create(ApiService::class.java).getProductInfo(query)
            if (response.isSuccessful) {
                productDataResponse = response.body()!!
                if (productDataResponse.status == 1) {
                    runOnUiThread {
                        adapter.updateList(productDataResponse)
                        updateNutriscore()
                        updateNutrientValues()
                        progressBar.isVisible = false
                    }
                } else {
                    runOnUiThread {
                        progressBar.isVisible = false
                        Toast.makeText(
                            applicationContext,
                            "Producto no encontrado",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                runOnUiThread {
                    progressBar.isVisible = false
                    Toast.makeText(applicationContext, "Producto no encontrado", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun updateNutriscore() {
        when (productDataResponse.productValues.nutriscoreGrade) {
            "a" -> {
                ivNutriscore.setImageResource(R.drawable.nutriscore_a)
            }

            "b" -> {
                ivNutriscore.setImageResource(R.drawable.nutriscore_b)
            }

            "c" -> {
                ivNutriscore.setImageResource(R.drawable.nutriscore_c)
            }

            "d" -> {
                ivNutriscore.setImageResource(R.drawable.nutriscore_d)
            }

            "e" -> {
                ivNutriscore.setImageResource(R.drawable.nutriscore_e)
            }
        }
    }

    private fun updateNutrientValues() {
        TODO("Not yet implemented")
    }

}