package com.example.autores;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private EditText inputBook;
    private TextView bookTitle;
    private  TextView bookAuthor;
    private TextView categories;
    private TextView description;
    private ImageView thumbnail;
    private TextView amount;
    private  TextView averageRating;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      inputBook   = (EditText)findViewById(R.id.inputbook);
      bookTitle = (TextView) findViewById(R.id.title);
      bookAuthor = (TextView)findViewById(R.id.author);
      categories = (TextView)findViewById(R.id.category);

        amount = (TextView) findViewById(R.id.amount);
        averageRating = (TextView) findViewById(R.id.averageRating);
        description = (TextView) findViewById(R.id.description);


    }



    public void searchbook(View view) {

        String searchstring= inputBook.getText().toString();

        new GetBook(bookTitle,bookAuthor,categories, thumbnail, amount, averageRating,description).execute(searchstring);

    }

    public class GetBook extends AsyncTask<String,Void,String>{

        private WeakReference<TextView> mTextTitle;
        private  WeakReference<TextView> mTextAuthor;
        private  WeakReference<TextView> mCategories;
        private WeakReference<ImageView> mThumbnail;
        private  WeakReference<TextView> mAmount;
        private  WeakReference<TextView> mAverageRating;
        private  WeakReference<TextView> mDescription;





        public GetBook(TextView mTextTitle, TextView mTextAuthor, TextView mCategories, ImageView mThumbnail, TextView mAmount, TextView mAverageRating, TextView mDescription) {
            this.mTextTitle = new WeakReference<>(mTextTitle);
            this.mTextAuthor = new WeakReference<>(mTextAuthor);
            this.mCategories = new WeakReference<>(mCategories);
            this.mThumbnail = new WeakReference<>(mThumbnail);
            this.mAmount = new WeakReference<>(mAmount);
            this.mAverageRating = new WeakReference<>(mAverageRating);
            this.mDescription = new WeakReference<>(mDescription);


        }


        @Override
        protected String doInBackground(String... strings) {


            return NetUtilities.getBookInfo(strings[0]);
        }

        @Override
        protected void  onPostExecute(String s){

            super.onPostExecute(s);
            try{
                JSONObject jsonObject = new JSONObject(s);
                JSONArray itemsArray = jsonObject.getJSONArray("items");
                int i= 0;
                String title = null;
                String author = null;
                String categories = null;
                String thumbnail = null;
                String amount = null;
                String averageRating = null;
                String description = null;




                while(i < itemsArray.length() &&(title == null && author == null && categories == null && thumbnail == null && amount == null && averageRating ==null
                && description ==null)){
                    JSONObject book = itemsArray.getJSONObject(i);
                    JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                    JSONObject saleInfo = book.getJSONObject("saleInfo");




                    try{
                        title = volumeInfo.getString("title");
                        author =volumeInfo.getString("authors");
                        categories = volumeInfo.getString("categories");
                        thumbnail = volumeInfo.getJSONObject("imageLinks").getString("thumbnail");
                        amount = saleInfo.getJSONObject("listPrice").getString("amount");
                        averageRating = volumeInfo.getString("averageRating");
                        description = volumeInfo.getString("description");


                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    i++;
                    if(title != null && author != null  && thumbnail!=null && amount !=null && averageRating !=null && description !=null){
                        mTextTitle.get().setText(title);
                        mTextAuthor.get().setText(author);
                        mCategories.get().setText(categories);



                        ImageView ivBasicImage = (ImageView) findViewById(R.id.bookImage);
                        Picasso.with(getApplicationContext()).load("https://miracomosehace.com/wp-content/uploads/2020/08/icono-play-books.jpg").
                                resize(150, 200).into(ivBasicImage);

                        mAmount.get().setText("S/."+" "+amount);
                        mAverageRating.get().setText("Puntuacion promedio de"+" "+averageRating);
                        mDescription.get().setText(description);

                    }else{
                        mTextTitle.get().setText("No existen resultados para la consulta");
                        mTextAuthor.get().setText("");
                      mCategories.get().setText("");
                      mThumbnail.get().setImageURI(Uri.parse(""));
                        mAmount.get().setText("");
                        mAverageRating.get().setText("");
                        mDescription.get().setText("");

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


}