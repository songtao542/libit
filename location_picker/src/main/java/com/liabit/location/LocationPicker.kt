package com.liabit.location

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.*
import android.view.animation.CycleInterpolator
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.location.AMapLocation
import com.amap.api.maps2d.model.LatLng
import com.amap.api.maps2d.model.MyLocationStyle
import com.liabit.location.databinding.MapLocationNestedPickerFragmentBinding
import com.liabit.location.extension.checkAndRequestPermission
import com.liabit.location.extension.checkAppPermission
import com.liabit.location.extension.location_permissions
import com.liabit.location.model.AddressType
import com.liabit.location.model.PoiAddress
import com.liabit.location.model.Position
import com.liabit.location.util.dip
import com.liabit.location.util.hideSoftKeyboard
import com.liabit.viewbinding.bind

/**
 *
 */
class LocationPicker : BaseFragment(), Toolbar.OnMenuItemClickListener {

    companion object {
        @JvmStatic
        fun newInstance(arguments: Bundle? = null) = LocationPicker().apply {
            this.arguments = arguments
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

    private lateinit var adapter: LocationPickerRecyclerViewAdapter

    private var address: PoiAddress? = null
    private var pois: List<PoiAddress>? = null
    private var hasMove = false
    private var lastMotionEvent: MotionEvent? = null

    private val binding by bind<MapLocationNestedPickerFragmentBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map_location_nested_picker_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        enableOptionsMenu(binding.appbar.toolbar, false, R.menu.map_location_picker_menu)
        binding.appbar.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        binding.appbar.toolbar.setOnMenuItemClickListener(this)
        //titleTextView.setText(R.string.location_picker_title)

        mapProxy = MapLocationFactory.create(requireContext(), mapView = binding.mapView)

        mapProxy.onCreate(savedInstanceState)

        val myLocationStyle = MyLocationStyle() //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
        myLocationStyle.showMyLocation(false)

        binding.mapView.map.setMyLocationStyle(myLocationStyle)
        binding.mapView.map.isMyLocationEnabled = true

        binding.mapView.map.setOnMapTouchListener { event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                if (lastMotionEvent == null) {
                    lastMotionEvent = event
                } else {
                    hasMove = (event.rawX != lastMotionEvent!!.rawX || event.rawY != lastMotionEvent!!.rawY)
                }
            }
            if (event.action == MotionEvent.ACTION_UP) {
                if (hasMove) {
                    playJumpAnimation()
                    search(latLng = binding.mapView.getCenter())
                }
                lastMotionEvent = null
                hasMove = false
            }
            if (event.action == MotionEvent.ACTION_CANCEL) {
                lastMotionEvent = null
                hasMove = false
            }
        }

        binding.nestLayout.setMinHeight(dip(150))

        binding.list.layoutManager = LinearLayoutManager(requireContext())
        adapter = LocationPickerRecyclerViewAdapter()
        adapter.setOnItemClickListener {
            mapProxy.setCenter(it.location, 20f)
        }
        binding.list.adapter = adapter


        binding.appbar.searchBox.setOnEditorActionListener { textView, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    val keyword = textView.text?.toString() ?: ""
                    if (keyword.isNotEmpty()) {
                        activity?.hideSoftKeyboard()
                        search(keyword, noBound = true)
                    }
                    return@setOnEditorActionListener true
                }
                else -> {
                    return@setOnEditorActionListener false
                }
            }
        }

        viewModel?.location?.observe(viewLifecycleOwner, Observer {
            setMyLocation(Position(it), 16f)
        })

        binding.myLocationButton.setOnClickListener {
            search()
        }

        // start search
        search()
    }

    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.location_picker, menu)
    }*/

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.confirm -> {
                adapter.getSelected()?.let {
                    _onResultListener?.invoke(it)
                }
                //activity?.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private var _onResultListener: ((item: PoiAddress) -> Unit)? = null

    fun setOnResultListener(onResultListener: ((item: PoiAddress) -> Unit)?) {
        this._onResultListener = onResultListener
    }

    private fun playJumpAnimation() {
        val downAnimator = ObjectAnimator.ofFloat(binding.centerLocation, "translationY", 0f, dip(10).toFloat())
        val set = AnimatorSet()
        set.playSequentially(downAnimator)
        set.interpolator = CycleInterpolator(2f)
        set.duration = 300
        set.start()
    }

    private fun search(keyword: String = "", latLng: LatLng? = null, noBound: Boolean = false) {
        if (!checkAndRequestPermission(*location_permissions)) {
            return
        }
        when {
            latLng != null -> {
                val location = Position(latLng)
                mapProxy.reverseGeocode(location).observe(viewLifecycleOwner, Observer {
                    it?.type = AddressType.ADDRESS.value
                    fillAdapter(address = it, pois = pois)
                })
                mapProxy.searchPoi(keyword, location).observe(viewLifecycleOwner, Observer { result ->
                    fillAdapter(address = address, pois = result)
                })
            }
            noBound -> {
                mapProxy.searchPoi(keyword).observe(viewLifecycleOwner, Observer { result ->
                    address = null
                    fillAdapter(address = address, pois = result)
                })
            }
            else -> {
                viewModel?.getMyLocation { location ->
                    if (location is AMapLocation) {
                        fillAdapter(address = location.toPoiAddress(), pois = pois)
                    }
                    mapProxy.searchPoi(keyword, Position(location)).observe(viewLifecycleOwner, Observer { result ->
                        fillAdapter(address = address, pois = result)
                    })
                }
            }
        }
    }

    private fun fillAdapter(address: PoiAddress? = null, pois: List<PoiAddress>? = null) {
        if (address != null) {
            this.address = address
        }
        if (pois != null) {
            this.pois = pois
        }
        val data = ArrayList<PoiAddress>()
        this.address?.let {
            data.add(it)
        }
        this.pois?.let {
            data.addAll(it)
        }
        adapter.data = data
    }

    private fun setMyLocation(location: Position, zoomLevel: Float?) {
        myLocationMarker?.let {
            mapProxy.removeMarker(it)
        }
        myLocationMarker = Marker(location, true, imageBitmap = Marker.createBitmap(requireContext(), R.drawable.map_location_double_circle_icon, "00"))
        mapProxy.addMarker(myLocationMarker!!, zoomLevel)
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
            search()
        }
    }
}
