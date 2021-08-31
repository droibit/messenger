package com.droibit.looking2.timeline.ui.content.photo

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.size.Scale
import com.droibit.looking2.timeline.R
import com.droibit.looking2.timeline.databinding.ListItemPhotoBinding
import javax.inject.Provider

class PhotoListAdapter(
    private val lifecycleOwner: Provider<LifecycleOwner>,
) : ListAdapter<String, PhotoListAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            lifecycleOwner.get(),
            binding = ListItemPhotoBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.update(url = getItem(position))
    }

    class ViewHolder(
        private val lifecycleOwner: LifecycleOwner,
        private val binding: ListItemPhotoBinding
    ) : RecyclerView.ViewHolder(binding.root), ImageRequest.Listener {

        fun update(url: String) {
            binding.photo.load(url) {
                error(R.drawable.ic_cloud_off)
                scale(Scale.FIT)
                lifecycle(lifecycleOwner)
                listener(this@ViewHolder)
            }
        }

        override fun onStart(request: ImageRequest) {
            binding.loadingInProgress = true
        }

        override fun onError(request: ImageRequest, throwable: Throwable) {
            binding.photo.scaleType = ImageView.ScaleType.CENTER
            binding.loadingInProgress = false
        }

        override fun onSuccess(request: ImageRequest, metadata: ImageResult.Metadata) {
            binding.loadingInProgress = false
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }
}
