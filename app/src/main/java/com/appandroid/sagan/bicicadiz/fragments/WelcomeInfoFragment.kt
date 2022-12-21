package com.appandroid.sagan.bicicadiz.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.appandroid.sagan.bicicadiz.R

class WelcomeInfoFragment: DialogFragment()  {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it,R.style.AlertDialogCustom)
            val inflater = requireActivity().layoutInflater
            builder.setView(inflater.inflate(R.layout.info_dialog, null))
                .setPositiveButton("¡Vamos!"
                ) { _, _ ->
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}