package com.wst.whereigo.Generic;

import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class DirectionsJSONParser {
	
	/** Receives a JSONObject and returns a list of lists containing latitude and longitude */
	public List<List<HashMap<String,String>>> parse(JSONObject jObject){
		
		List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
		JSONArray jRoutes = null;
		JSONArray jLegs = null;
		JSONArray jSteps = null;	
		
		try {			
			
			jRoutes = jObject.getJSONArray("routes");
			
			/* Traversing all routes */
			for(int i=0;i<jRoutes.length();i++){			
				jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
				List path = new ArrayList<HashMap<String, String>>();
				
				/*Traversing all legs */
				for(int j=0;j<jLegs.length();j++){
					jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
					
					/* Traversing all steps */
					for(int k=0;k<jSteps.length();k++){
						String polyline = "";
						polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
						List<LatLng> list = decodePoly(polyline);
						
						/* Traversing all points */
						for(int l=0;l<list.size();l++){
							HashMap<String, String> hm = new HashMap<String, String>();
							hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
							hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
							path.add(hm);						
						}								
					}
					routes.add(path);
				}
			}
			
		} catch (JSONException e) {			
			e.printStackTrace();
		}catch (Exception e){			
		}

		///writeJSON();
		
		return routes;
	}

	public void writeJSON(){
		JSONObject obj = new JSONObject();
		try {
            obj.put("Name", "crunchify.com");
            obj.put("Author", "App Shah");
        }catch (JSONException e){
            Log.e("", "");

        }

		JSONArray company = new JSONArray();
		try {
            company.put("Compnay: eBay");
            company.put("Compnay: Paypal");
            company.put("Compnay: Google");
            obj.put("Company List", company);
        }catch (JSONException e){
            Log.e("", "");

        }

        // try-with-resources statement based on post comment below :)
       /* if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try (FileWriter file = new FileWriter("storage/sdcard/Downloads/file.json")) {
                file.write(obj.toString());
                System.out.println("Successfully Copied JSON Object to File...");
                System.out.println("\nJSON Object: " + obj);
            } catch (IOException e) {
                Log.e("", "");
            }
        }*/

        File dir = new File(Environment.getExternalStorageDirectory() + "/Files/");
        if (!dir.exists()) {
            if(!dir.mkdir()){
                //Toast.makeText(MainActivity.ma, "Error creando la carpeta Renovacion",Toast.LENGTH_SHORT).show();
                return;
            }
        }

        File f = new File(dir.getAbsolutePath(),"_info.txt");
        try {
            /*OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(f));
            fout.write(obj.toString());
            fout.close();*/

            FileWriter file = new FileWriter(dir.getAbsolutePath()+"/_info.txt");
            file.write(obj.toString());
            file.flush();
            file.close();
        }catch (IOException e){
            Log.e("", e.getMessage());
        }
    }
	
	
	/**
	 * Method to decode polyline points 
	 * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java 
	 * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}