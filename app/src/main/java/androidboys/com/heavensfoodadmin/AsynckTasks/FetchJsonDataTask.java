package androidboys.com.heavensfoodadmin.AsynckTasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchJsonDataTask extends AsyncTask<String,Void,String> {

    @Override
    protected String doInBackground(String... strings) {
        String data="";
        try {
            data=getJsonData(strings[0]);
            Log.i("background Task",data);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        ParserTask parserTask = new ParserTask();

        //parsing the json data in another thread
        parserTask.execute(result);
    }


    private String getJsonData(String mapDirectionUrl) throws IOException {
        String data="";
        InputStream inputStream=null;
        HttpURLConnection httpURLConnection=null;

        try{
            URL url=new URL(mapDirectionUrl);
            httpURLConnection= (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            inputStream=httpURLConnection.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer=new StringBuffer();
            String line;

            //extracting the data from bufferedReader and then convert it into string
            while((line=bufferedReader.readLine())!=null){
                stringBuffer.append(line);
            }
            data=stringBuffer.toString();
            Log.i("JsonData","----"+data);
            bufferedReader.close();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            inputStream.close();
            httpURLConnection.disconnect();
        }
        return data;
    }
}
