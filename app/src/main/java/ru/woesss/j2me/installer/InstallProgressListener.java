package ru.woesss.j2me.installer;

/**
 * Reports real install milestones as they happen.
 * stepIndex: 0=extract, 1=manifest, 2=convert, 3=cache, 4=finalize.
 * percent is the overall (0-100) progress associated with reaching that step.
 * Note: step 2 (convert) covers the JAR->DEX conversion, which is done by a
 * third-party tool that doesn't expose granular internal progress, so the
 * caller only gets notified when that step starts and when it finishes -
 * anything shown while it's running in between is an estimate, not exact
 * byte-level progress.
 */
public interface InstallProgressListener {
	int STEP_EXTRACT = 0;
	int STEP_MANIFEST = 1;
	int STEP_CONVERT = 2;
	int STEP_CACHE = 3;
	int STEP_FINALIZE = 4;

	void onStep(int stepIndex, int percent);
}
