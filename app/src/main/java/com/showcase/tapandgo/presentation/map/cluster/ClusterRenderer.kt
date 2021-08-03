package com.showcase.tapandgo.presentation.map.cluster

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.showcase.tapandgo.databinding.ItemMarkerBinding

class ClusterRenderer(
    private val context: Context,
    googleMap: GoogleMap,
    clusterManager: ClusterManager<MarkerClusterItem>,
    private val makeCluster: Boolean = true
) : DefaultClusterRenderer<MarkerClusterItem>(context, googleMap, clusterManager) {

    override fun shouldRenderAsCluster(cluster: Cluster<MarkerClusterItem>): Boolean {
        return makeCluster && cluster.size >= 3
    }

    override fun onBeforeClusterItemRendered(item: MarkerClusterItem, markerOptions: MarkerOptions) {
        markerOptions.icon(getIcon(item))
    }

    private fun getIcon(item: MarkerClusterItem): BitmapDescriptor {
        val height = 128
        val width = 128

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemBinding = ItemMarkerBinding.inflate(inflater, null, false)
        itemBinding.number.text = item.station?.mainStands?.availabilities?.bikes?.toString() ?: item.step

        // set the drawable on the marker
        val finalIcon = createBitmapFromView(itemBinding.root)
        return BitmapDescriptorFactory.fromBitmap(
            Bitmap.createScaledBitmap(
                finalIcon,
                width,
                height,
                false
            )
        )
    }

    private fun createBitmapFromView(v: View): Bitmap {
        v.layoutParams = ConstraintLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        v.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        v.layout(0, 0, v.measuredWidth, v.measuredHeight)
        val bitmap = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
        val c = Canvas(bitmap)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)
        return bitmap
    }
}
