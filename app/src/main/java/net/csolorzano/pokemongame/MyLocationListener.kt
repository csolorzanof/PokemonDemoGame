package net.csolorzano.pokemongame

import android.location.Location
import android.location.LocationListener
import android.os.Bundle

class MyLocationListener: LocationListener {
    var location: Location?= null
    var callback:(loc: Location)->Unit
    constructor(cb:(loc: Location)->Unit){
        location = Location("Start")
        location?.longitude=0.0
        location?.latitude=0.0
        callback=cb
    }

    override fun onLocationChanged(p0: Location) {
        this.location = p0
        callback(p0)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onProviderDisabled(provider: String) {

    }
}