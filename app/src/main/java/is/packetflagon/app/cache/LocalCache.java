package is.packetflagon.app.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import is.packetflagon.app.PAC;

/**
 * Created by gareth on 30/07/15.
 */
public class LocalCache {
    private SQLiteDatabase database;
    private OpenHelper dbHelper;

    private String[] pacColumns = {"hash","name"};

    static public int RESULT_BLOCKED = 0;
    static public int RESULT_OK = 1;
    static public int RESULT_ERROR = 2;

    public LocalCache(Context context)
    {
        dbHelper = new OpenHelper(context);
    }

    public void open() throws SQLException
    {
        if(null != dbHelper)
            database = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        if(null != dbHelper)
            dbHelper.close();
    }

    public List<PAC> getPACs()
    {
        Cursor cursor = null;
        List<PAC> pacs = new ArrayList<PAC>();

        try
        {
            cursor = database.query("pacs", pacColumns, null, null, null, null, null);
            while(cursor.moveToNext())
            {
                Log.e("pacs", "Adding " + cursor.getString(0) + " and " + cursor.getString(1) + " to PAC");
                pacs.add(new PAC(cursor.getString(0),cursor.getString(1)));
            }
            if(null != cursor && !cursor.isClosed())
                cursor.close();
        }
        catch (Exception e)
        {
            if(null != cursor && !cursor.isClosed())
                cursor.close();
            e.printStackTrace();
            return pacs;
        }
        return pacs;
    }

    public boolean addPAC(String hash, String name)
    {
        boolean success = false;
        try
        {
            database.beginTransaction();
            ContentValues values = new ContentValues(2);
            values.put("hash", hash);
            values.put("name", name);

            database.insert("pacs", null, values);
            database.setTransactionSuccessful();
            success = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            database.endTransaction();
        }

        return success;
    }
}
