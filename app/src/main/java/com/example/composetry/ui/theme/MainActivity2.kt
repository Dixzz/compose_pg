package com.example.composetry.ui.theme

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.composetry.databinding.ActivityMain2Binding
import com.example.composetry.databinding.ItemRouteDetails2Binding
import com.example.composetry.databinding.ItemRouteDetailsBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

private const val TAG = "MainActivity2"

class MainActivity2 : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        val bs = binding.bottomRoot
        val bottomSheet = BottomSheetBehavior.from(bs.bottomSheetRoot)
        //bottomSheet.peekHeight = 200
//        bs.rvRouteDetails.setOnTouchListener { v, event ->
//            v.parent.requestDisallowInterceptTouchEvent(true);
//            v.onTouchEvent(event);
//            true
//        }
        bs.rvRouteDetails.adapter = RouteDetailsAdapter()
    }

    class RouteDetailsAdapter :
        androidx.recyclerview.widget.RecyclerView.Adapter<RouteDetailsViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RouteDetailsViewHolder {
            return RouteDetailsViewHolder.from(parent)
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(
            holder: RouteDetailsViewHolder,
            position: Int
        ) {
            holder.binding.childRv.setOnTouchListener { v, event ->
                v.parent.requestDisallowInterceptTouchEvent(true);
                v.onTouchEvent(event);
                true
            }
            holder.binding.childRv.adapter = ChildRouteDetailsAdapter()
        }

        override fun getItemCount(): Int {
            return 10
        }
    }

    class ChildRouteDetailsAdapter :
        androidx.recyclerview.widget.RecyclerView.Adapter<ChildRouteDetailsViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ChildRouteDetailsViewHolder {
            return ChildRouteDetailsViewHolder.from(parent)
        }

        override fun onBindViewHolder(
            holder: ChildRouteDetailsViewHolder,
            position: Int
        ) {
        }

        override fun getItemCount(): Int {
            return 10
        }
    }

    class ChildRouteDetailsViewHolder(binding: ItemRouteDetails2Binding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ChildRouteDetailsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemRouteDetails2Binding.inflate(layoutInflater, parent, false)
                return ChildRouteDetailsViewHolder(binding)
            }
        }
    }

    class RouteDetailsViewHolder(val binding: ItemRouteDetailsBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): RouteDetailsViewHolder {
                return RouteDetailsViewHolder(
                    ItemRouteDetailsBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }
        }

        init {
            itemView.setOnClickListener {
                println("clicked")
            }
        }
    }
}