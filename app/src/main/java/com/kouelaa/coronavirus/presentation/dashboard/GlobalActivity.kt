package com.kouelaa.coronavirus.presentation.dashboard


import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.kouelaa.coronavirus.R
import com.kouelaa.coronavirus.domain.entities.*
import com.kouelaa.coronavirus.presentation.about.AboutActivity
import kotlinx.android.synthetic.main.activity_global.*
import kotlinx.android.synthetic.main.country_linechart_item.*
import kotlinx.android.synthetic.main.global_linechart_item.*
import kotlinx.android.synthetic.main.global_piechart_item.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class GlobalActivity : AppCompatActivity(){

    private val globalViewModel: GlobalViewModel by viewModel()

    private lateinit var countryAdapter: CountryAdapter
    private lateinit var countryLayoutManager: LinearLayoutManager

    private lateinit var outAnimator: AnimatorSet
    private lateinit var inAnimator: AnimatorSet
    private var isChartBackVisible = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_global)

        initGlobalPieChart()
        initGlobalLineChart()
        initCoutryLineChart()
        initToolbar()

        loadAnimations()
        changeCameraDistance()
    }

    override fun onStart() {
        super.onStart()

        globalViewModel.global.observe(this, Observer {
            setPieChartData(it.toGlobalChart())
            setPieChartLabels(it)
            setGlobalLineChartData(it.globalData)
            setCountriesData(it.coutriesData)

            // Display first country data
            globalViewModel.onClickedCountry("Chine")
        })

        globalViewModel.countryData.observe(this, Observer {
            country_item_country_tv.text = it.country
            setCountriesLineChartDate(it.values)
        })
    }

    private fun changeCameraDistance() {
        val distance = 8000
        val scale = resources.displayMetrics.density * distance
        container_global_piechart.cameraDistance = scale
        container_global_linechart.cameraDistance = scale
    }

    private fun loadAnimations() {
        outAnimator = AnimatorInflater.loadAnimator(this, R.animator.out_animation) as AnimatorSet
        inAnimator = AnimatorInflater.loadAnimator(this, R.animator.in_animation) as AnimatorSet
    }

    private fun animateCard() {
        if (!inAnimator.isRunning && !outAnimator.isRunning) {
            if (!isChartBackVisible) {
                outAnimator.setTarget(container_global_piechart)
                inAnimator.setTarget(container_global_linechart)
                outAnimator.start()
                inAnimator.start()
                isChartBackVisible = true

            } else {
                outAnimator.setTarget(container_global_linechart)
                inAnimator.setTarget(container_global_piechart)
                outAnimator.start()
                inAnimator.start()
                isChartBackVisible = false
            }
        }
    }

    private fun initToolbar() {
        about_toolbar.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

    private fun initGlobalPieChart() {
        global_piechart.setParams()
        container_global_piechart.setOnClickListener{
            animateCard()
        }
    }

    private fun initGlobalLineChart(){
        global_linechart.setParams()
    }

    private fun initCoutryLineChart(){
        country_linechart.setParams()
    }

    private fun setPieChartData(values: List<GlobalChartValue>) {
        val xValues = ArrayList<PieEntry>()

        values.forEach {
            xValues.add(PieEntry(it.value))
        }
        val dataSet = PieDataSet(xValues, null)
        val sliceColors = mutableListOf<Int>()
        sliceColors.addAll(listOf(
            ContextCompat.getColor(this, R.color.colorDeath),
            ContextCompat.getColor(this, R.color.colorRecovered),
            ContextCompat.getColor(this, R.color.colorStillSick)
        ))
        dataSet.apply {
            setDrawValues(true)
            valueFormatter = PieChartValueFormatter()
            valueTextSize = 8f
            valueTextColor = ContextCompat.getColor(this@GlobalActivity, R.color.colorBackgroundLight)
            colors = sliceColors
        }
        val pieData = PieData(dataSet)
        global_piechart.data = pieData

        global_piechart.animateXY(2000, 2000)
    }

    private fun setPieChartLabels(global: Global) {
        global_piechart.centerText = "${getString(R.string.confirmed)} \n ${global.globalData[0].confirmed.toInt()}"

        global.toGlobalCards().forEach {
            when(it.label){
                GlobalTypeEnum.CONFIRMED -> Unit
                GlobalTypeEnum.RECOVERED -> recovered_tv.text = getString(R.string.recovered) + "\n"+ it.value.toInt().toString()
                GlobalTypeEnum.DEATHS -> death_tv.text = getString(R.string.deaths)+ "\n" + it.value.toInt().toString()
                GlobalTypeEnum.STILL_SICK -> still_sick_tv.text = getString(R.string.still_sick)+ "\n" + it.value.toInt().toString()
            }
        }
    }

    private fun setGlobalLineChartData(values: List<GlobalData>){
        val lastValue = values[0]
        val valuesChart = values.reversed()

        global_item_date_tv.text = lastValue.date.toChartLabelDate()
        global_item_confirmed_tv.text = lastValue.confirmed.toInt().toString()
        global_item_death_tv.text = lastValue.deaths.toInt().toString()
        global_item_recovered_tv.text = lastValue.recovered.toInt().toString()

        val entriesConfirmed = ArrayList<Entry>()
        val entriesRecovered = ArrayList<Entry>()
        val entriesDeaths = ArrayList<Entry>()

        valuesChart.forEachIndexed {index, element ->
            entriesConfirmed.add(Entry(index.toFloat(), element.confirmed.toFloat()))
            entriesRecovered.add(Entry(index.toFloat(), element.recovered.toFloat()))
            entriesDeaths.add(Entry(index.toFloat(), element.deaths.toFloat()))
        }

        val lineDataSetConfirmed = LineDataSet(entriesConfirmed, "")
        val lineDataSetRecovered = LineDataSet(entriesRecovered, "")
        val lineDataSetDeaths = LineDataSet(entriesDeaths, "")

        lineDataSetConfirmed.setParams(this, R.color.colorConfirmed)
        lineDataSetRecovered.setParams(this, R.color.colorRecovered)
        lineDataSetDeaths.setParams(this, R.color.colorDeath)

        val lineData = LineData()
        lineData.addDataSet(lineDataSetConfirmed)
        lineData.addDataSet(lineDataSetRecovered)
        lineData.addDataSet(lineDataSetDeaths)
        global_linechart.xAxis.valueFormatter = LineChartLabelFormatter(valuesChart)
        global_linechart.data = lineData
        global_linechart.animateXY(1000, 1000)
    }

    private fun setCountriesLineChartDate(values: List<CountryValue>) {

        val lastValue = values[values.size-1]
        country_item_date_tv.text = lastValue.date.toChartLabelDate()
        country_item_confirmed_tv.text = lastValue.confirmed.toInt().toString()
        country_item_death_tv.text = lastValue.death.toInt().toString()
        country_item_recovered_tv.text = lastValue.recovered.toInt().toString()

        val entriesConfirmed = ArrayList<Entry>()
        val entriesRecovered = ArrayList<Entry>()
        val entriesDeaths = ArrayList<Entry>()

        values.forEachIndexed { index, element ->
            entriesConfirmed.add(Entry(index.toFloat(), element.confirmed.toFloat(), element))
            entriesRecovered.add(Entry(index.toFloat(), element.recovered.toFloat(), element))
            entriesDeaths.add(Entry(index.toFloat(), element.death.toFloat(), element))
        }

        val lineDataSetConfirmed = LineDataSet(entriesConfirmed, "")
        val lineDataSetRecovered = LineDataSet(entriesRecovered, "")
        val lineDataSetDeaths = LineDataSet(entriesDeaths, "")

        lineDataSetConfirmed.setParams(this, R.color.colorConfirmed)
        lineDataSetRecovered.setParams(this, R.color.colorRecovered)
        lineDataSetDeaths.setParams(this, R.color.colorDeath)

        val lineData = LineData(lineDataSetConfirmed, lineDataSetRecovered, lineDataSetDeaths)

        country_linechart.xAxis.valueFormatter = LineChartCountryLabelFormatter(values)
        country_linechart.data = lineData

        country_linechart.highlightValue((values.size-1).toFloat(), 0, true)
        country_linechart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {}

            override fun onValueSelected(entry: Entry?, highlight: Highlight?) {
                val data  = entry?.data as? CountryValue?
                country_item_date_tv.text = data?.date?.toChartLabelDate() ?: ""
                country_item_confirmed_tv.text = data?.confirmed?.toInt().toString()
                country_item_death_tv.text = data?.death?.toInt().toString()
                country_item_recovered_tv.text = data?.recovered?.toInt().toString()
            }
        })
        country_linechart.animateXY(1000, 1000)
    }

    private fun setCountriesData(countries: List<CountryData>) {
        val countriesForAdapter = globalViewModel.getCoutriesForAdapter(countries)
        countryAdapter = CountryAdapter(this, countriesForAdapter) {globalViewModel.onClickedCountry(it)}
        countryLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        countries_rv.layoutManager = countryLayoutManager
        countries_rv.adapter = countryAdapter
    }

}
