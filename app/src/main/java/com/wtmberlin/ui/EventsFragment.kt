package com.wtmberlin.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wtmberlin.R
import com.wtmberlin.databinding.EventsScreenBinding
import com.wtmberlin.util.AdapterItem
import com.wtmberlin.util.BindingViewHolder
import com.wtmberlin.util.DIFF_CALLBACK
import com.wtmberlin.util.observeNotHandled
import kotlinx.android.synthetic.main.events_screen.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDateTime

class EventsFragment : Fragment(), EventsAdapter.Callbacks {
    private val viewModel: EventsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =
            DataBindingUtil
                .inflate<EventsScreenBinding>(
                    inflater,
                    R.layout.events_screen,
                    container,
                    false
                )

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemOffset = resources.getDimensionPixelOffset(R.dimen.material_space_half)

        events_recycler.adapter = EventsAdapter(this)
        events_recycler.addItemDecoration(OffsetItemDecoration(itemOffset))

        swipe_refresh.setOnRefreshListener { viewModel.refreshEvents() }
        swipe_refresh.setColorSchemeResources(
            R.color.wtmBlueDark,
            R.color.wtmBlueLight,
            R.color.wtmGreenLight,
            R.color.wtmGreenLight
        )

        viewModel.displayEventDetails.observeNotHandled(this) { startEventDetailsScreen(it.eventId) }
    }

    override fun onEventItemClicked(item: EventItem) {
        viewModel.onEventItemClicked(item)
    }

    private fun startEventDetailsScreen(eventId: String) {
        findNavController().navigate(EventsFragmentDirections.startEventDetailsScreen(eventId))
    }
}

class EventsAdapter(private val callbacks: Callbacks) : ListAdapter<AdapterItem, BindingViewHolder>(DIFF_CALLBACK) {
    override fun getItemViewType(position: Int) = getItem(position).viewType

    override fun onCreateViewHolder(parent: ViewGroup, @LayoutRes viewType: Int): BindingViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding =
            DataBindingUtil
                .inflate<ViewDataBinding>(
                    inflater,
                    viewType,
                    parent,
                    false
                )

        return BindingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        holder.bind(getItem(position), callbacks)
    }

    interface Callbacks {
        fun onEventItemClicked(item: EventItem)
    }
}

sealed class EventsAdapterItem : AdapterItem

object UpcomingHeaderItem : EventsAdapterItem() {
    override val id = 1L
    override val viewType = R.layout.events_upcoming_header
}

object PastHeaderItem : EventsAdapterItem() {
    override val id = 1L
    override val viewType = R.layout.events_past_header
}

data class EventItem(
    override val id: String,
    val name: String,
    val localDateTime: LocalDateTime,
    val venueName: String?
) : EventsAdapterItem() {
    override val viewType = R.layout.events_event_item
}

object NoUpcomingEventsItem : EventsAdapterItem() {
    override val id = 1L
    override val viewType = R.layout.events_no_upcoming_events_item
}

class OffsetItemDecoration(private val offset: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        parent.adapter?.let {
            val itemCount = it.itemCount
            val position = parent.getChildAdapterPosition(view)

            if (position != RecyclerView.NO_POSITION && position < itemCount - 1) {
                val thisViewType = it.getItemViewType(position)
                val nextViewType = it.getItemViewType(position + 1)

                if (thisViewType == R.layout.events_event_item && nextViewType == R.layout.events_event_item) {
                    outRect.set(0, 0, 0, offset)
                }
            }
        }
    }
}