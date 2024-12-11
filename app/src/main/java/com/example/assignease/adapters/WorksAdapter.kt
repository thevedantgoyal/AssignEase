package com.example.assignease.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.assignease.R
import com.example.assignease.databinding.WorksFragmentItemViewBinding
import com.example.assignease.models.Works

class WorksAdapter(val onUnassignesClickBtn: (Works) -> Unit) : RecyclerView.Adapter<WorksAdapter.WorksViewHolder>() {
    class WorksViewHolder(val binding: WorksFragmentItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)


    private val diffUtil = object : DiffUtil.ItemCallback<Works>() {
        override fun areItemsTheSame(oldItem: Works, newItem: Works): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Works, newItem: Works): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorksViewHolder {
        return WorksViewHolder(WorksFragmentItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WorksViewHolder, position: Int) {
        val works = differ.currentList[position]
        val isExpanded = works.expanded

        holder.binding.apply {
            itemWorkTitle.text = works.workTitle
            itemWorkdescContent.text = works.workDescription
            itemWorkDate.text = works.workLastDate

            when(works.workPriority){
                "1" -> itemWorkOvalimage.setImageResource(R.drawable.green_oval)
                "2" -> itemWorkOvalimage.setImageResource(R.drawable.orange_oval)
                "3" -> itemWorkOvalimage.setImageResource(R.drawable.red_oval)

            }
            when(works.workStatus){
                "1" -> holder.binding.itemWorkStatus.text = "Pending"
                "2" -> holder.binding.itemWorkStatus.text = "Progress"
                "3" -> holder.binding.itemWorkStatus.text = "Completed"
            }

            itemWorkdescContent.visibility = if(isExpanded == true) View.VISIBLE else View.GONE
            itemWorkDescTxt.visibility = if(isExpanded == true) View.VISIBLE else View.GONE
            unassignedBtn.visibility = if(isExpanded == true) View.VISIBLE else View.GONE
//
            constraintLayout.setOnClickListener {
                isAnyItemExpanded(position)
                works.expanded = !works.expanded
                notifyItemChanged(position,0)
            }
//
            unassignedBtn.setOnClickListener { onUnassignesClickBtn(works) }
        }
    }

    private fun isAnyItemExpanded(position: Int) {
        val expandedItemIndex = differ.currentList.indexOfFirst { it.expanded }
        if(expandedItemIndex >= 0 && expandedItemIndex != position){
            differ.currentList[expandedItemIndex].expanded = false
            notifyItemChanged(expandedItemIndex , 0)

        }
    }

    override fun onBindViewHolder(
        holder: WorksViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if(payloads.isNotEmpty() && payloads[0] == 0){
            holder.binding.apply {
                itemWorkDescTxt.visibility = View.GONE
                itemWorkdescContent.visibility = View.GONE
                unassignedBtn.visibility = View.GONE

            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }
}