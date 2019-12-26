/*
 * Copyright (c) Kuba Szczodrzyński 2019-11-13.
 */

package pl.szczodrzynski.edziennik.ui.modules.error

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.ColorUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import pl.szczodrzynski.edziennik.App
import pl.szczodrzynski.edziennik.R
import pl.szczodrzynski.edziennik.data.api.models.ApiError
import pl.szczodrzynski.edziennik.stackTraceString
import pl.szczodrzynski.navlib.getColorFromAttr

class ErrorSnackbar(val activity: AppCompatActivity) {
    companion object {
        private const val TAG = "ErrorSnackbar"
    }

    private var snackbar: Snackbar? = null
    private lateinit var coordinator: CoordinatorLayout
    private val errors = mutableListOf<ApiError>()

    fun setCoordinator(coordinatorLayout: CoordinatorLayout, showAbove: View? = null) {
        this.coordinator = coordinatorLayout
        snackbar = Snackbar.make(coordinator, R.string.snackbar_error_text, Snackbar.LENGTH_INDEFINITE)
        snackbar?.setAction(R.string.more) {
            if (errors.isNotEmpty()) {
                val message = errors.map {
                    if (App.devMode) it.throwable?.stackTraceString
                            ?: it.throwable?.localizedMessage ?: ""
                    else it.throwable?.localizedMessage
                }.joinToString("\n")

                MaterialAlertDialogBuilder(activity)
                        .setTitle(R.string.dialog_error_details_title)
                        .setMessage(message)
                        .setPositiveButton(R.string.ok) { dialog, _ ->
                            errors.clear()
                            dialog.dismiss()
                        }
                        .show()
            }
        }
        val bgColor = ColorUtils.compositeColors(
                getColorFromAttr(activity, R.attr.colorOnSurface) and 0xcfffffff.toInt(),
                getColorFromAttr(activity, R.attr.colorSurface)
        )
        snackbar?.setBackgroundTint(bgColor)
        showAbove?.let { snackbar?.anchorView = it }
    }

    fun addError(apiError: ApiError): ErrorSnackbar {
        errors += apiError
        snackbar?.setText(apiError.getStringReason(activity))
        return this
    }

    fun show() = snackbar?.show()
    fun dismiss() = snackbar?.dismiss()
}
