package locationspoof.tech.future.spooflocation.Utility;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Troller on 10/26/2017.
 */

public class FileHandler {

    private static String  fileName = "recentLocations";


    /**
     * Method writes data to the specified custom file
     * @param context the context of the application
     * @return boolean depending on the success
     */

    public static boolean  writeToFile(Context context, LatLng location ) {
        FileOutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter;
        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_APPEND);
            outputStream.write((location.latitude+" "+location.longitude).getBytes());
            outputStreamWriter = new OutputStreamWriter((outputStream));
            outputStreamWriter.append("\r\n");
            outputStreamWriter.close();
            outputStream.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
        /**
         * Method used to read context from the custom locations file
         * @param context the context of the application
         * @return list of custom locations added by the user
         */

    public static ArrayList<Location> readRecentLocations(Context context){
        String splits[];
        Location location;
        ArrayList<Location> customLocations = new ArrayList<>();
        try {
            FileInputStream inputStream = context.openFileInput(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                location = new Location("");
                splits = line.split(" ");
                location.setLatitude(Double.parseDouble(splits[0]));
                location.setLongitude(Double.parseDouble(splits[1]));
                customLocations.add(location);
            }
            bufferedReader.close();
            inputStream.close();
            return customLocations;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customLocations;
    }
}
