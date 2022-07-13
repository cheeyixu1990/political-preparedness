package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.model.Representative
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

private const val TAG = "PPDetailFragment"

class DetailFragment : Fragment() {

    companion object {
        const val LOCATION_REQUEST_CODE = 123
        const val MOTION_LAYOUT_STATE_KEY = "MOTION_LAYOUT_STATE_KEY"
        const val REP_LIST_KEY = "REP_LIST_KEY"
    }

    private val viewModel: RepresentativeViewModel by lazy {
        ViewModelProvider(this).get(RepresentativeViewModel::class.java)
    }

    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_representative, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.restoreRepList(savedInstanceState?.getParcelableArrayList<Representative>(REP_LIST_KEY)?.toList())
        savedInstanceState?.getBundle(MOTION_LAYOUT_STATE_KEY)
            ?.let {
                binding.motinLayout.transitionState = it
                Log.d(TAG, "MOTION_LAYOUT_STATE_KEY: $it")
            }

        viewModel.address.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.findRepresentatives()
            }
        }

        val representativeListAdapter = RepresentativeListAdapter()
        binding.representativesList.adapter = representativeListAdapter
        viewModel.representatives.observe(viewLifecycleOwner) {
            representativeListAdapter.submitList(it)
        }

        binding.btnLocation.setOnClickListener {
            hideKeyboard()
            if (checkLocationPermissions())
                getLocation()
        }

        binding.btnSearch.setOnClickListener {
            hideKeyboard()
            val address = Address(
                binding.addressLine1.text.toString(),
                binding.addressLine2.text.toString(),
                binding.city.text.toString(),
                binding.state.selectedItem.toString(),
                binding.zip.text.toString()
            )

            viewModel.setAddress(address)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.executePendingBindings()
        return binding.root

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_REQUEST_CODE)
        {
            if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED){
                Snackbar.make(
                    binding.root,
                    R.string.location_permission_denied_explanation,
                    Snackbar.LENGTH_LONG
                )
                    .setAction(R.string.settings) {
                        startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }
                    .show()
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            }
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_REQUEST_CODE)
            false
        }
    }

    private fun isPermissionGranted() : Boolean {
        return  ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            val address = it?.let { geoCodeLocation(it) }
            address?.let {
                viewModel.setAddress(address)
            }
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "MOTION_LAYOUT_STATE_KEY: ${binding.motinLayout.transitionState}")
        val motionLayoutState = binding.motinLayout.transitionState
        val repList = viewModel.representatives.value as ArrayList<Representative>
        super.onSaveInstanceState(outState)
        outState.putBundle(MOTION_LAYOUT_STATE_KEY, motionLayoutState)
        outState.putParcelableArrayList(REP_LIST_KEY, repList)
    }
}