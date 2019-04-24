package com.example.persistenciadedatos

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        bt_save.setOnClickListener{
            with(sharedPref.edit()){
                putString(getString(R.string.save_mensaje_key), et_option.text.toString())
                commit()
            }

            tv_data.text = et_option.text.toString()
        }

        var mensaje = sharedPref.getString(getString(R.string.save_mensaje_key), "")
        tv_data.text = mensaje

        bt_write_internal.setOnClickListener{
            mensaje = sharedPref.getString(getString(R.string.save_mensaje_key), "")

            tv_data.text = mensaje

            val filename = "mensaje.txt"

            val fileContent = "mensaje: $mensaje"

            openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(fileContent.toByteArray())
            }
            Toast.makeText(this, "Se guardo en la memoria interna!!!", Toast.LENGTH_SHORT).show()
        }

        bt_read_internal.setOnClickListener{
            val filename = "mensaje.txt"
            openFileInput(filename).use {
                val text = it.bufferedReader().readText()
                tv_data.text = text
            }
        }

        bt_write_external.setOnClickListener{
            mensaje = sharedPref.getString(getString(R.string.save_mensaje_key), "")
            val filename = "mensaje.txt"
            try {
                var tarjetaSD = Environment.getExternalStorageDirectory()
                var rutaArchivo = File(tarjetaSD.path, filename)
                var creaArchivo = OutputStreamWriter(FileOutputStream(rutaArchivo))

                creaArchivo.write("mensaje: $mensaje")
                creaArchivo.flush()
                creaArchivo.close()

                Toast.makeText(this, "Se guardo en la memoria externa!!!", Toast.LENGTH_SHORT).show()

            } catch (e : FileNotFoundException){
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }



    private var VALOR_RETORNO = 1

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION)
        } else {
            write()
        }
    }

    companion object {
        const val REQUEST_PERMISSION = 1
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                write()
            }
        }
    }

    private fun write() {
        val dir = "${Environment.getExternalStorageDirectory()}/$packageName"
        File(dir).mkdirs()
        val file = "%1\$tY%1\$tm%1\$td%1\$tH%1\$tM%1\$tS.log".format(Date())
        File("$dir/$file").printWriter().use {
            it.println("text")
        }
    }
}
