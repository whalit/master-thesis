package com.example.master

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class History : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        // Initialize the emergency contacts with click listeners
        setupEmergencyContacts(view)

        return view
    }

    private fun setupEmergencyContacts(view: View) {
        val contacts = listOf(
            Triple("General Emergency", "112", R.id.emergency_general),
            Triple("Non-Emergency Medical Assistance", "1733", R.id.emergency_medical),
            Triple("PHARE Service (Brussels-Capital Region)", "02 800 82 03", R.id.emergency_phare),
            Triple("AVIQ (Wallonia)", "0800 16 061", R.id.emergency_aviq),
            Triple("VAPH (Flanders)", "02 249 30 00", R.id.emergency_vaph),
            Triple("V!GO (Wheelchair Repair)", "+32 11 85 01 20", R.id.emergency_vigo),
            Triple("MediTechnics (Wheelchair Repair)", "+32 16 35 66 00", R.id.emergency_meditechnics),
            Triple("GRIP vzw", "03 216 43 04", R.id.emergency_grip),
            Triple("European Disability Forum", "+32 2 282 46 00", R.id.emergency_edf),
            Triple("ANLH", "02 424 55 50", R.id.emergency_anlh)
        )

        for ((name, number, viewId) in contacts) {
            view.findViewById<TextView>(viewId)?.setOnClickListener {
                showContactNumber(name, number)
            }
        }
    }

    private fun showContactNumber(name: String, number: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(name)
            .setMessage("Contact Number: $number")
            .setPositiveButton("Call") { _, _ -> initiateCall(number) }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun initiateCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CALL_PHONE),
                CALL_PERMISSION_REQUEST_CODE
            )
        } else {
            startActivity(intent)
        }
    }

    companion object {
        private const val CALL_PERMISSION_REQUEST_CODE = 2
    }
}
