/*
 * Copyright 2026
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.playsoftware.j2meloader.applist;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import ru.playsoftware.j2meloader.R;
import ru.playsoftware.j2meloader.databinding.ListRowCarouselJarBinding;

class AppsCarouselAdapter extends ListAdapter<AppItem, AppsCarouselAdapter.AppViewHolder> {
	interface OnItemActionListener extends View.OnCreateContextMenuListener {
		void onItemActivated(AppItem item);

		void onItemSelected(int position);
	}

	private final OnItemActionListener listener;
	private int selectedPosition = RecyclerView.NO_POSITION;

	AppsCarouselAdapter(OnItemActionListener listener) {
		super(new DiffUtil.ItemCallback<>() {
			@Override
			public boolean areItemsTheSame(@NonNull AppItem oldItem, @NonNull AppItem newItem) {
				return oldItem.getId() == newItem.getId();
			}

			@Override
			public boolean areContentsTheSame(@NonNull AppItem oldItem, @NonNull AppItem newItem) {
				return oldItem.getTitle().equals(newItem.getTitle()) &&
						oldItem.getVersion().equals(newItem.getVersion()) &&
						oldItem.getAuthor().equals(newItem.getAuthor());
			}
		});
		this.listener = listener;
	}

	@NonNull
	@Override
	public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		return new AppViewHolder(ListRowCarouselJarBinding.inflate(inflater, parent, false), listener);
	}

	@Override
	public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
		holder.bind(getItem(position), position == selectedPosition);
	}

	void setSelectedPosition(int position) {
		if (position == selectedPosition) {
			return;
		}
		int old = selectedPosition;
		selectedPosition = position;
		if (old != RecyclerView.NO_POSITION) {
			notifyItemChanged(old);
		}
		if (selectedPosition != RecyclerView.NO_POSITION) {
			notifyItemChanged(selectedPosition);
		}
	}

	static class AppViewHolder extends RecyclerView.ViewHolder {
		private final ImageView icon;
		private final TextView title;

		AppViewHolder(ListRowCarouselJarBinding binding, OnItemActionListener listener) {
			super(binding.getRoot());
			icon = binding.listImage;
			title = binding.listTitle;

			itemView.setOnClickListener(v -> {
				RecyclerView.Adapter<?> adapter = getBindingAdapter();
				if (!(adapter instanceof AppsCarouselAdapter carouselAdapter)) {
					return;
				}
				int position = getBindingAdapterPosition();
				if (position == RecyclerView.NO_POSITION) {
					return;
				}
				if (position == carouselAdapter.selectedPosition) {
					listener.onItemActivated(carouselAdapter.getItem(position));
				} else {
					listener.onItemSelected(position);
				}
			});
			itemView.setOnCreateContextMenuListener(listener);
		}

		void bind(AppItem item, boolean selected) {
			Drawable drawable = Drawable.createFromPath(item.getImagePathExt());
			if (drawable != null) {
				drawable.setFilterBitmap(true);
				icon.setImageDrawable(drawable);
			} else {
				icon.setImageResource(R.mipmap.ic_launcher);
			}
			title.setText(item.getTitle());
			itemView.setTag(item);
			itemView.setScaleX(selected ? 1.0f : 0.88f);
			itemView.setScaleY(selected ? 1.0f : 0.88f);
			itemView.setAlpha(selected ? 1.0f : 0.72f);
		}
	}
}
