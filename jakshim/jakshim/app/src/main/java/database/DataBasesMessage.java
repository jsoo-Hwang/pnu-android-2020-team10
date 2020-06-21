package database;

import android.provider.BaseColumns;

public final class DataBasesMessage {

    public static final class CreateDB implements BaseColumns {
        public static final String PHONENUMBER = "phonenumber";
        public static final String MESSAGE = "message";
        public static final String NAME = "PHONE";
        public static final String _TABLENAME0 = "message_database";
        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("
                +_ID+" integer primary key autoincrement, "
                +PHONENUMBER+" text not null , "
                +NAME+" text not null , "
                +MESSAGE+" text not null); ";

    }
}
