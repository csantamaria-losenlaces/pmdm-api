package com.csantamaria.consumoapis

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
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
    private lateinit var cvFatsModerate: CardView
    private lateinit var cvFatsHigh: CardView

    private lateinit var cvSaturatedFatsLow: CardView
    private lateinit var cvSaturatedFatsModerate: CardView
    private lateinit var cvSaturatedFatsHigh: CardView

    private lateinit var cvSaltLow: CardView
    private lateinit var cvSaltModerate: CardView
    private lateinit var cvSaltHigh: CardView

    private lateinit var cvSugarLow: CardView
    private lateinit var cvSugarModerate: CardView
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

        cvFatsLow = findViewById(R.id.cvFatsLow)
        cvFatsModerate = findViewById(R.id.cvFatsModerate)
        cvFatsHigh = findViewById(R.id.cvFatsHigh)

        cvSaturatedFatsLow = findViewById(R.id.cvSaturatedFatsLow)
        cvSaturatedFatsModerate = findViewById(R.id.cvSaturatedFatsModerate)
        cvSaturatedFatsHigh = findViewById(R.id.cvSaturatedFatsHigh)

        cvSaltLow = findViewById(R.id.cvSaltLow)
        cvSaltModerate = findViewById(R.id.cvSaltModerate)
        cvSaltHigh = findViewById(R.id.cvSaltHigh)

        cvSugarLow = findViewById(R.id.cvSugarLow)
        cvSugarModerate = findViewById(R.id.cvSugarModerate)
        cvSugarHigh = findViewById(R.id.cvSugarHigh)

        progressBar = findViewById(R.id.progressBar)

        btnSearch.isEnabled = false
        btnSearch.isClickable = false

        barcodeLauncher = registerForActivityResult(
            ScanContract()
        ) { result: ScanIntentResult ->
            if (result.contents == null) {
                Toast.makeText(this, "Se ha cancelado el escaneo", Toast.LENGTH_LONG).show()
            } else {
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
        hideKeyboard()
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
                        resetNutrientBars()
                        updateNutrientBars()
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
                    Toast.makeText(
                        applicationContext,
                        "Producto no encontrado",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun updateNutriscore() {
        if (!productDataResponse.productValues.nutriscoreGrade.isNullOrEmpty()) {
            when (productDataResponse.productValues.nutriscoreGrade) {
                "a" -> ivNutriscore.setImageResource(R.drawable.nutriscore_a)
                "b" -> ivNutriscore.setImageResource(R.drawable.nutriscore_b)
                "c" -> ivNutriscore.setImageResource(R.drawable.nutriscore_c)
                "d" -> ivNutriscore.setImageResource(R.drawable.nutriscore_d)
                "e" -> ivNutriscore.setImageResource(R.drawable.nutriscore_e)
                else -> ivNutriscore.setImageResource(R.drawable.nutriscore_disabled)
            }
        }
    }

    private fun updateNutrientBars() {
        // Fat
        if (!productDataResponse.productValues.nutrientsValues.fat.isNullOrEmpty()) {
            when (productDataResponse.productValues.nutrientsValues.fat) {
                "low" -> {
                    cvFatsLow.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.low
                        )
                    )
                }

                "moderate" -> {
                    cvFatsLow.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.low
                        )
                    )
                    cvFatsModerate.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.moderate
                        )
                    )
                }

                "high" -> {
                    cvFatsLow.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.low
                        )
                    )
                    cvFatsModerate.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.moderate
                        )
                    )
                    cvFatsHigh.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.high
                        )
                    )
                }
            }
        }
        // Saturated fat
        if (!productDataResponse.productValues.nutrientsValues.saturatedFat.isNullOrEmpty()) {
            when (productDataResponse.productValues.nutrientsValues.saturatedFat) {
                "low" -> {
                    cvSaturatedFatsLow.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.low
                        )
                    )
                }

                "moderate" -> {
                    cvSaturatedFatsLow.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.low
                        )
                    )
                    cvSaturatedFatsModerate.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.moderate
                        )
                    )
                }

                "high" -> {
                    cvSaturatedFatsLow.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.low
                        )
                    )
                    cvSaturatedFatsModerate.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.moderate
                        )
                    )
                    cvSaturatedFatsHigh.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.high
                        )
                    )
                }
            }
        }
        // Salt
        if (!productDataResponse.productValues.nutrientsValues.salt.isNullOrEmpty()) {
            when (productDataResponse.productValues.nutrientsValues.salt) {
                "low" -> {
                    cvSaltLow.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.low
                        )
                    )
                }

                "moderate" -> {
                    cvSaltLow.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.low
                        )
                    )
                    cvSaltModerate.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.moderate
                        )
                    )
                }

                "high" -> {
                    cvSaltLow.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.low
                        )
                    )
                    cvSaltModerate.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.moderate
                        )
                    )
                    cvSaltHigh.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.high
                        )
                    )
                }
            }
        }
        // Sugar
        if (!productDataResponse.productValues.nutrientsValues.sugar.isNullOrEmpty()) {
            when (productDataResponse.productValues.nutrientsValues.sugar) {
                "low" -> {
                    cvSugarLow.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.low
                        )
                    )
                }

                "moderate" -> {
                    cvSugarLow.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.low
                        )
                    )
                    cvSugarModerate.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.moderate
                        )
                    )
                }

                "high" -> {
                    cvSugarLow.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.low
                        )
                    )
                    cvSugarModerate.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.moderate
                        )
                    )
                    cvSugarHigh.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.high
                        )
                    )
                }
            }
        }
    }

    private fun resetNutrientBars() {
        // Fat
        cvFatsLow.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.gray))
        cvFatsModerate.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.gray))
        cvFatsHigh.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.gray))

        // Saturated fat
        cvSaturatedFatsLow.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.gray))
        cvSaturatedFatsModerate.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.gray))
        cvSaturatedFatsHigh.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.gray))

        // Salt
        cvSaltLow.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.gray))
        cvSaltModerate.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.gray))
        cvSaltHigh.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.gray))

        // Sugar
        cvSugarLow.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.gray))
        cvSugarModerate.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.gray))
        cvSugarHigh.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.gray))
    }

}