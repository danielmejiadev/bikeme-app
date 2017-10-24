package com.android.bikeme.databaselocal.databasesqlite;

/**
 * Created by Daniel on 6 jul 2017.
 */
public class SqlStatements {

    public static String getSQLSuggestRoutes(String idUserToSuggest,String[] selectionArgs)
    {
        String sql =
                "SELECT * FROM " +
                "( " +
                "    SELECT uid, name, description, image, COALESCE(average_ratings,0) average_ratings, recommendation FROM  " +
                "    ( " +
                "          ( SELECT uid, name, description, image, recommendation FROM route INNER JOIN rating " +
                "             ON rating.route_id = route.uid " +
                "             WHERE rating.user_id = '"+idUserToSuggest+"' and calification = 0 and recommendation > 0 " +
                "          ) suggests " +

                "           LEFT JOIN " +

                "          ( SELECT route_id, AVG(calification) average_ratings FROM rating " +
                "            WHERE calification > 0 and recommendation = 0 " +
                "            GROUP BY route_id " +
                "          ) averages " +

                "          ON suggests.uid = averages.route_id " +
                "    ) " +


                "    UNION " +

                "    SELECT uid, name, description, image, COALESCE(average_ratings,0) average_ratings, recommendation FROM  " +
                "    ( " +
                "        ( " +
                "           SELECT * FROM " +
                "           ( ";

        if(selectionArgs != null)
        {
            sql +=
                            "       SELECT uid, name, description, image, -1 recommendation FROM " +
                            "       ( " +
                            "           route " +

                            "           INNER JOIN " +

                            "           ( " +
                            "               SELECT * FROM " +
                            "               ( " +
                            "                   SELECT min(id), route_id, latitude, longitude FROM point " +
                            "                   GROUP by route_id " +
                            "                   HAVING latitude  >= "+selectionArgs[0]+" and " +
                            "                          longitude >= "+selectionArgs[1]+" and " +
                            "                          latitude  <= "+selectionArgs[2]+" and " +
                            "                          longitude <= "+selectionArgs[3]+" " +
                            "               ) initial_points " +

                            "           )routes_closer " +

                            "           ON routes_closer.route_id = route.uid " +
                            "       ) " +

                            "       UNION ";
        }

        sql +=
                "                   SELECT uid, name, description, image, -2 recommendation FROM route " +
                "                   WHERE level = (SELECT level FROM user WHERE uid = '"+idUserToSuggest+"') " +

                "           ) level_closer_routes_union " +
                "           WHERE level_closer_routes_union.uid NOT IN " +
                "           (" +
                "               SELECT uid FROM route INNER JOIN rating " +
                "               ON rating.route_id = route.uid " +
                "               WHERE rating.user_id = '"+idUserToSuggest+"' and (calification > 0 or recommendation > 0)" +
                "           )" +

                "        ) level_closer_routes" +

                "        LEFT JOIN " +

                "        ( SELECT route_id, avg (calification) average_ratings FROM rating " +
                "            WHERE calification > 0 and recommendation = 0" +
                "           GROUP BY route_id " +
                "        ) average " +

                "        ON level_closer_routes.uid = average.route_id " +
                "    )" +

                ") recommender " +
                "ORDER BY recommender.recommendation DESC ";

        return  sql;
    }

    public static String getSQLRoutesNews(String[] selectionArgs)
    {
        String date = selectionArgs[0];
        String minRatings = selectionArgs[1];
        String userId = selectionArgs[2];

        String sql =
                "SELECT uid, name, description, image, created, average_ratings FROM " +
                "(" +
                "   SELECT uid, name, description, image, created, COALESCE(count_ratings,0) count_ratings, COALESCE(average_ratings,0) average_ratings FROM " +
                "   (" +
                "      (" +

                "         SELECT * FROM route " +
                "         WHERE uid NOT IN " +
                "         (" +
                "             SELECT uid FROM " +
                "             (" +
                "               route INNER JOIN rating " +
                "               ON rating.route_id = route.uid " +
                "             )" +
                "             WHERE rating.user_id = '"+userId+"'  and  (calification > 0 or recommendation > 0) " +

                "             UNION " +

                "             SELECT uid FROM route " +
                "             WHERE level = (SELECT level FROM user WHERE uid = '"+userId+"') ";

        if (selectionArgs.length > 3)
        {
            sql +=
                "             UNION " +

                "             SELECT uid FROM " +
                "             ( " +
                "               route " +

                "               INNER JOIN " +

                "               ( " +
                "                   SELECT * FROM " +
                "                   ( " +
                "                       SELECT min(id), route_id, latitude, longitude FROM point " +
                "                       GROUP by route_id " +
                "                       HAVING latitude  >= "+selectionArgs[3]+" and " +
                "                              longitude >= "+selectionArgs[4]+" and " +
                "                              latitude  <= "+selectionArgs[5]+" and " +
                "                              longitude <= "+selectionArgs[6]+" " +
                "                    ) initial_points " +

                "                )routes_closer " +

                "                ON routes_closer.route_id = route.uid " +
                "             ) ";
        }

        sql +=
                "         ) " +

                "      ) routes_news_min_ratings " +

                "      LEFT JOIN " +

                "      ( " +
                "       SELECT route_id, count(calification) count_ratings, AVG(calification) average_ratings FROM rating " +
                "       WHERE calification > 0 and recommendation = 0 " +
                "       GROUP BY route_id " +
                "      ) counts " +

                "      ON counts.route_id = routes_news_min_ratings.uid " +
                "   )" +

                ") routes_news " +
                "WHERE routes_news.count_ratings < "+minRatings+" OR routes_news.created >= '"+date+"' " +
                "ORDER BY  routes_news.count_ratings ASC, routes_news.created DESC ";

        return sql;
    }

