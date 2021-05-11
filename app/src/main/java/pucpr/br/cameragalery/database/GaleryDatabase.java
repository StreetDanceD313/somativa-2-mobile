package pucpr.br.cameragalery.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import pucpr.br.cameragalery.model.Foto;

public class GaleryDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "galery.sqlite";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "Galery";
    private static final String COL_ID = "id";
    private static final String COL_PATH = "path";

    /*
    CREATE
    RETRIEVE
    UPDATE
    DELETE
    CRUD
     */

    private Context context;

    public GaleryDatabase(Context context){
        super(context,DB_NAME,null,DB_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query;
        query = String.format("CREATE TABLE IF NOT EXISTS %s("+
                " %s INTEGER PRIMARY KEY AUTOINCREMENT, "+
                " %s TEXT)",DB_TABLE,COL_ID,COL_PATH);

        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    //CRUD
    //CREATE - CRIA UMA FOTO NO BD
    public long createPhotoInDB(Foto foto){
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PATH,foto.getPath());
        long id = database.insert(DB_TABLE,null,values);
        database.close();
        return id;
    }
    //RETRIEVE - TRAZER OS DADOS DO BD
    public ArrayList<Foto> retrievePhotoFromDB(){
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(DB_TABLE,null,null,
                null,null,null,null);
        ArrayList<Foto> photos = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                long id = cursor.getLong(cursor.getColumnIndex(COL_ID));
                String path = cursor.getString(cursor.getColumnIndex(COL_PATH));

                photos.add(new Foto(id,path));

            }while (cursor.moveToNext());
        }
        database.close();
        return photos;
    }
    //DELETE - DELETE FOTO WHERE ID
    public boolean deleteFoto(long id){
        SQLiteDatabase database = getWritableDatabase();
        String[] args = new String[1];
        args[0] = String.valueOf(id);
        boolean retorno = database.delete(DB_TABLE,"id=?",args) > 0;
        database.close();
        return retorno;
    }

    //UPDATE - UPDATE FOTO WHERE ID
    public void updateFoto(Foto foto){
        SQLiteDatabase database = getWritableDatabase();
        String query = String.format("UPDATE %s SET %s='%s' WHERE id=%s",DB_TABLE,COL_PATH,foto.getPath(),foto.getId());
        database.execSQL(query);
        database.close();
    }
}
