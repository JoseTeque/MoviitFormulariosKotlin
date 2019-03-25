package m.google.moviitformularioskotlin

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.wonderkiln.camerakit.*
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_codigo_qr.*
import java.lang.StringBuilder
import java.lang.reflect.Proxy
import java.util.*
import kotlin.collections.HashMap


class CodigoQRActivity : AppCompatActivity() {

    private lateinit var waiting_dialog:android.app.AlertDialog
    private lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_codigo_qr)

        db= FirebaseFirestore.getInstance()

        requestedOrientation = (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        waiting_dialog = SpotsDialog.Builder().setContext(this).setMessage("Please wait").setCancelable(false).build()

        BtnDetectar.setOnClickListener {
            cameraView.start()
            cameraView.captureImage()
        }

        cameraView.addCameraKitListener(object : CameraKitEventListener{
            override fun onVideo(p0: CameraKitVideo?) {

            }

            override fun onEvent(p0: CameraKitEvent?) {

            }

            override fun onImage(p0: CameraKitImage?) {
                waiting_dialog.show()
                var bitmap:Bitmap = p0!!.bitmap
                bitmap= Bitmap.createScaledBitmap(bitmap,cameraView.getWidth(),cameraView.getHeight(),false)
                cameraView.stop()

                runDetector(bitmap)
            }

            override fun onError(p0: CameraKitError?) {

            }

        })

    }

    private fun runDetector(bitmap: Bitmap?) {
        val  image= FirebaseVisionImage.fromBitmap(bitmap!!)
        val option= FirebaseVisionBarcodeDetectorOptions.Builder().setBarcodeFormats(
            FirebaseVisionBarcode.FORMAT_QR_CODE,
            FirebaseVisionBarcode.FORMAT_CODABAR
        ).build()

        val detector= FirebaseVision.getInstance().getVisionBarcodeDetector(option)
        detector.detectInImage(image).addOnSuccessListener {
             result-> proccessResult(result)
        }.addOnFailureListener{
            e-> Toast.makeText(this,""+ e.message, Toast.LENGTH_SHORT).show()
        }

    }

    private fun proccessResult(result: List<FirebaseVisionBarcode>) {
        for (item in result) {

            val rawValue = item.rawValue
            val valueType = item.valueType
            // See API reference for complete list of supported types
            when (valueType) {
                FirebaseVisionBarcode.TYPE_TEXT -> {
                  val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setMessage(rawValue)
                    alertDialog.setPositiveButton("OK") { alerDilog, which-> alerDilog.dismiss()}
                    val dialog= alertDialog.create()
                    dialog.show()
                }
                FirebaseVisionBarcode.TYPE_URL -> {
                   val intent= Intent(Intent.ACTION_VIEW, Uri.parse(rawValue))
                    startActivity(intent)
                }

                FirebaseVisionBarcode.TYPE_CONTACT_INFO ->{

                   val name= item.contactInfo!!.name!!.formattedName.toString()
                    val address= item.contactInfo!!.addresses[0].addressLines[0].toString()
                    val email= item.contactInfo!!.emails[0].address.toString()

                    val info= StringBuilder("Name: ").append(name).append("\n")
                        .append("Address: ").append(address).append("\n")
                        .append("Email: ").append(email).toString()

                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setMessage(info)
                    alertDialog.setPositiveButton("OK",{alerDialog,which->

                        val intent= Intent(this, FormularioActivity::class.java)
                        intent.putExtra("name",name)
                        intent.putExtra("address",address)
                        intent.putExtra("email",email)
                        startActivity(intent)

                        alerDialog.dismiss()

                    })
                    val dialog= alertDialog.create()



                    dialog.show()
                }

            }
        }

        waiting_dialog.dismiss()


    }

    override fun onResume() {
        super.onResume()
        cameraView.start()
    }

    override fun onStop() {
        super.onStop()
        cameraView.stop()
    }
}
