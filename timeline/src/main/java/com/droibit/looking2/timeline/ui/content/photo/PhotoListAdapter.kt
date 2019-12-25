package com.droibit.looking2.timeline.ui.content.photo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.decode.DataSource
import coil.request.Request
import coil.size.Scale
import com.droibit.looking2.R
import com.droibit.looking2.timeline.databinding.ListItemPhotoBinding
import dagger.Lazy

class PhotoListAdapter(
    context: Context,
    private val lifecycleOwner: Lazy<LifecycleOwner>,
    private val photoUrls: List<String>
) : RecyclerView.Adapter<PhotoListAdapter.ViewHolder>(), LifecycleObserver {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = photoUrls.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            lifecycleOwner.get(),
            binding = ListItemPhotoBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.update(photoUrls[position])
    }

    class ViewHolder(
        private val lifecycleOwner: LifecycleOwner,
        private val binding: ListItemPhotoBinding
    ) :
        RecyclerView.ViewHolder(binding.root), Request.Listener {

        fun update(url: String) {
            binding.photo.load(url) {
                error(R.drawable.ic_full_sad)
                scale(Scale.FIT)
                lifecycle(lifecycleOwner)
                listener(this@ViewHolder)
            }
        }

        override fun onStart(data: Any) {
            binding.loadingInProgress = true
        }

        override fun onError(data: Any, throwable: Throwable) {
            binding.loadingInProgress = false
        }

        override fun onSuccess(data: Any, source: DataSource) {
            binding.loadingInProgress = false
        }
    }
}