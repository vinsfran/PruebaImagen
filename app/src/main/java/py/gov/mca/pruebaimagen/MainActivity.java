package py.gov.mca.pruebaimagen;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends ActionBarActivity {
    private String selectedImagePath = "";
    final private int PICK_IMAGE = 1;
    final private int CAPTURE_IMAGE = 2;
    ImageView imgView;
    private String imgPath;
    private Bitmap photobmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgView = (ImageView) findViewById(R.id.imageView1);

        Button butCamera = (Button) findViewById(R.id.button1);
        butCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                getPhotoDialog();
            }
        });

        Button butGallery = (Button) findViewById(R.id.button2);
        butGallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                //hklhkkhkkllkl
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, ""), PICK_IMAGE);
            }
        });

    }

    private void getPhotoDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("TITULO");
            builder.setPositiveButton("Camara", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
                    startActivityForResult(intent, CAPTURE_IMAGE);

                }

            });
            builder.setNegativeButton("Galeria", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, ""), PICK_IMAGE);
                }

            });
        builder.create().show();



    }

    public Uri setImageUri() {
        // Store image in dcim
        File file = new File(Environment.getExternalStorageDirectory()
                + "/DCIM/", "image" + new Date().getTime() + ".png");
        Uri imgUri = Uri.fromFile(file);
        this.imgPath = file.getAbsolutePath();
        return imgUri;
    }

    public String getImagePath() {
        return imgPath;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == PICK_IMAGE) {
                selectedImagePath = getAbsolutePath(data.getData());
                photobmp = BitmapFactory.decodeFile(selectedImagePath);
                imgView.setImageBitmap(photobmp);

                //Codifica la imagen con Base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photobmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                //Se ejecuta en segundo plano para no colgar la aplicacion
                System.out.println("IMA BYT " + encodedImage);

            } else if (requestCode == CAPTURE_IMAGE) {
                selectedImagePath = getImagePath();
                photobmp = BitmapFactory.decodeFile(selectedImagePath);
                imgView.setImageBitmap(photobmp);

                //Codifica la imagen con Base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photobmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                //Se ejecuta en segundo plano para no colgar la aplicacion
                System.out.println("IMA BYT 2 " + encodedImage);
            } else {
                super.onActivityResult(requestCode, resultCode,
                        data);
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is
        // present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of
            // 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE
                    && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

    public String getAbsolutePath(Uri uri) {
        String[] projection = { MediaColumns.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
}