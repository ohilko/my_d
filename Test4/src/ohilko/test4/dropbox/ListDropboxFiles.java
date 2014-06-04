package ohilko.test4.dropbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
 
public class ListDropboxFiles extends AsyncTask<Void, Void, ArrayList<String>> {
 
    private DropboxAPI<AndroidAuthSession> dropbox;
    private String path;
    private Handler handler;
    private List<Map<String, ?>> list = new ArrayList<Map<String, ?>>();
 
    public ListDropboxFiles(DropboxAPI<AndroidAuthSession> dropbox, String path, List<Map<String, ?>> list) {
        this.dropbox = dropbox;
        this.path = path;
        this.list = list;
        
//        this.handler = handler;
    }
 
    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        ArrayList<String> files = new ArrayList<String>();
        try {
            Entry directory = dropbox.metadata(path, 1000, null, true, null);
            for (Entry entry : directory.contents) {
                files.add(entry.fileName());
            }
        } catch (DropboxException e) {
            e.printStackTrace();
        }
 
        return files;
    }
 
    @Override
    protected void onPostExecute(ArrayList<String> result) {
//        Message msgObj = handler.obtainMessage();
//        Bundle b = new Bundle();
//        b.putStringArrayList("data", result);
//        msgObj.setData(b);
//        handler.sendMessage(msgObj);
 
    }
}
