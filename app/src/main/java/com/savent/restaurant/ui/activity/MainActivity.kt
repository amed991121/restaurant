package com.savent.restaurant.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.mazenrashed.printooth.ui.ScanningActivity
import com.savent.restaurant.isNeededRequestAndroid12BLEPermission
import com.savent.restaurant.toast
import com.savent.restaurant.ui.model.SharedReceipt
import com.savent.restaurant.ui.model.StaggeredColoredItem
import com.savent.restaurant.ui.navigation.BottomNavItem
import com.savent.restaurant.ui.navigation.BottomNavigationBar
import com.savent.restaurant.ui.navigation.Screen
import com.savent.restaurant.ui.screen.sales.SalesListScreen
import com.savent.restaurant.ui.screen.food.FoodScreen
import com.savent.restaurant.ui.screen.orders.*

import com.savent.restaurant.ui.theme.SaventRestaurantTheme
import com.savent.restaurant.ui.viewmodel.FoodViewModel
import com.savent.restaurant.ui.viewmodel.OrdersViewModel
import com.savent.restaurant.ui.viewmodel.SalesViewModel
import com.savent.restaurant.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.random.Random


class MainActivity : ComponentActivity() {
    private var saveCurrentPrinter: (() -> Unit) -> Unit = { }
    private var printReceipt: (Int) -> Unit = {}
    private var orderId = 0

