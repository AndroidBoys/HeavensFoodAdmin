package androidboys.com.heavensfoodadmin.AsynckTasks;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidboys.com.heavensfoodadmin.Fragments.UsersLocationFragment;
import androidboys.com.heavensfoodadmin.Parsers.DataParser;

public class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>> {

    //The last argument passed in AsyncTask is the return type of doInBackgroundMethod
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... strings) {

        JSONObject jsonObject;
        List<List<HashMap<String,String>>> routes=null;
        try{
            jsonObject=new JSONObject(strings[0]);
            Log.i("ParserTask",jsonObject.toString());
            DataParser dataParser=new DataParser();

            routes=dataParser.parse(jsonObject);
            Log.i("Parser",routes.toString());
        }catch(Exception e){
            e.printStackTrace();
        }


        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
        super.onPostExecute(lists);

        ArrayList<LatLng> points;
        PolylineOptions polylineOptions=null; //used to make polyLine on map

        //Traversing through all the routes
        for(int i=0;i<lists.size();i++){
            points=new ArrayList<>();
            polylineOptions=new PolylineOptions();

            //fetching i-th route
            List<HashMap<String,String>> path=lists.get(i);
            for(int j=0;j<path.size();j++){

                HashMap<String,String> hashMap=path.get(j);
                double lat=Double.parseDouble(hashMap.get("lat"));
                double lng=Double.parseDouble(hashMap.get("lng"));
                LatLng position=new LatLng(lat,lng);
                points.add(position);

            }

            //Adding all the point in the route
            polylineOptions.addAll(points);//it will add all the LatLng points
            polylineOptions.width(10);
            polylineOptions.color(Color.BLUE);//it is the path color

            Log.i("onPostExecute","onPostExecute lineoptions decoded");
        }

        //drawing polyLine in the googlemap for ith route
        if(polylineOptions!=null){
            UsersLocationFragment.googleMap.addPolyline(polylineOptions);
        }else{
            Log.i("onPostExecute","without Polylines drawn");
        }

    }
}
