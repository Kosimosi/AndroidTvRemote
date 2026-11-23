// SPDX-License-Identifier: GPL-3.0-only

package app.tellyscope

import android.content.Context
import app.tellyscope.data.LauncherPick
import app.tellyscope.data.Updates
import app.tellyscope.data.AndroidLauncherDownload

/** This fork is distributed through its own GitHub Actions artifacts; never offer Tellyscope updates. */
internal fun updates(context: Context, version: String): Updates? = null

/** Kept for shared backend compatibility; launcher-management UI is not part of this app. */
internal fun launcherPick(context: Context): LauncherPick = AndroidLauncherDownload(context)
