@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.kouelaa.evolutionc19.presentation.dashboard


import android.content.Context
import androidx.core.content.ContextCompat.*
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.kouelaa.evolutionc19.R
import com.kouelaa.evolutionc19.domain.entities.CountryValue
import com.kouelaa.evolutionc19.domain.entities.GlobalData
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by kheirus on 15/03/2020.
 */

class PieChartValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "%.1f".format(value).toString() + "%"
    }
}

class LineChartLabelFormatter(private val data: List<GlobalData>) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return data[value.toInt()].date.toChartLabelDate()
    }
}

class LineChartCountryLabelFormatter(private val data: List<CountryValue>) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return if (value < data.size) {
            data[value.toInt()].date.toChartLabelDate()
        } else {
            data[data.size-1].date.toChartLabelDate()
        }
    }
}

fun String.toChartLabelDate(): String{
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.FRANCE)
    val formatter = SimpleDateFormat("dd MMM", Locale.FRANCE)
    return formatter.format(parser.parse(this))

}

fun String.toExtraChartLabelDate(): String{
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.FRANCE)
    val formatter = SimpleDateFormat("dd MMM yy", Locale.FRANCE)
    return formatter.format(parser.parse(this))

}

fun LineDataSet.setParams(context: Context, colorSet: Int) {
    setDrawValues(false)
    axisDependency = YAxis.AxisDependency.RIGHT
    lineWidth = 2f
    color = getColor(context, colorSet)
    mode = LineDataSet.Mode.CUBIC_BEZIER
    isHighlightEnabled = true
    setDrawCircleHole(false)
    setDrawCircles(false)
    setDrawHighlightIndicators(true)
    setDrawHorizontalHighlightIndicator(false)
    setDrawVerticalHighlightIndicator(true)
}

fun LineChart.setParams(){
    setDrawBorders(false)
    setDrawGridBackground(false)
    setPinchZoom(false)
    setScaleEnabled(false)
    isClickable = false
    legend.isEnabled = true
    legend.textColor = getColor(context, R.color.colorConfirmed)
    description = null
    legend.isEnabled = false

    xAxis.apply {
        setDrawGridLinesBehindData(false)
        setDrawLabels(true)
        setDrawAxisLine(true)
        setDrawGridLines(false)
        setScaleEnabled(false)
        axisLineWidth = 2f
        position = XAxis.XAxisPosition.BOTTOM
        textSize = 7f
        textColor = getColor(context, R.color.colorConfirmed)
        setDrawAxisLine(true)
        labelRotationAngle = -45f
        axisLineColor = getColor(context, R.color.colorBackgroundLight)
    }

    axisLeft.apply {
        setDrawLabels(false)
        setDrawAxisLine(false)
        setDrawGridLines(false)
    }

    axisRight.apply {
        setDrawLabels(true)
        setDrawAxisLine(true)
        setDrawGridLines(false)
        axisLineWidth = 2f
        textColor = getColor(context, R.color.colorConfirmed)
        valueFormatter = LargeValueFormatter()
        axisLineColor = getColor(context, R.color.colorBackgroundLight)
    }
}

fun PieChart.setParams(){
    isRotationEnabled = false
    legend.isEnabled = false
    description.isEnabled = false
    setTouchEnabled(false)
    setUsePercentValues(true)
    isDrawHoleEnabled = true
    setHoleColor(getColor(context, R.color.colorBackground))
    setDrawCenterText(true)
    setCenterTextColor(getColor(context, R.color.colorConfirmed))
    setCenterTextSize(10f)
}

fun BarChart.setParams(){
    setDrawBarShadow(false)
    setFitBars(false)
    setDrawBorders(false)
    setDrawGridBackground(false)
    setDrawValueAboveBar(true)
    setPinchZoom(false)
    setTouchEnabled(true)
    setScaleEnabled(false)
    isClickable = false
    isHighlightFullBarEnabled = false
    isHighlightPerDragEnabled = false
    isHighlightPerTapEnabled = false
    isDoubleTapToZoomEnabled = false
    description = null
    legend.isEnabled = false

    xAxis.apply {
        setDrawGridLinesBehindData(false)
        setDrawLabels(false)
        setDrawAxisLine(false)
        setDrawGridLines(false)
    }

    axisLeft.apply {
        setDrawLabels(false)
        setDrawLabels(false)
        setDrawAxisLine(false)
        setDrawGridLines(false)
    }

    axisRight.apply {
        setDrawLabels(false)
        setDrawAxisLine(false)
        setDrawGridLines(false)
    }

}