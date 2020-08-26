package net.csolorzano.pokemongame

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val ACCESSLOCATION = 0x001
    var listPokemon= mutableListOf<Pokemon>()
    lateinit var myMarker: Marker
    var pokemonMarkers = mutableListOf<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermission()
        loadPokemon()
    }

    private fun loadPokemon() {
        listPokemon.add(Pokemon(
            "Charmander",
            "Fire pokemon of the dragon class",
            1,
            55.0,
            14.0681,
            -87.1933
        ))
        listPokemon.add(Pokemon(
            "Bulbasaur",
            "Plant pokemon",
            2,
            90.5,
            14.0665,
            -87.1939
        ))
        listPokemon.add(Pokemon(
            "Squirtle",
            "Water pokemon",
            3,
            33.5,
            14.0688,
            -87.1915
        ))

    }

    fun checkPermission(){
        if(Build.VERSION.SDK_INT >= 23){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),ACCESSLOCATION)
                return
            }
        }

        getUserLocation()
    }

    fun getUserLocation(){
        Toast.makeText(this,"User location access on", Toast.LENGTH_LONG).show()

        val myLocationListener = MyLocationListener(::onLocationChange)
        val locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,myLocationListener)

    }

    fun onLocationChange(loc:Location){
        val newLocation = LatLng(loc.latitude, loc.longitude)
        if(myMarker != null){
            println("Marker ${myMarker}")
            myMarker.position=newLocation
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 18f))
        }else{
            addMyMarker(newLocation)
        }
        lookCatch()
    }

    private fun lookCatch() {
        val myLocation = Location("")
        myLocation.latitude = myMarker.position.latitude
        myLocation.longitude = myMarker.position.longitude

        var pokemonRemoves = mutableListOf<Marker>()
        for(pk in pokemonMarkers){
            val pkLocation = Location("")
            pkLocation.latitude = pk.position.latitude
            pkLocation.longitude = pk.position.longitude

            println("DBG: Me(${myLocation}) pokemon(${pkLocation}) distance:${myLocation.distanceTo(pkLocation)}")
            if(myLocation.distanceTo(pkLocation) <= 20){
                pk.remove()
                pokemonRemoves.add(pk)
                Toast.makeText(this,"${pk.title} was captured", Toast.LENGTH_LONG).show()
            }
        }

        pokemonMarkers.removeAll(pokemonRemoves)
        pokemonRemoves.clear()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            ACCESSLOCATION->{
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    getUserLocation()
                }else{
                    Toast.makeText(this,"Access location permission was denied", Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun addMyMarker(latLng:LatLng){
            myMarker = mMap.addMarker(MarkerOptions()
                .position(latLng)
                .title("Me")
                .snippet("My current location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario)))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        addMyMarker(LatLng(14.07,-87.19))

        for (pk in listPokemon){
            var marker = mMap.addMarker(MarkerOptions()
                .position(LatLng(pk.lat,pk.lon))
                .title(pk.name)
                .snippet(pk.des)
                .icon(if (pk.image == 1) BitmapDescriptorFactory.fromResource(R.drawable.charmander)
                else if(pk.image == 2) BitmapDescriptorFactory.fromResource(R.drawable.bulbasaur)
                else BitmapDescriptorFactory.fromResource(R.drawable.squirtle))
            )
            pokemonMarkers.add(marker)
        }
    }
}