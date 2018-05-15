package com.sdsmdg.rohan.testapp

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sdsmdg.rohan.attachablefloatingmenu.FloatingMenuManager
import kotlinx.android.synthetic.main.fragment_grid.*
import kotlinx.android.synthetic.main.item_grid.view.*

/**
 * Created by rohan on 26/1/18.
 */
class GridFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ) = inflater?.inflate(R.layout.fragment_grid, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.adapter = RvAdapter(activity)
        recycler_view.layoutManager = StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.VERTICAL)
    }

}

class RvAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val menuManager= FloatingMenuManager(context)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_grid, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun getItemCount() = 18

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val str = "Item $position"
        holder.itemView.text_view.text = str
        menuManager.addView(holder.itemView)
    }
}
