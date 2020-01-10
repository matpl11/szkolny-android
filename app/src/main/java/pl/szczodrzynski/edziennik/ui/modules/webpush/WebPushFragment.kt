/*
 * Copyright (c) Kuba Szczodrzyński 2019-12-19.
 */

package pl.szczodrzynski.edziennik.ui.modules.webpush

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*
import pl.szczodrzynski.edziennik.*
import pl.szczodrzynski.edziennik.data.api.szkolny.SzkolnyApi
import pl.szczodrzynski.edziennik.data.api.szkolny.response.WebPushResponse
import pl.szczodrzynski.edziennik.databinding.WebPushFragmentBinding
import pl.szczodrzynski.edziennik.ui.dialogs.QrScannerDialog
import pl.szczodrzynski.edziennik.utils.SimpleDividerItemDecoration
import pl.szczodrzynski.edziennik.utils.Themes
import kotlin.coroutines.CoroutineContext

class WebPushFragment : Fragment(), CoroutineScope {
    companion object {
        private const val TAG = "TemplateFragment"
    }

    private lateinit var app: App
    private lateinit var activity: MainActivity
    private lateinit var b: WebPushFragmentBinding

    private val job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private lateinit var adapter: WebPushBrowserAdapter
    private val api by lazy {
        SzkolnyApi(app)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity = (getActivity() as MainActivity?) ?: return null
        context ?: return null
        app = activity.application as App
        context!!.theme.applyStyle(Themes.appTheme, true)
        if (app.profile == null)
            return inflater.inflate(R.layout.fragment_loading, container, false)
        // activity, context and profile is valid
        b = WebPushFragmentBinding.inflate(inflater)
        return b.root
    }

    @SuppressLint("DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // TODO check if app, activity, b can be null
        if (app.profile == null || !isAdded)
            return

        b.scanQrCode.onClick {
            val result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
            if (result == PackageManager.PERMISSION_GRANTED) {
                QrScannerDialog(activity, {
                    b.tokenEditText.setText(it.crc32().toString(36).toUpperCase())
                    pairBrowser(browserId = it)
                })
            } else {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), 1)
            }
        }

        b.tokenAccept.onClick {
            val pairToken = b.tokenEditText.text.toString().toUpperCase()
            if (!"[0-9A-Z]{3,13}".toRegex().matches(pairToken)) {
                b.tokenLayout.error = app.getString(R.string.web_push_token_invalid)
                return@onClick
            }
            b.tokenLayout.error = null
            b.tokenEditText.setText(pairToken)
            pairBrowser(pairToken = pairToken)
        }

        adapter = WebPushBrowserAdapter(
                activity,
                onItemClick = null,
                onUnpairButtonClick = {
                   unpairBrowser(it.browserId)
                }
        )

        launch {
            val browsers = withContext(Dispatchers.Default) {
                api.listBrowsers()
            }
            updateBrowserList(browsers)
        }
    }

    private fun updateBrowserList(browsers: List<WebPushResponse.Browser>) {
        adapter.items = browsers
        if (b.browsersView.adapter == null) {
            b.browsersView.adapter = adapter
            b.browsersView.apply {
                isNestedScrollingEnabled = false
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(SimpleDividerItemDecoration(context))
            }
        }
        adapter.notifyDataSetChanged()

        if (browsers.isNotEmpty()) {
            b.browsersView.visibility = View.VISIBLE
            b.browsersNoData.visibility = View.GONE
        } else {
            b.browsersView.visibility = View.GONE
            b.browsersNoData.visibility = View.VISIBLE
        }
    }

    private fun pairBrowser(browserId: String? = null, pairToken: String? = null) {
        b.scanQrCode.isEnabled = false
        b.tokenAccept.isEnabled = false
        b.tokenEditText.isEnabled = false
        b.tokenEditText.clearFocus()
        launch {
            val browsers = withContext(Dispatchers.Default) {
                api.pairBrowser(browserId, pairToken)
            }
            b.scanQrCode.isEnabled = true
            b.tokenAccept.isEnabled = true
            b.tokenEditText.isEnabled = true
            updateBrowserList(browsers)
        }
    }

    private fun unpairBrowser(browserId: String) {
        launch {
            val browsers = withContext(Dispatchers.Default) {
                api.unpairBrowser(browserId)
            }
            updateBrowserList(browsers)
        }
    }
}