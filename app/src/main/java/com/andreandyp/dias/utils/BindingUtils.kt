package com.andreandyp.dias.utils

import android.widget.NumberPicker
import androidx.databinding.BindingAdapter

@BindingAdapter("valor")
fun NumberPicker.establecerValor(valor: Int) {
    this.value = valor
}
