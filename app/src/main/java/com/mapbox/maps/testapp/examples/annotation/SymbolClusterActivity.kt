package com.mapbox.maps.testapp.examples.annotation

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationSourceOptions
import com.mapbox.maps.plugin.annotation.ClusterOptions
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.annotation.getAnnotationPlugin
import com.mapbox.maps.testapp.R
import kotlinx.android.synthetic.main.activity_add_marker_symbol.*
import kotlinx.android.synthetic.main.activity_add_marker_symbol.mapView
import kotlinx.android.synthetic.main.activity_annotation.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

/**
 * Example showing how to add Symbol annotations
 */
class SymbolClusterActivity : AppCompatActivity(), CoroutineScope {
  private var mapboxMap: MapboxMap? = null
  private val job = Job()
  override val coroutineContext = job + Dispatchers.IO
  private var symbolManager: SymbolManager? = null
  private var options: List<SymbolOptions>? = null
  private var index: Int = 0
  private val nextStyle: String
    get() {
      return AnnotationUtils.STYLES[index++ % AnnotationUtils.STYLES.size]
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_annotation)
    mapboxMap = mapView.getMapboxMap()
      .apply {
        jumpTo(
          CameraOptions.Builder()
            .center(Point.fromLngLat(LONGITUDE, LATITUDE))
            .zoom(10.0)
            .build()
        )
        loadStyleUri(nextStyle) {
          launch {
            loadData()
          }
          val annotationPlugin = mapView.getAnnotationPlugin()
          val annotationConfig = AnnotationConfig(
            annotationSourceOptions = AnnotationSourceOptions(
              clusterOptions = ClusterOptions(
                cluster = true,
                colorLevels = listOf(
                  Pair(100, Color.RED),
                  Pair(50, Color.BLUE),
                  Pair(0, Color.GREEN)
                )
              )
            )
          )
          symbolManager = annotationPlugin.createSymbolManager(mapView, annotationConfig)
        }
      }

    deleteAll.setOnClickListener { symbolManager?.deleteAll() }
    changeStyle.setOnClickListener {
      mapView.getMapboxMap().loadStyleUri(nextStyle)
    }
  }

  private fun loadData() {
    if (options == null) {
      AnnotationUtils.loadStringFromAssets(
        this@SymbolClusterActivity,
        "points.geojson"
      )?.let {
        FeatureCollection.fromJson(it).features()?.let { features ->
          features.shuffle()
          options = features.take(AMOUNT).map { feature ->
            SymbolOptions()
              .withGeometry((feature.geometry() as Point))
              .withIconImage(ICON_FIRE_STATION)
          }
        }
      }
    }
    symbolManager?.deleteAll()
    options?.let {
      symbolManager?.create(it)
    }

  }

  override fun onStart() {
    super.onStart()
    mapView.onStart()
  }

  override fun onStop() {
    super.onStop()
    mapView.onStop()
  }

  override fun onLowMemory() {
    super.onLowMemory()
    mapView.onLowMemory()
  }

  override fun onDestroy() {
    super.onDestroy()
    mapView.onDestroy()
  }

  companion object {
    private const val AMOUNT = 10000
    private const val ICON_FIRE_STATION = "fire-station-11"
    private const val LONGITUDE = -77.00897
    private const val LATITUDE = 38.87031
  }
}