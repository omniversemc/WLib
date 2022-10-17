package com.wizardlybump17.wlib.database.model;

import com.wizardlybump17.wlib.database.DatabaseHolder;
import com.wizardlybump17.wlib.database.DatabaseModel;
import com.wizardlybump17.wlib.database.MySQLDatabase;
import com.wizardlybump17.wlib.database.orm.Modifier;

import java.util.Properties;

public class MySQLDatabaseModel extends DatabaseModel<MySQLDatabase> {

    public MySQLDatabaseModel() {
        super("mysql", "jdbc:mysql://{host}:{port}/{database}");
    }

    @Override
    public MySQLDatabase createDatabase(DatabaseHolder holder, Properties properties) {
        return new MySQLDatabase(this, properties, holder);
    }

    @Override
    public String getModifierCommand(Modifier modifier) {
        switch (modifier) {
            case UNIQUE: return "UNIQUE";
            case PRIMARY_KEY: return "PRIMARY KEY";
            case NOT_NULL: return "NOT NULL";
            case AUTO_INCREMENT: return "AUTO_INCREMENT";
            default: return "";
        }
    }
}
