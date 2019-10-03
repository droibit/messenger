package com.droibit.looking2.timeline.ui.content.photo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.RecyclerView
import com.droibit.looking2.R
import com.droibit.looking2.timeline.databinding.ListItemPhotoBinding
import com.squareup.picasso.Picasso
import com.squareup.picasso.Callback as PicassoCallback

class PhotoListAdapter(
    context: Context,
    private val photoUrls: List<String>
) : RecyclerView.Adapter<PhotoListAdapter.ViewHolder>(), LifecycleObserver {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = photoUrls.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            binding = ListItemPhotoBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.update(photoUrls[position])
    }

    class ViewHolder(private val binding: ListItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root), PicassoCallback {

        fun update(url: String) {
            Picasso.get()
                .load(url)
                .error(R.drawable.ic_full_sad)
                .tag(TAG_TWEET_PHOTO)
                .into(binding.photo, this)
                .also {
                    binding.loadingInProgress = true
                }
        }

        override fun onSuccess() {
            binding.loadingInProgress = false
        }

        override fun onError(e: Exception?) {
            binding.loadingInProgress = false
        }
    }

    companion object {
        private const val TAG_TWEET_PHOTO = "TAG_TWEET_PHOTO"
    }
}