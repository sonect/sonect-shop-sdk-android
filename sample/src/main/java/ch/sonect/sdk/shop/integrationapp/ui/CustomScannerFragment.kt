package ch.sonect.sdk.shop.integrationapp.ui

import android.os.Bundle
import android.view.View
import ch.sonect.sdk.shop.integrationapp.R
import ch.sonect.sdk.shop.transactions.screen.scanner.ScannerFragment
import kotlinx.android.synthetic.main.fragment_custom_scanner.*

class CustomScannerFragment : ScannerFragment() {

    override fun getLayoutId(): Int = R.layout.fragment_custom_scanner

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnScannerComplete.setOnClickListener {
            listener.onScan("123456")
        }
    }

}