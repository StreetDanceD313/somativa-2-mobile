package pucpr.br.cameragalery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pucpr.br.cameragalery.adapters.FotoAdapter;
import pucpr.br.cameragalery.model.Foto;
import pucpr.br.cameragalery.repository.GaleryRepository;

import static android.graphics.Color.argb;

public class RegisterPicture extends AppCompatActivity {

    static final int CAMERA_PERMISSION_CODE = 2001;
    static final int CAMERA_INTENT_CODE = 3001;
    static final int GALERY_INTENT_CODE = 4001;
    GaleryRepository galeryRepository;
    ImageView imageViewCamera;
    String picturePath;

    Boolean grayScaleApplied;

    ConstraintLayout constraintLayoutRegister;
    RecyclerView recyclerView;
    FotoAdapter adapter;
    SeekBar seekBarOpacity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        grayScaleApplied = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        imageViewCamera = findViewById(R.id.imageViewCamera);
        constraintLayoutRegister = findViewById(R.id.constraintLayoutRegister);
        seekBarOpacity = findViewById(R.id.seekBarOpacity);
        seekBarOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                constraintLayoutRegister.setBackgroundColor(
                        argb(progress,0,0,0)
                );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        galeryRepository = GaleryRepository.getInstance();
        galeryRepository.setContext(RegisterPicture.this);

        recyclerView = findViewById(R.id.recyclerViewGalery);
        adapter = new FotoAdapter();

        adapter.setClickListener(new FotoAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View view) {

                if(galeryRepository.getSelectedId() != null && (long) position == galeryRepository.getSelectedId()){
                    galeryRepository.setSelectedId(null);
                    imageViewCamera.setImageURI(null);
                    Log.d("ChangeSelectedId","vazio");
                }else{
                    galeryRepository.setSelectedId((long) position);
                    imageViewCamera.setImageURI(
                            Uri.fromFile(
                                    new File(
                                            galeryRepository.getFotos().get((int) position).getPath()
                                    )
                            )
                    );
                    grayScaleApplied = false;
                    Log.d("ChangeSelectedId",galeryRepository.getSelectedId().toString());
                }

            }

            @Override
            public boolean onItemLongClick(int position, View view) {
                galeryRepository.deleteFoto(position);
                adapter.notifyItemRemoved(position);
                return true;
            }

        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
    }

    @Override
    protected void onResume() {
        super.onResume();
        String paths = "";
        for(Foto foto:galeryRepository.getFotos()){
            paths += foto.getId() + " - " +foto.getPath()+"\n";
        }
        if(paths == ""){
            paths = "EMPTYYY";
        }
        Log.d(
                "ImagesStocked",
                paths
        );
    }

    public void buttonCameraClick(View view){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestCameraPermissions();
        }else{
            sendCameraIntent();
        }

    }
    public void buttonGrayScale(View view){
        Log.d("grayScale","Clicou no Botão.");
        String path = galeryRepository.getFotos().get(Integer.parseInt(galeryRepository.getSelectedId().toString())).getPath();
        if(galeryRepository.getSelectedId() != null){
            if(grayScaleApplied){
                File f = new File(path);
                imageViewCamera.setImageURI(Uri.fromFile(f));
                grayScaleApplied = false;
            }else{
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                bitmap = toGrayscale(bitmap);
                imageViewCamera.setImageBitmap(bitmap);
                grayScaleApplied = true;
            }

        }else{
            Toast.makeText(
                    RegisterPicture.this,
                    "Selecione uma Foto para aplicar o Filtro",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public void buttonGaleryClick(View view){
        sendGaleryIntent();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestCameraPermissions(){
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{
                        Manifest.permission.CAMERA
                },CAMERA_PERMISSION_CODE);
            }else{
                sendCameraIntent();
            }
        }else{
            Toast.makeText(
                    RegisterPicture.this,
                    "Não foi possível encontrar uma câmera",
                    Toast.LENGTH_LONG
            ).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(RegisterPicture.this,"Permissão de acesso a câmera negado.",Toast.LENGTH_LONG).show();
            }else{
                sendCameraIntent();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean continuar = false;
        if(resultCode != RESULT_OK){
            Toast.makeText(
                    RegisterPicture.this,
                    "Não foi possível retornar a foto do aplicativo da Câmera",
                    Toast.LENGTH_LONG
            ).show();
        }else{
            if(requestCode == CAMERA_INTENT_CODE){
                continuar = true;
            }else if(requestCode == GALERY_INTENT_CODE){
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                picturePath = c.getString(columnIndex);
                Log.d("PicturePath",picturePath);
                File file = new File(picturePath);
                Log.d("FileExists",file.exists() ? "existe" : "Não");
                c.close();
                continuar = true;
            }
            if(continuar){
                File file = new File(picturePath);
                if(file.exists()){
                    imageViewCamera.setImageURI(Uri.fromFile(file));
                    Foto foto = new Foto();
                    foto.setPath(picturePath);

                    if(galeryRepository.getSelectedId() != null){
                        foto.setId(galeryRepository.getFotos().get(Integer.parseInt(galeryRepository.getSelectedId().toString())).getId());
                        galeryRepository.updateFoto(foto);
                        adapter.notifyItemChanged(Integer.parseInt(galeryRepository.getSelectedId().toString()));
                        galeryRepository.setSelectedId(null);
                        Toast.makeText(
                                RegisterPicture.this,
                                "Foto atualizada na galeria",
                                Toast.LENGTH_LONG
                        ).show();
                    }else{
                        if(galeryRepository.addFoto(foto)){
                            Toast.makeText(
                                    RegisterPicture.this,
                                    "Foto adicionada na galeria",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                }else{
                    Toast.makeText(
                            RegisterPicture.this,
                            "Arquivo recebido do serviço não existe",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        }

    }

    public void sendCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION,true);
        if(intent.resolveActivity(getPackageManager()) != null){
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String picName = "pic_"+timeStamp;
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File pictureFile = null;
            try {
                pictureFile = File.createTempFile(picName,".jpg",dir);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(RegisterPicture.this,"Não foi possível criar o arquivo Temporário",Toast.LENGTH_LONG).show();
            }

            if(pictureFile != null){
                picturePath = pictureFile.getAbsolutePath();
                Uri photoUri = FileProvider.getUriForFile(
                        RegisterPicture.this,
                        "pucpr.br.cameragalery.fileprovider",
                        pictureFile
                );
                intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(intent,CAMERA_INTENT_CODE);
            }else{
                Toast.makeText(RegisterPicture.this,"Não foi possível abrir a Câmera",Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(RegisterPicture.this,"Algo deu Errado",Toast.LENGTH_LONG).show();
        }
    }

    public void sendGaleryIntent(){
        Intent intent = new   Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,GALERY_INTENT_CODE);
    }
}