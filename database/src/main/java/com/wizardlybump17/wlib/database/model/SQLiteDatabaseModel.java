package com.wizardlybump17.wlib.database.model;

import com.wizardlybump17.wlib.database.DatabaseHolder;
import com.wizardlybump17.wlib.database.DatabaseModel;
import com.wizardlybump17.wlib.database.SQLiteDatabase;
import com.wizardlybump17.wlib.database.orm.Modifier;

import java.util.Properties;

public class SQLiteDatabaseModel extends DatabaseModel<SQLiteDatabase> {

    public SQLiteDatabaseModel() {
        super("sqlite", "jdbc:sqlite:{database}");
    }

    @Override
    public SQLiteDatabase createDatabase(DatabaseHolder holder, Properties properties) {
        return new SQLiteDatabase(this, properties, holder);
    }

    @Override
    public String getModifierCommand(Modifier modifier) {
        switch (modifier) {
            case UNIQUE: return "UNIQUE";
            case PRIMARY_KEY: return "PRIMARY KEY";
            case NOT_NULL: return "NOT NULL";
            case AUTO_INCREMENT: return "AUTOINCREMENT";
            default: return "";
        }
    }
}
