package com.miv2nir.project

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import java.sql.Timestamp
import kotlin.concurrent.thread
import kotlin.math.abs


class MainActivity : AppCompatActivity(),View.OnClickListener {
    //init vars, lateinit used for potential referencing elsewhere although idk if i ever will
    lateinit var clickerButton:Button
    lateinit var option1:Button
    lateinit var option2:Button
    lateinit var option3:Button
    lateinit var wipeout:Button
    var counter:Int =0
    var num1:Int=1
    var num2:Int=0
    var num3:Int=0
    var stamp:Timestamp=Timestamp(System.currentTimeMillis())
    lateinit var textView:TextView

    lateinit var sharedPreferences: SharedPreferences
    //val path:String=(filesDir.toString()+"/save.txt")
    override fun onPause() {
        super.onPause()
        save()
    }
    fun computeSecs(time:String):Int{
        val h=time.split(':')[0].toInt()
        val m=time.split(':')[1].toInt()
        val s=time.split(':')[2].split('.')[0].toInt()
        return (h*3600+m*60+s)

    }
    fun countTime(time1:Timestamp, time2:Timestamp):Int{
        val t1=time1.toString().split(' ')[1]
        val t2=time2.toString().split(' ')[1]
        return (abs(computeSecs(t1)-computeSecs(t2))/15)
    }

    fun load()
    {
        /*
        var savefile:File=File(path)
        val isFileThere:Boolean=savefile.createNewFile()
        if (!isFileThere)
        {
            savefile.createNewFile()
            savefile.writeText(
                "counter: 0\n" +
                        "option_1: 1\n"+
                        "option_2: 0\n"+
                        "option_3: 0\n"
            )
            counter=0
            num1=1
            num2=0
            num3=0
        }
        else {
            val things = savefile.readLines()
            counter = things[0].split(' ')[1].toInt()
            num1 = things[1].split(' ')[1].toInt()
            num2 = things[2].split(' ')[1].toInt()
            num3 = things[3].split(' ')[1].toInt()
        }
         */
        counter=sharedPreferences.getInt("counter",0)
        num1=sharedPreferences.getInt("option1",1)
        num2=sharedPreferences.getInt("option2",0)
        num3=sharedPreferences.getInt("option3",0)
        stamp=Timestamp.valueOf(sharedPreferences.getString("stamp",Timestamp(System.currentTimeMillis()).toString()))
        //SimpleDateFormat
        //val date=SimpleDateFormat("yyyy-mm-dd HH:mm:ss.SSS").parse(stamp.toString())

    }
    fun refreshViews()
    {
        textView.setText(counter.toString())
        option1.setText((num1 - 1).toString())
        option2.setText((num2).toString())
        option3.setText((num3).toString())
    }
    fun save() //will override the old file, intentional
    {
        //val path:String=(filesDir.toString()+"/save.txt")
        /*val savefile:File=File(path)

            savefile.writeText(
                "counter: "+counter.toString()+"\n" +
                        "option_1: "+num1.toString()+"\n"+
                        "option_2: "+num2.toString()+"\n"+
                        "option_3: "+num3.toString()+"\n"
            )*/
        sharedPreferences.edit().putInt("counter",counter).apply()
        sharedPreferences.edit().putInt("option1",num1).apply()
        sharedPreferences.edit().putInt("option2",num2).apply()
        sharedPreferences.edit().putInt("option3",num3).apply()
        sharedPreferences.edit().putString("stamp",stamp.toString()).apply()
        sharedPreferences.edit().apply()
        textView.setText(counter.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //var values insert
        sharedPreferences=this.getSharedPreferences(
            "com.miv2nir.project", Context.MODE_PRIVATE)


        load()
        clickerButton=findViewById<Button>(R.id.clickerButton)
        option1=findViewById<Button>(R.id.option1)
        option2=findViewById<Button>(R.id.option2)
        option3=findViewById<Button>(R.id.option3)
        wipeout=findViewById<Button>(R.id.wipeout)
        textView=findViewById<TextView>(R.id.textCenter)
        refreshViews()
        //textView.setText(counter.toString())
        clickerButton.setOnClickListener(this)
        option1.setOnClickListener(this)
        option2.setOnClickListener(this)
        option3.setOnClickListener(this)
        wipeout.setOnClickListener(this)

        //save file handling





        //counter thread
        thread(start=true) {
            while (true) {
                Thread.sleep(1000)
                counter += num2
                val precalc=num3*(countTime(stamp,Timestamp(System.currentTimeMillis())))
                if (precalc>0)
                {
                    counter+=precalc
                    stamp=Timestamp(System.currentTimeMillis())

                }
                println(num2.toString()+' '+precalc.toString())

                runOnUiThread { textView.setText(counter.toString()) }
                //could do some saving too there btw
                //save() i moved it to onPause override
            }
        }
            //getfilesdir
    }

    override fun onClick(p0: View?) { //button processes
        when(p0!!.id){
            R.id.clickerButton ->{
                println("clickin")
                counter+=num1

                textView.setText(counter.toString())
            }
            R.id.option1 ->{
                if (counter>=100) {
                    counter-=100

                    textView.setText(counter.toString())

                    num1 += 1

                    option1.setText((num1 - 1).toString())
                }
            }
            R.id.option2 ->{
                if (counter>=500){
                    counter-=500

                    textView.setText(counter.toString())

                    num2+=1

                    option2.setText((num2).toString())
                }
            }
            R.id.option3 ->{
                if (counter>=2000){
                    counter-=2000

                    textView.setText(counter.toString())

                    num3+=1

                    option3.setText((num3).toString())
                }
            }
            R.id.wipeout ->{
                val b=AlertDialog.Builder(this)
                b.setMessage("Are you sure you'd like to reset the game?")
                    .setCancelable(false)
                    .setPositiveButton("Yes"){ dialog,id ->
                        sharedPreferences.edit().remove("counter").apply()
                        sharedPreferences.edit().remove("option1").apply()
                        sharedPreferences.edit().remove("option2").apply()
                        sharedPreferences.edit().remove("option3").apply()
                        sharedPreferences.edit().remove("stamp").apply()
                        sharedPreferences.edit().apply()
                        counter =0
                        num1=1
                        num2=0
                        num3=0
                        stamp=Timestamp(System.currentTimeMillis())
                        refreshViews()
                    }
                    .setNegativeButton("No") { dialog, id ->
                        // Dismiss the dialog
                        dialog.dismiss()
                    }
                val alert=b.create()
                alert.show()
                //println("wipe")
            }

        }
    }
}
