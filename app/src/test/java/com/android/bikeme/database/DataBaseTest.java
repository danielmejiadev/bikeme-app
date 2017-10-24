package com.android.bikeme.database;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.android.bikeme.databaselocal.databasesqlite.DataBase;

/**
 * Created by Daniel on 17 oct 2017.
 */
public class DataBaseTest extends AndroidTestCase {
    private DataBase db;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DataBase(context);
    }

    @Override
    public void tearDown() throws Exception
    {
        db.close();
        super.tearDown();
    }

    //According to Zainodis annotation only for legacy and not valid with gradle>1.1:
    //@Test
    public void testAddEntry(){
        // Here i have my new database wich is not connected to the standard database of the App
    }
}