    @OptIn(ExperimentalMaterialApi::class)
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaventRestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    val navController = rememberNavController()
                    Scaffold(
                        bottomBar = { BottomNavigationBar(navController) },
                    ) {
                        NavHost(navController, startDestination = BottomNavItem.FoodMenu.route) {

                            composable(BottomNavItem.FoodMenu.route) {
                                val foodViewModel by viewModel<FoodViewModel>()
                                val state by foodViewModel.state.collectAsStateWithLifecycle()
                                val pullRefreshState = rememberPullRefreshState(
                                    refreshing = state.isLoading,
                                    onRefresh = foodViewModel::refreshData
                                )
                                val sgcList =
                                    (0..state.categoriesListWithImage.size).map {
                                        StaggeredColoredItem(
                                            height = Random.nextInt(200, 270).dp,
                                            color = Color(Random.nextLong(0xFFFFFFFF)).copy(alpha = 0.15f)
                                        )
                                    }

                                FoodScreen(
                                    state = state,
                                    sgcList = sgcList,
                                    onEvent = foodViewModel::onEvent,
                                    pullRefreshState = pullRefreshState,
                                    onClearSession = {
                                        try {
                                            (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                                                .clearApplicationUserData()
                                            startActivity(
                                                Intent(
                                                    this@MainActivity,
                                                    LoginActivity::class.java
                                                )
                                            )
                                        } catch (e: Exception) {
                                            startActivity(
                                                Intent(
                                                    this@MainActivity,
                                                    LoginActivity::class.java
                                                )
                                            )
                                            finish()
                                        }

                                    }
                                )
                                LaunchedEffect(Unit) {
                                    foodViewModel.message.flowWithLifecycle(
                                        lifecycle,
                                        Lifecycle.State.STARTED
                                    ).collectLatest { message ->
                                        toast(message)
                                    }
                                }
                            }

                            navigation(
                                route = BottomNavItem.Orders.route,
                                startDestination = Screen.Orders.List.route,
                            ) {
                                val ordersViewModel by viewModel<OrdersViewModel>()
                                CoroutineScope(Dispatchers.Main).launch {
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
                                                OrdersViewModel.Event.OrderNotFound ->
                                                    if (navController.currentDestination?.route == Screen.Orders.Order.route)
                                                        orderNavEvent(
                                                            navController = navController,
                                                            navEvent = OrderNavEvent.Back
                                                        )
                                                else -> {}

                                            }
                                        }
                                    }

                                }

                                composable(Screen.Orders.List.route) {
                                    val state by ordersViewModel.state.collectAsStateWithLifecycle()

                                    val pullRefreshState = rememberPullRefreshState(
                                        refreshing = state.isLoading,
                                        onRefresh = ordersViewModel::refreshData
                                    )
                                    OrdersListScreen(
                                        state = state,
                                        onEvent = ordersViewModel::onEvent,
                                        navEvent = {
                                            orderNavEvent(
                                                navController = navController,
                                                navEvent = it
                                            )
                                        },
                                        pullRefreshState = pullRefreshState
                                    )

                                }

                                composable(Screen.Orders.Order.route) {
                                    val state by ordersViewModel.state.collectAsStateWithLifecycle()
                                    NewOrderScreen(
                                        state = state,
                                        onEvent = ordersViewModel::onEvent,
                                        navEvent = { navEvent ->
                                            orderNavEvent(
                                                navController = navController,
                                                navEvent = navEvent
                                            )
                                        }
                                    )
                                    BackHandler {
                                        orderNavEvent(
                                            navController = navController,
                                            navEvent = OrderNavEvent.Back
                                        )
                                    }
                                }

                                /*composable(Screen.Orders.Register.route) {
                                }*/


                            }
                            composable(Screen.Sales.route) {
                                val salesViewModel by viewModel<SalesViewModel>()
                                val state by salesViewModel.state.collectAsStateWithLifecycle()
                                val pullRefreshState = rememberPullRefreshState(
                                    refreshing = state.isLoading,
                                    onRefresh = salesViewModel::reloadSales
                                )
                                SalesListScreen(
                                    state = state,
                                    onEvent = salesViewModel::onEvent,
                                    pullRefreshState = pullRefreshState
                                )
                                LaunchedEffect(Unit) {
                                    salesViewModel.uiEvent.flowWithLifecycle(
                                        lifecycle,
                                        Lifecycle.State.STARTED
                                    ).collectLatest { event ->
                                        when (event) {
                                            is SalesViewModel.UiEvent.ScanPrinters -> {
                                                if (isNeededRequestAndroid12BLEPermission()) return@collectLatest
                                                saveCurrentPrinter =
                                                    salesViewModel::saveCurrentPrinter
                                                printReceipt = salesViewModel::printReceipt
                                                orderId = event.orderId
                                                resultLauncher.launch(
                                                    Intent(
                                                        this@MainActivity,
                                                        ScanningActivity::class.java,
                                                    )
                                                )
                                            }
                                            is SalesViewModel.UiEvent.ShareReceipt -> {

                                                when (event.sharedReceipt.method) {
                                                    SharedReceipt.Method.Sms ->
                                                        sendReceiptBySms(
                                                            phoneNumber = event.sharedReceipt.contact?.phoneNumber,
                                                            note = event.sharedReceipt.note
                                                        )
                                                    else -> {
                                                        when (val resultFile =
                                                            saveReceiptFile(event.sharedReceipt.note)) {
                                                            is Resource.Error -> toast(resultFile.message)
                                                            is Resource.Success -> {
                                                                if (event.sharedReceipt.method == SharedReceipt.Method.Whatsapp) {
                                                                    sendReceiptByWhatsApp(
                                                                        phoneNumber = event.sharedReceipt.contact?.phoneNumber,
                                                                        noteUri = GetUriFromFile(
                                                                            context = this@MainActivity,
                                                                            file = resultFile.data
                                                                        )
                                                                    )
                                                                    return@collectLatest
                                                                }
                                                                if (event.sharedReceipt.method == SharedReceipt.Method.Email) {
                                                                    sendReceiptByEmail(
                                                                        email = event.sharedReceipt.contact?.email,
                                                                        noteUri = GetUriFromFile(
                                                                            context = this@MainActivity,
                                                                            file = resultFile.data
                                                                        )
                                                                    )
                                                                    return@collectLatest
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            is SalesViewModel.UiEvent.ShowMessage ->
                                                toast(event.message)
                                        }

                                    }
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
                saveCurrentPrinter { printReceipt(orderId) }

            }
        }

}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SaventRestaurantTheme {
        Greeting("Android")
    }
}