    public static String getSQLRoutesMine(String idUserRoutesMine)
    {
        return  "SELECT uid, name, description, image, average_ratings FROM " +
                "( " +
                "     ( SELECT * FROM " +
                "       route INNER JOIN rating " +
                "       ON rating.route_id = route.uid " +
                "       WHERE rating.user_id = '"+idUserRoutesMine+"' and calification > 0 and recommendation = 0 " +
                "     ) mines " +

                "     INNER JOIN " +

                "     ( SELECT route_id, avg (calification) as average_ratings FROM " +
                "       rating " +
                "       WHERE calification > 0 and recommendation = 0 " +
                "       GROUP BY route_id  " +
                "     ) average " +

                "     ON mines.uid = average.route_id " +
                ") ORDER BY date DESC ";
    }

    public static String getSQLUserWorkouts(String idUserWorkout)
    {
        return  "SELECT * FROM " +
                "(  " +

                "   ( SELECT * FROM workout WHERE user_id = '"+idUserWorkout+"' ) workouts " +

                "    JOIN " +

                "    (SELECT b.dateGroup , COUNT(*) AS numberGroup FROM " +
                "            (SELECT DISTINCT DATE(beginDate) as dateGroup FROM workout) a " +
                "                JOIN " +
                "            (SELECT DISTINCT DATE(beginDate) as dateGroup FROM workout) b " +
                "            ON a.dateGroup <= b.dateGroup " +
                "            GROUP BY b.dateGroup  " +
                "    ) dates  " +
                "    ON DATE(workouts.beginDate) = dates.dateGroup " +
                ") " +
                "ORDER BY beginDate DESC";
    }

    public static String getSQLUserChallengesParams(String idUserParams, String[] selectionArgs)
    {
        return "SELECT * FROM " +
                "( " +
                "    ( " +
                "    SELECT COUNT(*) as totalRoutesCreated FROM route " +
                "    WHERE creator_id = '"+idUserParams+"'" +
                "    ) routesCreated " +

                "    JOIN " +

                "    ( " +
                "    SELECT COUNT(*) as totalRoutesRated FROM rating " +
                "    WHERE user_id = '"+idUserParams+"' AND " +
                "          calification > 0 AND " +
                "          recommendation = 0 " +
                "    ) routesRated " +

                "    JOIN " +

                "    ( " +
                "    SELECT COALESCE(SUM(totalDistanceMeters), 0) as totalDistanceMetersByWeek " +
                "    FROM workout " +
                "    WHERE user_id = '"+idUserParams+"' AND " +
                "          beginDate >= '"+selectionArgs[0]+"' AND " +
                "          beginDate <  '"+selectionArgs[1]+"'" +
                "    ) totalDistanceByWeek " +

                "    JOIN " +

                "    ( " +
                "    SELECT COALESCE(SUM(totalDistanceMeters), 0) as totalDistanceMetersByMonth " +
                "    FROM workout " +
                "    WHERE user_id = '"+idUserParams+"' AND " +
                "          beginDate >= '"+selectionArgs[2]+"' AND " +
                "          beginDate < '"+selectionArgs[3]+"'" +
                "    ) totalDistanceByMonth " +

                "    JOIN " +

                "    ( " +
                "    SELECT COALESCE(SUM(totalDistanceMeters), 0) as totalDistanceMeters, " +
                "           COALESCE(SUM(durationSeconds ), 0) as totalDurationSeconds " +
                "    FROM workout " +
                "    WHERE user_id = '"+idUserParams+"'" +
                "    ) totalDistanceDuration " +
                ")";
    }



    public static String getSQLEventsGroupByDate(String userUid)
    {
        return  "SELECT events.uid uid, name, date, route_id, pos, guest, departure, arrival, distance FROM " +
                "(  " +
                " SELECT * FROM " +
                "    (SELECT event.uid, event.name, event.date, event.route_id, COALESCE(guest,0) guest FROM " +
                "        event " +
                "        LEFT JOIN " +
                "        (SELECT uid, name, event.date, route_id, 1 guest FROM " +
                "            guest JOIN event " +
                "            ON guest.event_id = event.uid " +
                "            WHERE user_id = '"+userUid+"' " +
                "        ) my_events " +
                "        ON event.uid = my_events.uid " +
                "    ) all_events " +

                "    JOIN " +

                "    (SELECT b.dateGroup , COUNT(*) AS pos FROM " +
                "            (SELECT DISTINCT DATE(date) as dateGroup FROM event) a " +
                "                JOIN " +
                "            (SELECT DISTINCT DATE(date) as dateGroup FROM event) b " +
                "            ON a.dateGroup <= b.dateGroup " +
                "            GROUP BY b.dateGroup  " +
                "    ) dates  " +
                "    ON DATE(all_events.date) = dates.dateGroup " +
                "     " +
                ") events " +

                "JOIN " +

                "(SELECT uid, departure, arrival, distance FROM route " +
                ") routes " +

                "ON events.route_id = routes.uid " +
                "ORDER BY date DESC";
    }
}