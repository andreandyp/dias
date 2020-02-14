package me.andreandyp.dias.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter

class AlarmasAdapter(
    private var context: Context,
    private val titleList: List<String>,
    private val dataList: HashMap<String, List<String>>
)