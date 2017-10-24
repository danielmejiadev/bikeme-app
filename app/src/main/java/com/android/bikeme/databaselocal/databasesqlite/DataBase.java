package com.android.bikeme.databaselocal.databasesqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

/**
 * Created by Daniel on 18/03/2017.
 */
public class DataBase extends SQLiteOpenHelper {


    public DataBase(Context context)
    {
        super(context, DataBaseContract.DATABASE_NAME, null, DataBaseContract.DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase sqLiteDatabase)
    {
        super.onOpen(sqLiteDatabase);
        if (!sqLiteDatabase.isReadOnly())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                sqLiteDatabase.setForeignKeyConstraintsEnabled(true);
            } else
            {
                sqLiteDatabase.execSQL("PRAGMA foreign_keys=ON");
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL(DataBaseContract.User.SQL_CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(DataBaseContract.Workout.SQL_CREATE_WORKOUT_TABLE);

        sqLiteDatabase.execSQL(DataBaseContract.Route.SQL_CREATE_ROUTE_TABLE);
        sqLiteDatabase.execSQL(DataBaseContract.Rating.SQL_CREATE_RATING_TABLE);
        sqLiteDatabase.execSQL(DataBaseContract.Point.SQL_CREATE_POINT_TABLE);

        sqLiteDatabase.execSQL(DataBaseContract.Event.SQL_CREATE_EVENT_TABLE);
        sqLiteDatabase.execSQL(DataBaseContract.Guest.SQL_CREATE_GUEST_TABLE);

        sqLiteDatabase.execSQL(DataBaseContract.Challenge.SQL_CREATE_CHALLENGE_TABLE);

        sqLiteDatabase.execSQL(DataBaseContract.Problem.SQL_CREATE_PROBLEM_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL(DataBaseContract.User.SQL_DELETE_USER_TABLE);
        sqLiteDatabase.execSQL(DataBaseContract.Workout.SQL_DELETE_WORKOUT_TABLE);

        sqLiteDatabase.execSQL(DataBaseContract.Route.SQL_DELETE_ROUTE_TABLE);
        sqLiteDatabase.execSQL(DataBaseContract.Rating.SQL_DELETE_RATING_TABLE);
        sqLiteDatabase.execSQL(DataBaseContract.Point.SQL_DELETE_POINT_TABLE);

        sqLiteDatabase.execSQL(DataBaseContract.Event.SQL_DELETE_EVENT_TABLE);
        sqLiteDatabase.execSQL(DataBaseContract.Guest.SQL_DELETE_GUEST_TABLE);

        sqLiteDatabase.execSQL(DataBaseContract.Challenge.SQL_DELETE_CHALLENGE_TABLE);

        sqLiteDatabase.execSQL(DataBaseContract.Problem.SQL_DELETE_PROBLEM_TABLE);

        onCreate(sqLiteDatabase);
    }
}
