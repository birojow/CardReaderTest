package app.fabianomello.cardreadertest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat.startActivityForResult

import io.card.payment.CardIOActivity

import android.content.Intent
import android.graphics.Color
import android.widget.TextView
import android.widget.Toast
import io.card.payment.CardType
import io.card.payment.CreditCard

class MainActivity : AppCompatActivity() {

    private val MY_SCAN_REQUEST_CODE = 42
    private lateinit var readCardButton: Button
    private lateinit var numberTextView: TextView
    private lateinit var holderTextView: TextView
    private lateinit var expirationDateTextView: TextView
    private lateinit var issuerTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        readCardButton = findViewById(R.id.read_card_button)
        readCardButton.setOnClickListener {
            onScanPress()
        }

        numberTextView = findViewById(R.id.number_text_view)
        holderTextView = findViewById(R.id.holder_text_view)
        expirationDateTextView = findViewById(R.id.expiration_date_text_view)
        issuerTextView = findViewById(R.id.issuer_text_view)
    }

    private fun onScanPress() {

        val scanIntent = Intent(this, CardIOActivity::class.java)

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true)
        scanIntent.putExtra(CardIOActivity.EXTRA_SCAN_EXPIRY, true)
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, true)
        scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true)
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false)
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false)

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MY_SCAN_REQUEST_CODE) {

            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {

                val scanResult: CreditCard? =  data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT)

                numberTextView.text = scanResult?.cardNumber

                val month = scanResult?.expiryMonth.toString()
                val year = scanResult?.expiryYear.toString()
                val expirationDate = "$month/$year"
                expirationDateTextView.text = expirationDate
                if (scanResult?.isExpiryValid == true) {
                    expirationDateTextView.setTextColor(numberTextView.textColors)
                } else {
                    expirationDateTextView.setTextColor(Color.RED)
                }

                holderTextView.text = scanResult?.cardholderName
                issuerTextView.text = CardType.fromCardNumber(scanResult?.cardNumber).name

            } else {
                Toast.makeText(
                    this,
                    "Scan was canceled.",
                    Toast.LENGTH_SHORT
                )
            }

        } else {
            Toast.makeText(
                this,
                "Wrong code request",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}