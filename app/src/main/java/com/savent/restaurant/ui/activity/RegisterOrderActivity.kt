package com.savent.restaurant.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.mazenrashed.printooth.ui.ScanningActivity
import com.savent.restaurant.isNeededRequestAndroid12BLEPermission
import com.savent.restaurant.toast
import com.savent.restaurant.ui.screen.orders.OrderNavEvent
import com.savent.restaurant.ui.screen.orders.RegisterOrderScreen
import com.savent.restaurant.ui.screen.orders.orderNavEvent
import com.savent.restaurant.ui.theme.SaventRestaurantTheme
import com.savent.restaurant.ui.viewmodel.OrdersViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterOrderActivity : ComponentActivity() {
    private var saveCurrentPrinter: () -> Unit = { }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ordersViewModel by inject<OrdersViewModel>()
        setContent {
            SaventRestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    val state by ordersViewModel.state.collectAsStateWithLifecycle()
                    RegisterOrderScreen(
                        state = state,
                        onEvent = ordersViewModel::onEvent,
                        navEvent = {
                            orderNavEvent(
                                navController = navController,
                                navEvent = it
                            )
                        })
                    LaunchedEffect(Unit) {
                        async {
                            ordersViewModel.message.flowWithLifecycle(
                                lifecycle,
                                Lifecycle.State.STARTED
                            ).collectLatest { message ->
                                toast(message)
                            }
                        }
                        async {
                            ordersViewModel.event.flowWithLifecycle(
                                lifecycle,
                                Lifecycle.State.STARTED
                            ).collectLatest { event ->
                                when (event) {
                                    OrdersViewModel.Event.OrderCreated -> orderNavEvent(
                                        navController = navController,
                                        navEvent = OrderNavEvent.Back
                                    )
                                    OrdersViewModel.Event.OrderRegistered -> orderNavEvent(
                                        navController = navController,
                                        navEvent = OrderNavEvent.Back
                                    )
                                    OrdersViewModel.Event.ScanPrinterDevices -> {
                                        if (isNeededRequestAndroid12BLEPermission())
                                            return@collectLatest
                                        saveCurrentPrinter =
                                            ordersViewModel::saveCurrentPrinter
                                        resultLauncher.launch(
                                            Intent(
                                                this@RegisterOrderActivity,
                                                ScanningActivity::class.java,
                                            )
                                        )
                                    }
                                    OrdersViewModel.Event.OrderNotFound -> finish()
                                }
                            }
                        }

                    }
                }
            }
        }
    }
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == ScanningActivity.SCANNING_FOR_PRINTER || result.resultCode == Activity.RESULT_OK) {
                saveCurrentPrinter ()

            }
        }
}

@Composable
fun Greeting4(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview4() {
    SaventRestaurantTheme {
        Greeting4("Android")
    }
}