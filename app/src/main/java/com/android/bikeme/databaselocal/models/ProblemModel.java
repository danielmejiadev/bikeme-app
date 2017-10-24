package com.android.bikeme.databaselocal.models;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.android.bikeme.classes.Problem;
import com.android.bikeme.databaselocal.databasesqlite.DataBaseContract;
import com.android.bikeme.databaseremote.syncronization.Synchronization;

import java.util.ArrayList;

/**
 * Created by Daniel on 29 sep 2017.
 */
public class ProblemModel extends BikeMeModel{

    private ContentResolver contentResolver;

    public ProblemModel(ContentResolver contentResolver)
    {
        this.contentResolver=contentResolver;
    }

    public void insertProblem(Problem problem)
    {
        ContentValues valuesProblem = new ContentValues();

        valuesProblem.put(DataBaseContract.Problem.COLUMN_DESCRIPTION, problem.getDescription());
        valuesProblem.put(DataBaseContract.Problem.COLUMN_USER_ID, problem.getUser());
        valuesProblem.put(DataBaseContract.COLUMN_DATE, problem.getDate());
        valuesProblem.put(DataBaseContract.COLUMN_INSERT_STATE, DataBaseContract.INSERT_PENDING);

        contentResolver.insert(DataBaseContract.Problem.URI_CONTENT, valuesProblem);

        Synchronization.syncNow(Synchronization.LOCAL_TO_REMOTE_DATABASE_SYNC);

    }

    /**  get ratings for sync */
    public ArrayList<Problem> getProblemsForSync()
    {
        Uri uri = DataBaseContract.Problem.URI_CONTENT;
        String selectionPendingForInsert = DataBaseContract.COLUMN_INSERT_STATE +"=? AND " + DataBaseContract.COLUMN_STATE_SYNC + "=?";
        String[] selectionArgsPendingForInsert = new String[]{DataBaseContract.INSERT_PENDING+"", DataBaseContract.STATE_SYNC+""};
        Cursor cursor = contentResolver.query(uri, null, selectionPendingForInsert, selectionArgsPendingForInsert, null);
        assert  cursor != null;
        ArrayList <Problem> problems = new ArrayList<>();
        if (cursor.getCount() > 0)
        {
            while (cursor.moveToNext())
            {
                problems.add(getProblem(cursor));
            }
        }
        cursor.close();
        return  problems;
    }


    public Problem getProblem(Cursor cursor)
    {
        int id = cursor.getInt(cursor.getColumnIndex(DataBaseContract.Problem._ID));
        String userId = cursor.getString(cursor.getColumnIndex(DataBaseContract.Problem.COLUMN_USER_ID));
        String description = cursor.getString(cursor.getColumnIndex(DataBaseContract.Problem.COLUMN_DESCRIPTION));
        String date = cursor.getString(cursor.getColumnIndex(DataBaseContract.COLUMN_DATE));

        Problem problem = new Problem();
        problem.setId(id);
        problem.setUser(userId);
        problem.setDescription(description);
        problem.setDate(date);

        return problem;
    }
}