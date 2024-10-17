package mobiledev.unb.ca.mapstest

import android.Manifest
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.location.FusedLocationProviderClient
import android.os.Bundle
import com.google.android.gms.location.LocationServices
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory

class MapsActivity : FragmentActivity(), OnMapReadyCallback {
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var currentLocation: Location? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Create an instance of the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Fetch the last known location
        fetchLastLocation()
    }

    private fun fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST)

            // Location permissions not available
            return
        }
        val task = fusedLocationClient!!.lastLocation
        task.addOnSuccessListener(this) { location: Location? ->
            if (location != null) {
                currentLocation = location
                Toast.makeText(applicationContext, currentLocation!!.latitude.toString() + "," +
                        currentLocation!!.longitude, Toast.LENGTH_LONG).show()

                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                val mapFragment = (supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment?)!!
                mapFragment.getMapAsync(this@MapsActivity)
            } else {
                Toast.makeText(applicationContext,
                    "Unable to fetch the location",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Fredericton.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {

        // Add a marker based on the current location and move the camera
        val latLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
        val markerOptions = MarkerOptions().position(latLng).title("You are here")
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        googleMap.addMarker(markerOptions)
    }

    /**
     * Handles the result of the permissions check
     * @param requestCode The code to be checked against
     * @param permissions The list of permissions to be checked against
     * @param grantResults The list of granted results
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(TAG, "onRequestPermissionsResult: Granted")
                fetchLastLocation()
            } else {
                Toast.makeText(this, "onRequestPermissionsResult: Denied", Toast.LENGTH_SHORT)
                    .show()
                Log.i(TAG, "onRequestPermissionsResult: Denied")
            }
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
        private const val LOCATION_REQUEST = 101
    }
}