package gov.gao.epds.test;
public class UploadThread implements Runnable {

    private String fileLocation;

    public UploadThread(String s){
        this.fileLocation=s;
    }

    @Override
    public void run() {
       //your api call to upload file using fileLocation
    }

    @Override
    public String toString(){
        return this.fileLocation;
    }
}

