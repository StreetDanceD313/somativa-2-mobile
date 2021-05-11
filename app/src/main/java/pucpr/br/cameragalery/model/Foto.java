package pucpr.br.cameragalery.model;

public class Foto {
    public String path;
    public long id;

    public Foto(long id,String path){
        this.path = path;
        this.id=id;
    }

    public Foto(){

    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
