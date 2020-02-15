package me.andreandyp.dias.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseExpandableListAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import me.andreandyp.dias.R
import me.andreandyp.dias.databinding.AlarmaItemChildBinding
import me.andreandyp.dias.databinding.AlarmaItemParentBinding


class AlarmasAdapter(
    private var context: Context,
    private val titleList: List<String>,
    private val dataList: HashMap<String, List<String>>,
    var viewLifecycleOwner: LifecycleOwner
) : BaseExpandableListAdapter(){

    // Utilizamos data binding para ambos items
    lateinit var bindingParent: AlarmaItemParentBinding
    lateinit var bindingChild: AlarmaItemChildBinding

    // 7 días de la semana
    override fun getGroupCount(): Int = this.titleList.size

    // Cada día tiene solo un hijo (Donde se establece las configuraciones de la alarma)
    override fun getChildrenCount(groupPosition: Int): Int = this.dataList[this.titleList[groupPosition]]!!.size


    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return this.dataList[this.titleList[groupPosition]]!![childPosition]

    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val expandedListText = getChild(groupPosition, childPosition) as String

        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            bindingChild = DataBindingUtil.inflate(inflater, R.layout.alarma_item_child, parent, false)
            bindingChild.lifecycleOwner = viewLifecycleOwner
        }
        bindingChild.diaChild.text = expandedListText

        return bindingChild.root
    }

    override fun getGroup(groupPosition: Int): Any {
        return this.titleList[groupPosition]

    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {

        val listTitle = getGroup(groupPosition) as String
        if (convertView == null) {
            Log.i("PRUEBA", "padre inflado")

            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            bindingParent = DataBindingUtil.inflate(inflater, R.layout.alarma_item_parent, parent, false)
        }
        else{
            Log.i("PRUEBA", "NO NULL")
        }

        bindingParent.diaParent.text = listTitle
        bindingParent.lifecycleOwner = viewLifecycleOwner

        return bindingParent.root
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

    override fun hasStableIds(): Boolean = false

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

}
