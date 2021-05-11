package pucpr.br.cameragalery.repository;

import android.content.Context;

import java.util.ArrayList;

import pucpr.br.cameragalery.database.GaleryDatabase;
import pucpr.br.cameragalery.model.Foto;

public class GaleryRepository {
    private static GaleryRepository instance = new GaleryRepository();

    private GaleryDatabase database;
    private ArrayList<Foto> fotos;
    private Context context;
    private Long selectedId;


    private GaleryRepository(){
    }
    public static GaleryRepository getInstance(){
        return instance;
    }

    public void setContext(Context context){
        this.context =context;
        database = new GaleryDatabase(context);
        fotos = database.retrievePhotoFromDB();
    }

    public ArrayList<Foto> getFotos(){
        return fotos;
    }

    public boolean addFoto(Foto foto){
        long id = database.createPhotoInDB(foto);
        if(id > 0){
            foto.setId(id);
            fotos.add(foto);
            return true;
        }else{
            return false;
        }
    }

    public boolean deleteFoto(long id){
        Boolean retorno = database.deleteFoto(fotos.get((int) id).getId());
        fotos.remove((int) id);
        return retorno;
    }

    public void updateFoto(Foto newFoto){
        for(int i=0;i<fotos.size();i++){
            Foto foto = fotos.get(i);
            if(foto.getId() == newFoto.getId()){
                fotos.set(i,newFoto);
            }
        }
        database.updateFoto(newFoto);
    }

    public Long getSelectedId() {
        return selectedId;
    }
    public void setSelectedId(Long selectedId) {
        this.selectedId = selectedId;
    }

}
