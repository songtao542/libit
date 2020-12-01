package com.liabit.location

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.amap.api.maps2d.CoordinateConverter
import com.amap.api.maps2d.model.LatLng
import com.amap.api.maps2d.model.MyLocationStyle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.liabit.location.databinding.MapLocationViewerFragmentBinding
import com.liabit.location.extension.checkAndRequestPermission
import com.liabit.location.extension.checkAppPermission
import com.liabit.location.extension.location_permissions
import com.liabit.location.model.Position
import com.liabit.viewbinding.bind

/**
 *
 */
class LocationViewer : BaseFragment(), Toolbar.OnMenuItemClickListener {

    companion object {
        const val EXTRA_TITLE = "TITLE"
        const val EXTRA_SUB_TITLE = "SUB_TITLE"
        const val EXTRA_LATITUDE = "LATITUDE"
        const val EXTRA_LONGITUDE = "LONGITUDE"

        private const val BAIDU_MAP = "com.baidu.BaiduMap"
        private const val A_MAP = "com.autonavi.minimap"
        private const val TENCENT_MAP = "com.tencent.map"

        @JvmStatic
        fun newInstance(title: String?, subTitle: String?, latitude: Double, longitude: Double) = LocationViewer().apply {
            this.arguments = Bundle().apply {
                putString(EXTRA_TITLE, title)
                putString(EXTRA_SUB_TITLE, subTitle)
                putDouble(EXTRA_LATITUDE, latitude)
                putDouble(EXTRA_LONGITUDE, longitude)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    private val viewModel: LocationViewModel? by lazy {
        context?.let {
            return@lazy LocationViewModel(it.applicationContext)
        }
    }

    private lateinit var mapProxy: MapProxy

    private var myLocationMarker: Marker? = null
    private var targetLocationMarker: Marker? = null
    private var targetLocationTitle: String? = null
    private var targetLocationSubTitle: String? = null
    private var targetLocationLatitude: Double = -1.0
    private var targetLocationLongitude: Double = -1.0

    private lateinit var adapter: LocationPickerRecyclerViewAdapter

    private var mapPickerDialog: BottomSheetDialog? = null

    private val binding by bind<MapLocationViewerFragmentBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.map_location_viewer_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //enableOptionsMenu(toolbar, false, R.menu.location_picker)
        //toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        //toolbar.setOnMenuItemClickListener(this)
        //titleTextView.setText(R.string.location_picker_title)

        binding.backButton.setOnClickListener { activity?.onBackPressed() }

        mapProxy = MapLocationFactory.create(requireContext(), mapView = binding.mapView)

        mapProxy.onCreate(savedInstanceState)

        val myLocationStyle = MyLocationStyle() //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
        myLocationStyle.showMyLocation(false)

        binding.mapView.map.setMyLocationStyle(myLocationStyle)
        binding.mapView.map.isMyLocationEnabled = true

        adapter = LocationPickerRecyclerViewAdapter()
        adapter.setOnItemClickListener {
            mapProxy.setCenter(it.location, 20f)
        }

        viewModel?.location?.observe(viewLifecycleOwner, {
            setMyLocation(Position(it), 16f)
        })

        binding.myLocationButton.setOnClickListener {
            getMyLocation()
        }

        arguments?.let {
            targetLocationTitle = it.getString(EXTRA_TITLE)
            targetLocationSubTitle = it.getString(EXTRA_SUB_TITLE)
            targetLocationLatitude = it.getDouble(EXTRA_LATITUDE, -1.0)
            targetLocationLongitude = it.getDouble(EXTRA_LONGITUDE, -1.0)
            if (targetLocationLatitude == -1.0 || targetLocationLongitude == -1.0) {
                activity?.finish()
            } else {
                targetLocationTitle?.let { title ->
                    binding.locationTitle.text = title
                }
                targetLocationSubTitle?.let { subTitle ->
                    binding.locationSubTitle.text = subTitle
                }

                setTargetLocation(Position(targetLocationLatitude, targetLocationLongitude),
                        16f)

                binding.openNavigation.setOnClickListener {
                    showMapPicker()
                }
            }
            return@let
        }
    }

    private fun getMyLocation() {
        if (checkAndRequestPermission(*location_permissions)) {
            viewModel?.getMyLocation()
        }
    }

    private fun showMapPicker() {
        if (mapPickerDialog != null || context == null) {
            return
        }
        context?.let {
            mapPickerDialog = BottomSheetDialog(it, R.style.MapPickerStyle)
            val view: View = LayoutInflater.from(it).inflate(R.layout.map_location_picker_menu_dialog, null)
            val cancel = view.findViewById<TextView>(R.id.cancel)
            cancel.setOnClickListener { mapPickerDialog?.dismiss() }
            val baiduMap = view.findViewById<TextView>(R.id.baiduMap)
            val tencentMap = view.findViewById<TextView>(R.id.tencentMap)
            val aMap = view.findViewById<TextView>(R.id.aMap)

            /*val baiduDivider = view.findViewById<View>(R.id.baiduDivider)
            val tencentDivider = view.findViewById<View>(R.id.tencentDivider)
            val aMapDivider = view.findViewById<View>(R.id.aMapDivider)
            if (!isMapAppInstalled(BAIDU_MAP)) {
                baiduMap.visibility = View.GONE
                baiduDivider.visibility = View.GONE
                tencentMap.setBackgroundResource(R.drawable.menu_button_top_selector)
            }
            if (!isMapAppInstalled(TENCENT_MAP)) {
                tencentMap.visibility = View.GONE
                tencentDivider.visibility = View.GONE
                aMap.setBackgroundResource(R.drawable.menu_button_top_selector)
            }
            if (!isMapAppInstalled(A_MAP)) {
                aMap.visibility = View.GONE
                aMapDivider.visibility = View.GONE
                Toast.makeText(it, R.string.not_find_map_app, Toast.LENGTH_SHORT).show()
                return
            }*/

            baiduMap.setOnClickListener { openNavigation(BAIDU_MAP) }
            tencentMap.setOnClickListener { openNavigation(TENCENT_MAP) }
            aMap.setOnClickListener { openNavigation(A_MAP) }

            mapPickerDialog?.setContentView(view)
            mapPickerDialog?.setOnDismissListener { mapPickerDialog = null }
            mapPickerDialog?.show()
            return@let
        }
    }

    private fun isMapAppInstalled(packageName: String): Boolean {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = context?.packageManager?.getPackageInfo(packageName, 0)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return packageInfo != null
    }

    private fun getAppName(): String {
        context?.let {
            try {
                val packageManager = it.packageManager
                val packageInfo = packageManager.getPackageInfo(it.packageName, 0)
                val labelRes = packageInfo.applicationInfo.labelRes
                return it.resources.getString(labelRes)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        return ""
    }

    private fun openNavigation(packageName: String) {
        context?.let {
            var toast = 0
            val appName = getAppName()
            try {
                val bd = CoordinateConverter()
                        .from(CoordinateConverter.CoordType.BAIDU)
                        .coord(LatLng(targetLocationLatitude, targetLocationLongitude))
                        .convert()
                when (packageName) {
                    BAIDU_MAP -> {
                        toast = R.string.ml_baidu_map_not_install
                        val intent = Intent()
                        intent.data = Uri.parse("baidumap://map/direction?destination=latlng:$targetLocationLatitude,$targetLocationLongitude|name:$targetLocationTitle&coord_type=bd09ll&mode=driving")
                        it.startActivity(intent)
                    }
                    A_MAP -> {
                        toast = R.string.ml_a_map_not_install
                        val intent = Intent()
                        intent.setPackage("com.autonavi.minimap")
                        intent.action = Intent.ACTION_VIEW
                        intent.addCategory(Intent.CATEGORY_DEFAULT)
                        intent.data = Uri.parse("androidamap://route?sourceApplication=$appName&dlat=${bd.latitude}&dlon=${bd.longitude}&dname=$targetLocationTitle&dev=0&t=0")
                        it.startActivity(intent)
                    }
                    TENCENT_MAP -> {
                        toast = R.string.ml_tencent_map_not_install
                        val intent = Intent()
                        intent.data = Uri.parse("qqmap://map/routeplan?type=walk&to=$targetLocationTitle&tocoord=${bd.latitude},${bd.longitude}&policy=1&referer=$appName")
                        it.startActivity(intent)
                    }
                    else -> {
                    }
                }
            } catch (ex: ActivityNotFoundException) {
                if (toast != 0) {
                    Toast.makeText(it, toast, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.confirm -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setMyLocation(location: Position, zoomLevel: Float?) {
        myLocationMarker?.let {
            mapProxy.removeMarker(it)
        }
        myLocationMarker = Marker(location, true, imageBitmap = Marker.createBitmap(requireContext(), R.drawable.map_location_double_circle_icon, "00"))
        mapProxy.addMarker(myLocationMarker!!, zoomLevel)
    }

    private fun setTargetLocation(location: Position, zoomLevel: Float?) {
        targetLocationMarker?.let {
            mapProxy.removeMarker(it)
        }
        targetLocationMarker = Marker(location, true, imageBitmap = Marker.createBitmap(requireContext(), R.drawable.map_location_double_circle_icon, "00"))
        mapProxy.addMarker(targetLocationMarker!!, zoomLevel)
    }

    override fun onResume() {
        super.onResume()
        mapProxy.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapProxy.onPause()
    }

    override fun onDestroyView() {
        mapProxy.onDestroy()
        super.onDestroyView()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (checkAppPermission(*location_permissions)) {
            viewModel?.getMyLocation()
        }
    }
}
