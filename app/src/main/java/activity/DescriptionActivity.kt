package activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.akriti.bookhub2.R
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.json.JSONObject
import util.ConnectionManager
import java.lang.Exception
import java.util.prefs.PreferencesFactory

class DescriptionActivity : AppCompatActivity() {
    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor: TextView
    lateinit var txtBookPrice: TextView
    lateinit var txtBookRating: TextView
    lateinit var imgBookImage: ImageView
    lateinit var txtBookDesc: TextView
    lateinit var btnAddToFavourite:Button
    lateinit var ProgressBar:ProgressBar
    lateinit var ProgressLayout:RelativeLayout
    lateinit var toolbar: Toolbar

    var bookId:String ?= "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)
        txtBookName= findViewById(R.id.txtBookName)
        txtBookAuthor= findViewById(R.id.txtBookAuthor)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating= findViewById(R.id.txtBookRating)
        imgBookImage= findViewById(R.id.imgBookImage)
        txtBookDesc= findViewById(R.id.Descbook)
        btnAddToFavourite= findViewById(R.id.btnAddtofavourites)
        ProgressBar= findViewById(R.id.progressBar)
        ProgressBar.visibility=View.VISIBLE
        ProgressLayout= findViewById(R.id.progressLayout)
        ProgressLayout.visibility=View.VISIBLE
        toolbar= findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title="Book Details"

        if(intent!=null) {
            bookId = intent.getStringExtra("book_Id")
        }
        else{
            finish()
            Toast.makeText( this@DescriptionActivity , "Some unexpected error occured!", Toast.LENGTH_SHORT).show()
        }

        if(bookId=="100")
        {
            finish()
            Toast.makeText( this@DescriptionActivity , "Some unexpected error occured!", Toast.LENGTH_SHORT).show()
        }

        val queue = Volley.newRequestQueue( this@DescriptionActivity)

        var url =" http://13.235.250.119/v1/book/get_book/"

        val jsonParams = JSONObject()
        jsonParams.put("book_Id",bookId)

        if(ConnectionManager().checkConnectivity(this@DescriptionActivity)){

         val jsonRequest= object :JsonObjectRequest(Method.POST,url,jsonParams, Response.Listener {

             try {
                 val success = it.getBoolean("success")
                 if (success){
                   val bookJsonObject = it.getJSONObject("book_data")
                     ProgressLayout.visibility = View.GONE

                     Picasso.get().load(bookJsonObject.getString("image")).error(R.drawable.default_book_cover).into(imgBookImage)
                     txtBookName.text = bookJsonObject.getString("name")
                     txtBookAuthor.text = bookJsonObject.getString("author")
                     txtBookPrice.text = bookJsonObject.getString("price")
                     txtBookRating.text = bookJsonObject.getString("rating")
                     txtBookDesc.text = bookJsonObject.getString("description")

                 }
                 else{
                     Toast.makeText(this@DescriptionActivity,"Some unexpected error occured",Toast.LENGTH_SHORT).show()
                 }
             }catch (e:Exception){
                 Toast.makeText(this@DescriptionActivity,"Some unexpected error occured",Toast.LENGTH_SHORT).show()

             }
         }, Response.ErrorListener {
             Toast.makeText(this@DescriptionActivity,"Volley error $it",Toast.LENGTH_SHORT).show()

         }){
             override fun getHeaders(): MutableMap<String, String> {
                 val headers =  HashMap<String,String>()
                 headers["Content-type"]="application/json"
                 headers["token"]="807e08a454fde6"
                 return headers
             }
         }
       queue.add(jsonRequest)
        }
        else{
            val dialog = AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Open Settings")
            dialog.setPositiveButton("Go to Settings"){text,listener ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("Exit"){text,listener ->
                ActivityCompat.finishAffinity(this@DescriptionActivity)

            }
            dialog.create()
            dialog.show()

        }
    }
}