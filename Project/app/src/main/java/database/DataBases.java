package database;

import android.provider.BaseColumns;

public final class DataBases {

    public static final class CreateDB implements BaseColumns {
        public static final String ALARMID = "alarmId";
        public static final String HOURS = "hours";
        public static final String MINUTE = "minute";
        //public static final String DAYOFWEEK = "dayOfWeek";
        public static final String _TABLENAME0 = "alarm_database";
        public static final String DAYOFAMPM = "dayofampm";
        public static final String NEXTDAYOFWEEK = "nextdayofweek";
        public static final String PLACE ="place";
        public static final String PENALTY = "penalty";
        public static final String PENALTY_LINE = "penaltyline";
        public static final String ONOFF = "onoff";
        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("
                +_ID+" integer primary key autoincrement, "
                +ALARMID+" text not null , "
                +HOURS+" text not null , "
                +DAYOFAMPM+" text not null , "
                +MINUTE+" text not null , "
                +NEXTDAYOFWEEK+" text not null , "
                +PLACE+" text not null , "
                +ONOFF+" text not null , "
                +PENALTY_LINE+" text not null, "
                +PENALTY+" text not null); ";
                //+DAYOFWEEK+" text not null);";
    }
}
