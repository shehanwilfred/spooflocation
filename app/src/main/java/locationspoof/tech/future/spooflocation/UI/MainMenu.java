package locationspoof.tech.future.spooflocation.UI;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import locationspoof.tech.future.spooflocation.R;
import locationspoof.tech.future.spooflocation.Utility.AlertMessage;
import locationspoof.tech.future.spooflocation.Utility.FileHandler;

/**
 * Created by Troller on 10/23/2017.
 */

public class MainMenu extends BaseActivity {

    private String address;
    String mocLocationProvider = LocationManager.GPS_PROVIDER;
    FragmentManager fragmentManager;
    MapsActivity mapsActivity;

    /** Called when the activity is first created.
     * Method requests permission from user to use GPS and also checks whether GPS is enabled*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            AlertMessage.noGPSAlert(this);
        }
            mapsActivity = new MapsActivity();
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer,
                    mapsActivity).commit();


    }

    public void launchMapWithAddress(String address, String latitude, String longitude) {

        mapsActivity = new MapsActivity();
        Bundle bundle = new Bundle();    //create a new bundle to the put address to send to fragment

        if (address != null) {
            bundle.putString("address", address);
            this.address = address;
        }
        if (latitude != null && longitude != null) {
            bundle.putString("latitude", latitude);
            bundle.putString("longitude", longitude);
        }
        mapsActivity.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer,
                mapsActivity).commit();
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void changeLocation(View view) {


        try {
        LatLng newAddress = MapsActivity.getNewAddress();
            LocationManager locationManager = (LocationManager) this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);


            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);

            if (mocLocationProvider == null) {
                Toast.makeText(getApplicationContext(), "No location provider found!", Toast.LENGTH_SHORT).show();
                return;
            }
            locationManager.addTestProvider(mocLocationProvider, false, false, false, false, true, true, true, 0, 5);
            locationManager.setTestProviderEnabled(mocLocationProvider, true);

            Location mockLocation = new Location(mocLocationProvider);
            mockLocation.setLatitude(newAddress.latitude);
            mockLocation.setLongitude(newAddress.longitude);
            mockLocation.setAccuracy(1);
            mockLocation.setAltitude(mockLocation.getAltitude());
            mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
            mockLocation.setTime(System.currentTimeMillis());


            locationManager.setTestProviderLocation(mocLocationProvider, mockLocation);
            Toast.makeText(getBaseContext(), "Latitude: " + mockLocation.getLatitude() + " Longitude: " + mockLocation.getLongitude(), Toast.LENGTH_SHORT)
                    .show();
            super.selectedAddress = newAddress;
            super.setShareIntent(super.createShareIntent());
            saveLocation(newAddress);

        } catch (Exception e) {
            Toast.makeText(getBaseContext(),"Please Select location to engage fake location", Toast.LENGTH_SHORT).show();
        }

    }

    public void stopFakeLocation(View view) {
        try {
            LocationManager locationManager = (LocationManager) this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            locationManager.removeTestProvider(mocLocationProvider);
            Toast.makeText(getBaseContext(), "Fake Location Stopped", Toast.LENGTH_SHORT).show();

        }catch(Exception e){
            Toast.makeText(getBaseContext(),"Fake Location Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void saveLocation(LatLng newAddress){
        Location location = new Location("");
        location.setLatitude(newAddress.latitude);
        location.setLongitude(newAddress.longitude);
        FileHandler.writeToFile(this.getBaseContext(),newAddress);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // check if results is granted by checking grantresult array
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    AlertMessage.locationRequiredAlert(this);
                }
                return;
            }
        }
    }


}
