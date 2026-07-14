/*
 * Copyright 2020-2026 Yury Kharchenko
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

package ru.woesss.j2me.installer;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import org.acra.ACRA;
import org.acra.ErrorReporter;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.playsoftware.j2meloader.R;
import ru.playsoftware.j2meloader.applist.AppItem;
import ru.playsoftware.j2meloader.applist.AppListModel;
import ru.playsoftware.j2meloader.config.Config;
import ru.playsoftware.j2meloader.databinding.FragmentInstallerBinding;
import ru.playsoftware.j2meloader.databinding.RowInstallerStepBinding;
import ru.playsoftware.j2meloader.util.Constants;
import ru.playsoftware.j2meloader.util.FileUtils;
import ru.woesss.j2me.jar.Descriptor;

public class InstallerDialog extends DialogFragment {
	private static final String ARG_URI = "InstallerDialog.uri";
	private static final String ARG_ID = "InstallerDialog.id";
	private final CompositeDisposable compositeDisposable = new CompositeDisposable();

	private FragmentInstallerBinding binding;
	private TextView btnPrimary;
	private TextView btnSecondary;
	private TextView btnTertiary;
	private AppListModel appListModel;
	private AppInstaller installer;
	private AlertDialog dialog;
	private RowInstallerStepBinding[] stepRows;
	private ValueAnimator convertAnimator;
	private long installStartTime;

	/**
	 * @param uri original uri from intent.
	 * @return A new instance of fragment InstallerDialog.
	 */
	public static InstallerDialog newInstance(Uri uri) {
		InstallerDialog fragment = new InstallerDialog();
		Bundle args = new Bundle();
		args.putParcelable(ARG_URI, uri);
		fragment.setArguments(args);
		fragment.setCancelable(false);
		return fragment;
	}

	public static InstallerDialog newInstance(int id) {
		InstallerDialog fragment = new InstallerDialog();
		Bundle args = new Bundle();
		args.putInt(ARG_ID, id);
		fragment.setArguments(args);
		fragment.setCancelable(false);
		return fragment;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		appListModel = new ViewModelProvider(requireActivity()).get(AppListModel.class);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			dismissAllowingStateLoss();
		}
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		binding = FragmentInstallerBinding.inflate(getLayoutInflater());
		dialog = new AlertDialog.Builder(requireActivity(), getTheme())
				.setView(binding.getRoot())
				.setCancelable(false)
				.create();
		return dialog;
	}

	@Override
	public void onDismiss(@NonNull DialogInterface dialog) {
		super.onDismiss(dialog);
		binding = null;
	}

	@Override
	public void onDestroy() {
		compositeDisposable.dispose();
		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();
		if (installer != null) {
			return;
		}
		btnPrimary = binding.btnActionPrimary;
		btnSecondary = binding.btnActionSecondary;
		btnTertiary = binding.btnActionTertiary;
		binding.ivIcon.setImageResource(R.mipmap.ic_launcher);
		binding.tvDialogTitle.setText(R.string.midlet_installer_title);
		binding.tvMessage.setText("");
		hideButtons();
		stepRows = new RowInstallerStepBinding[]{
				binding.stepExtract, binding.stepManifest, binding.stepConvert,
				binding.stepCache, binding.stepFinalize
		};
		int[] stepLabels = {
				R.string.installer_step_extract, R.string.installer_step_manifest,
				R.string.installer_step_convert, R.string.installer_step_cache,
				R.string.installer_step_finalize
		};
		for (int i = 0; i < stepRows.length; i++) {
			stepRows[i].label.setText(stepLabels[i]);
		}
		binding.btnClose.setOnClickListener(v -> {
			compositeDisposable.clear();
			if (convertAnimator != null) {
				convertAnimator.cancel();
			}
			if (installer != null) {
				installer.deleteTemp();
				installer.clearCache();
			}
			dismiss();
		});
		Bundle args = requireArguments();
		Uri uri = args.getParcelable(ARG_URI);
		if (uri != null) {
			installApp(null, uri);
			return;
		}
		int id = args.getInt(ARG_ID);
		reinstallApp(id);
	}

	private void installApp(File jar, Uri uri) {
		installer = new AppInstaller(jar, uri, appListModel);
		btnSecondary.setText(android.R.string.cancel);
		btnSecondary.setOnClickListener(v -> {
			installer.deleteTemp();
			installer.clearCache();
			dismiss();
		});
		Disposable disposable = Single.create(installer::loadInfo)
				.subscribeOn(Schedulers.computation())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(this::onProgress, this::onError);
		compositeDisposable.add(disposable);
	}

	private void reinstallApp(int id) {
		installer = new AppInstaller(id, appListModel);
		btnSecondary.setText(android.R.string.cancel);
		btnSecondary.setOnClickListener(v -> {
			installer.deleteTemp();
			installer.clearCache();
			dismiss();
		});
		Disposable disposable = Single.create(installer::loadInfo)
				.subscribeOn(Schedulers.computation())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(this::onProgress, this::onError);
		compositeDisposable.add(disposable);
	}

	private void hideProgress() {
		binding.progressGroup.setVisibility(View.GONE);
		binding.stepsCard.setVisibility(View.GONE);
		binding.tipCard.setVisibility(View.GONE);
		if (convertAnimator != null) {
			convertAnimator.cancel();
			convertAnimator = null;
		}
	}

	private void showProgress() {
		binding.progressGroup.setVisibility(View.VISIBLE);
		binding.stepsCard.setVisibility(View.VISIBLE);
		binding.tipCard.setVisibility(View.VISIBLE);
	}

	private void hideButtons() {
		btnPrimary.setVisibility(View.GONE);
		btnSecondary.setVisibility(View.GONE);
		btnTertiary.setVisibility(View.GONE);
	}

	private void showButtons() {
		btnPrimary.setVisibility(View.VISIBLE);
		btnSecondary.setVisibility(View.VISIBLE);
	}

	private void convert() {
		Descriptor nd = installer.getNewDescriptor();
		bindDescriptor(nd);
		binding.tvMessage.setVisibility(View.GONE);
		binding.tvDialogTitle.setText(R.string.installer_title_installing);
		File jar = installer.getJar();
		if (jar != null && jar.exists()) {
			binding.rowSize.setVisibility(View.VISIBLE);
			binding.tvSize.setText(getString(R.string.installer_size_value, formatSize(jar.length())));
		} else {
			binding.rowSize.setVisibility(View.GONE);
		}
		resetSteps();
		binding.progressGroup.setVisibility(View.VISIBLE);
		binding.stepsCard.setVisibility(View.VISIBLE);
		binding.tipCard.setVisibility(View.VISIBLE);
		binding.progress.setProgress(0);
		binding.tvPercent.setText("0%");
		installStartTime = System.currentTimeMillis();
		hideButtons();
		installer.setProgressListener(this::onInstallStep);
		Disposable disposable = Single.create(installer::install)
				.subscribeOn(Schedulers.computation())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(this::onProgress, this::onError);
		compositeDisposable.add(disposable);
	}

	private String formatSize(long bytes) {
		double mb = bytes / (1024.0 * 1024.0);
		if (mb < 0.1) {
			return String.format(Locale.getDefault(), "%.0f KB", bytes / 1024.0);
		}
		return String.format(Locale.getDefault(), "%.2f MB", mb);
	}

	private void resetSteps() {
		for (RowInstallerStepBinding row : stepRows) {
			row.dotBg.setBackgroundResource(R.drawable.bg_installer_step_dot_pending);
			row.dotCheck.setVisibility(View.GONE);
			row.label.setTextColor(0xFF7B84A6);
			row.status.setText(R.string.installer_status_pending);
			row.status.setTextColor(0xFF7B84A6);
		}
	}

	private void onInstallStep(int stepIndex, int percent) {
		if (!isAdded() || binding == null) {
			return;
		}
		requireActivity().runOnUiThread(() -> {
			if (binding == null) {
				return;
			}
			if (convertAnimator != null) {
				convertAnimator.cancel();
				convertAnimator = null;
			}
			applyStepUi(stepIndex, percent);
			if (stepIndex == InstallProgressListener.STEP_CONVERT) {
				// The JAR->DEX conversion doesn't expose granular progress, so we
				// animate an estimate between this step's start and the next one's
				// start; it gets cancelled/snapped the moment the real next step arrives.
				convertAnimator = ValueAnimator.ofInt(percent, 84);
				convertAnimator.setDuration(12000);
				convertAnimator.addUpdateListener(a -> {
					if (binding == null) return;
					applyProgressValue((int) a.getAnimatedValue());
				});
				convertAnimator.start();
			}
		});
	}

	private void applyStepUi(int stepIndex, int percent) {
		for (int i = 0; i < stepRows.length; i++) {
			RowInstallerStepBinding row = stepRows[i];
			if (i < stepIndex) {
				row.dotBg.setBackgroundResource(R.drawable.bg_installer_step_dot_done);
				row.dotCheck.setVisibility(View.VISIBLE);
				row.label.setTextColor(0xFFC9D0E8);
				row.status.setText(R.string.installer_status_done);
				row.status.setTextColor(0xFFA221CC);
			} else if (i == stepIndex) {
				row.dotBg.setBackgroundResource(R.drawable.bg_installer_step_dot_active);
				row.dotCheck.setVisibility(View.GONE);
				row.label.setTextColor(0xFFFFFFFF);
				row.status.setText(R.string.installer_status_active);
				row.status.setTextColor(0xFF4F8CFF);
			} else {
				row.dotBg.setBackgroundResource(R.drawable.bg_installer_step_dot_pending);
				row.dotCheck.setVisibility(View.GONE);
				row.label.setTextColor(0xFF7B84A6);
				row.status.setText(R.string.installer_status_pending);
				row.status.setTextColor(0xFF7B84A6);
			}
		}
		binding.tvStatus.setText(stepIndex == InstallProgressListener.STEP_CONVERT
				? getString(R.string.installer_converting_files)
				: stepRows[stepIndex].label.getText());
		applyProgressValue(percent);
	}

	private void applyProgressValue(int percent) {
		if (binding == null) {
			return;
		}
		binding.progress.setProgress(percent);
		binding.tvPercent.setText(percent + "%");
		long elapsed = System.currentTimeMillis() - installStartTime;
		if (percent > 2 && elapsed > 300) {
			double total = elapsed / (percent / 100.0);
			long remainingSec = Math.max(0, (long) ((total - elapsed) / 1000));
			binding.tvTimeRemaining.setText(getString(R.string.installer_time_remaining,
					String.format(Locale.getDefault(), "%02d:%02d", remainingSec / 60, remainingSec % 60)));
		} else {
			binding.tvTimeRemaining.setText(getString(R.string.installer_time_remaining, "--:--"));
		}
	}

	private void alertConfirm(SpannableStringBuilder message,
							  View.OnClickListener positive) {
		hideProgress();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		binding.tvMessage.setVisibility(View.VISIBLE);
		binding.tvMessage.setText(message);
		btnPrimary.setOnClickListener(positive);
		showButtons();
	}

	private void onProgress(@NonNull Integer status) {
		if (!isAdded()) {
			return;
		}
		if (status == AppInstaller.STATUS_SUCCESS) {
			hideProgress();
			binding.tvDialogTitle.setText(R.string.midlet_installer_title);
			AppItem app = installer.getExistsApp();
			Drawable drawable = Drawable.createFromPath(app.getImagePathExt());
			if (drawable != null) {
				binding.ivIcon.setImageDrawable(drawable);
			}
			binding.tvName.setText(app.getTitle());
			binding.tvMessage.setVisibility(View.VISIBLE);
			binding.tvMessage.setText(R.string.install_done);
			btnPrimary.setText(R.string.START_CMD);
			btnPrimary.setOnClickListener(v -> {
				Config.startApp(v.getContext(), app.getTitle(), app.getPathExt());
				dismiss();
			});
			btnSecondary.setText(R.string.close);
			btnSecondary.setOnClickListener(v -> dismiss());
			showButtons();
			return;
		}
		Descriptor nd = installer.getNewDescriptor();
		SpannableStringBuilder message;
		switch (status) {
			case AppInstaller.STATUS_NEW -> {
				if (installer.getJar() != null) {
					convert();
					return;
				}
				message = nd.getInfo(requireActivity());
			}
			case AppInstaller.STATUS_OLDER -> message = new SpannableStringBuilder(getString(
					R.string.reinstall_older,
					nd.getVersion(),
					installer.getCurrentVersion()));
			case AppInstaller.STATUS_EQUAL -> {
				message = new SpannableStringBuilder(getString(R.string.reinstall));
				AppItem app = installer.getExistsApp();
				btnTertiary.setVisibility(View.VISIBLE);
				btnTertiary.setOnClickListener(v -> {
					installer.clearCache();
					installer.deleteTemp();
					Config.startApp(v.getContext(), app.getTitle(), app.getPathExt());
					dismiss();
				});
			}
			case AppInstaller.STATUS_NEWER -> message = new SpannableStringBuilder(getString(
					R.string.reinstall_newest,
					nd.getVersion(),
					installer.getCurrentVersion()));
			case AppInstaller.STATUS_UNMATCHED -> {
				SpannableStringBuilder info = installer.getManifest().getInfo(requireActivity());
				info.append(getString(R.string.install_jar_non_matched_jad));
				alertConfirm(info, v -> installApp(installer.getJar(), null));
				return;
			}
			case AppInstaller.STATUS_SAME -> {
				installer.clearCache();
				installer.deleteTemp();
				AppItem app = installer.getExistsApp();
				Config.startApp(getContext(), app.getTitle(), app.getPathExt());
				dismiss();
				return;
			}
			default -> throw new IllegalStateException("Unexpected value: " + status);
		}
		if (installer.getJar() == null) {
			message.append('\n').append(getString(R.string.warn_install_from_net));
		}
		Drawable drawable = Drawable.createFromPath(installer.getIconPath());
		if (drawable != null) {
			binding.ivIcon.setImageDrawable(drawable);
		}
		bindDescriptor(nd);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		binding.tvMessage.setVisibility(View.VISIBLE);
		binding.tvMessage.setText(message);
		btnPrimary.setText(R.string.install);
		btnPrimary.setOnClickListener(v -> convert());
		hideProgress();
		showButtons();
	}

	private void bindDescriptor(@NonNull Descriptor descriptor) {
		binding.tvName.setText(descriptor.getName());
		binding.tvVendor.setText(descriptor.getVendor());
		binding.tvVersion.setText(getString(R.string.installer_version_value, descriptor.getVersion()));
	}

	private void onError(Throwable e) {
		Log.e("Installer", e.toString(), e);
		ErrorReporter errorReporter = ACRA.getErrorReporter();
		Bundle args = getArguments();
		if (args != null) {
			String report = errorReporter.getCustomData(Constants.KEY_APPCENTER_ATTACHMENT);
			StringBuilder sb = new StringBuilder();
			if (report != null) {
				sb.append(report);
			}
			sb.append("\n====================Installer==================\n");
			Uri uri = args.getParcelable(ARG_URI);
			if (uri != null) {
				sb.append("from uri: ").append(uri).append('\n');
			}
			Descriptor descriptor = installer.getNewDescriptor();
			if (descriptor != null) {
				sb.append(Descriptor.MIDLET_NAME).append(": ").append(descriptor.getName()).append("\n");
				sb.append(Descriptor.MIDLET_VENDOR).append(": ").append(descriptor.getVendor()).append("\n");
				sb.append(Descriptor.MIDLET_VERSION).append(": ").append(descriptor.getVersion()).append("\n");
			}
			File jar = installer.getJar();
			if (jar != null) {
				String jarSize = Long.toString(jar.length());
				sb.append(Descriptor.MIDLET_JAR_SIZE).append(": ").append(jarSize).append("\n");
				try {
					byte[] bytes = FileUtils.getBytes(jar);
					byte[] sum = MessageDigest.getInstance("md5").digest(bytes);
					BigInteger bi = new BigInteger(1, sum);
					String jarHash = bi.toString(16);
					sb.append("JAR_HASH_MD5").append(": ").append(jarHash);
				} catch (IOException ignored) {
				} catch (NoSuchAlgorithmException ignored) {
				}
			}
			errorReporter.putCustomData(Constants.KEY_APPCENTER_ATTACHMENT, sb.toString());
		}

		errorReporter.handleException(e);
		installer.clearCache();
		installer.deleteTemp();
		if (!isAdded()) {
			return;
		}
		hideProgress();
		dismissAllowingStateLoss();
	}
}
