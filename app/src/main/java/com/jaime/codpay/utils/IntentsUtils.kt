package com.jaime.codpay.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

fun AbrirNavegacion(context: Context, direccionCompleta: String){
    val uri = Uri.parse("geo:0,0?q=${Uri.encode(direccionCompleta)}")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.setPackage("com.google.amdroid.apps.maps")

    if(intent.resolveActivity(context.packageManager) !=null){
        context.startActivity(intent)
    }else{
        val fallbackIntent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(fallbackIntent)
    }
}

fun LlamarTelefono(context: Context, numero: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$numero")
    }
    context.startActivity(intent)
}