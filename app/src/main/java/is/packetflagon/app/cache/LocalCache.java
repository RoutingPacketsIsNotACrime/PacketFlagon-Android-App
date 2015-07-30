package is.packetflagon.app.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by gareth on 30/07/15.
 */
public class LocalCache {
    private SQLiteDatabase database;
    private OpenHelper dbHelper;

    private String[] pacColumns = {"fingerprint","nickname"};

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
