package locationspoof.tech.future.spooflocation.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.ListIterator;

import locationspoof.tech.future.spooflocation.R;
import locationspoof.tech.future.spooflocation.Utility.AlertMessage;
import locationspoof.tech.future.spooflocation.Utility.FileHandler;


/**
 * Created by Troller on 10/12/2017.
 */

public abstract class BaseActivity extends Activity {


    private static ShareActionProvider mShareActionProvider;
    protected static LatLng selectedAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        setShareIntent(createShareIntent());
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.menu_item_search:
                showAddressInput(this);
                return true;
            case R.id.action_refresh:
                refresh();
                return true;
            case R.id.action_about:
                  about_handler();
                return true;
            case R.id.action_help:
                help_handler();
                return true;
            case R.id.recent_location:
                showRecentLocations(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * Method to check whether permission for GPS is still granted.
     * @return status of the permission
     */
    public boolean checkLocationPermission()
    {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private void showRecentLocations(final Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recent Locations");

      final  ArrayList<Location> recentLocations = FileHandler.readRecentLocations(getBaseContext());

        Collections.reverse(recentLocations);
      final ArrayList<String> locations = new ArrayList<>();
        int count = 0;
        if(!recentLocations.isEmpty()) {

            for(Location location : recentLocations) {
                locations.add("Latitude :" + location.getLatitude() + " Longitude :" + location.getLongitude());
                count++;
                if(count>=5){
                    break;
                }
            }
            ListView modeList = new ListView(this);
            ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_list_item_2, android.R.id.text2, locations);
            modeAdapter.getCount();
            modeList.setAdapter(modeAdapter);
            builder.setView(modeList);
            final Dialog dialog = builder.create();
            modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if(!checkLocationPermission()){
                        AlertMessage.locationRequiredAlert(context);
                    }
                    addressOk_Handler(null, "" + recentLocations.get(position).getLatitude(), "" + recentLocations.get(position).getLongitude());
                    dialog.dismiss();
                }
            });

            dialog.show();

        }else{
            builder.setMessage("No recent locations found");
            builder.show();
        }

    }
    public void showAddressInput(Context context){
        if(!checkLocationPermission()){
            AlertMessage.locationRequiredAlert(this);
        }
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.input_address_dialog);
            dialog.setTitle("Enter Location");


            ImageView image = (ImageView) dialog.findViewById(R.id.image);
            // image.setImageResource(R.drawable.icon);

            Button cancelButton = (Button) dialog.findViewById(R.id.dialogButtonCancel);
            // if button is clicked, close the custom dialog
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            Button okButton = (Button) dialog.findViewById(R.id.dialogButtonOK);

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText uAddress = (EditText) dialog.findViewById(R.id.address);
                    EditText latitude = (EditText) dialog.findViewById(R.id.latitude);
                    EditText longitude = (EditText) dialog.findViewById(R.id.longitude);

                    if (!uAddress.getText().toString().isEmpty()) {
                        addressOk_Handler(uAddress.getText().toString(), null, null);
                        dialog.dismiss();
                    } else if (!latitude.getText().toString().isEmpty() && !longitude.getText().toString().isEmpty()) {
                        addressOk_Handler(null, latitude.getText().toString(), longitude.getText().toString());
                        dialog.dismiss();
                    } else {
                        uAddress.setError("Enter Address or Coordinates");
                        latitude.setError("Enter Address or Coordinates");
                        longitude.setError("Enter Address or Coordinates");
                    }
                }
            });
            dialog.show();

    }

    /**
     * Refresh the activity
     */
    public void refresh(){
        finish();
        startActivity(getIntent());
    }


    // Call to update the share intent
    protected static  void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
    protected static Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        if(selectedAddress!=null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Location is : Latitude -" + selectedAddress.latitude + " Longitude - " + selectedAddress.longitude);
        }else{
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Location not selected");
        }
        return shareIntent;

    }

    public void addressOk_Handler(String  uAddress, String latitude, String longitude){
        launchMapWithAddress(uAddress, latitude , longitude);
    }


    public void about_handler(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About");
        builder.setMessage("This application was created by Shehan Wilfred in-order to achieve the distinction " +
                "task in software development for mobile device")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void help_handler(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Help");
        builder.setMessage("To fake location you need to first add the spoof location app in the 'Mock Locaiton App'.\nThis is found" +
                " in the developer options in settings")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public abstract void launchMapWithAddress(String address, String latitude, String longitude);





}
