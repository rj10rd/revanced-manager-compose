package app.revanced.manager.ui.viewmodel

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.revanced.manager.patcher.PatcherUtils
import app.revanced.manager.ui.Resource
import app.revanced.manager.util.tag
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import io.sentry.Sentry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class AppSelectorViewModel(
    val app: Application, patcherUtils: PatcherUtils
) : ViewModel() {

    val filteredApps = mutableListOf<ApplicationInfo>()
    val patches = patcherUtils.patches
    private val selectedAppPackage = patcherUtils.selectedAppPackage
    private val selectedPatches = patcherUtils.selectedPatches

    init {
        viewModelScope.launch { filterApps() }
    }

    private suspend fun filterApps() = withContext(Dispatchers.Main) {
        try {
            val (patches) = patches.value as Resource.Success
            patches.forEach patch@{ patch ->
                patch.compatiblePackages?.forEach { pkg ->
                    try {
                        if (!(filteredApps.any { it.packageName == pkg.name })) {
                            val appInfo = app.packageManager.getApplicationInfo(pkg.name, 0)
                            filteredApps.add(appInfo)
                            return@forEach
                        }
                    } catch (e: Exception) {
                        return@forEach
                    }
                }
            }
            Log.d(tag, "Filtered apps.")
        } catch (e: Exception) {
            Log.e(tag, "An error occurred while filtering", e)
            Sentry.captureException(e)
        }
    }

    fun applicationLabel(info: ApplicationInfo): String {
        return app.packageManager.getApplicationLabel(info).toString()
    }

    fun loadIcon(info: ApplicationInfo): Drawable? {
        return info.loadIcon(app.packageManager)
    }

    fun setSelectedAppPackage(appId: ApplicationInfo) {
        selectedAppPackage.value.ifPresent { s ->
            if (s != appId) selectedPatches.clear()
        }
        selectedAppPackage.value = Optional.of(appId)
    }

    fun setSelectedAppPackageFromFile(file: Uri?) {
        try {
            val apkDir = app.cacheDir.resolve(File(file!!.path!!).name)
            app.contentResolver.openInputStream(file)!!.run {
                copyTo(apkDir.outputStream())
                close()
            }
            setSelectedAppPackage(
                app.packageManager.getPackageArchiveInfo(
                    apkDir.path, PackageManager.GET_META_DATA
                )!!.applicationInfo
            )
        } catch (e: Exception) {
            Log.e(tag, "Failed to load apk", e)
            Sentry.captureException(e)
        }
    }
}