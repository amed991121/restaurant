package com.savent.restaurant.ui.screen.sales

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savent.restaurant.R
import com.savent.restaurant.ui.component.SearchBar
import com.savent.restaurant.ui.model.sale.SaleItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun SalesListScreen(
    state: SalesState,
    onEvent: (SalesEvent) -> Unit,
    pullRefreshState: PullRefreshState
) {

    val sheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    val scope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()

    var dialog by remember { mutableStateOf(SalesDialog.ShareReceiptDialog) }

    var idReceiptToShare = 0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.91f)
            .pullRefresh(pullRefreshState)
    ) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                when (dialog) {
                    SalesDialog.ShareReceiptDialog ->
                        ShareReceiptDialog(
                            methods = state.shareReceiptMethods,
                            onClose = { scope.launch { sheetState.collapse() } },
                            onClick = {
                                onEvent(
                                    SalesEvent.ShareReceipt(
                                        orderId = idReceiptToShare,
                                        method = it
                                    )
                                )
                                scope.launch { sheetState.collapse() }
                            },
                            printerLongClick = {
                                scope.launch {
                                    sheetState.collapse()
                                    dialog = SalesDialog.SelectPrintDeviceDialog
                                    delay(100)
                                    sheetState.expand()
                                }
                            }
                        )
                    SalesDialog.SelectPrintDeviceDialog ->
                        RemoveDeviceDialog(onClose = {
                            scope.launch {
                                sheetState.collapse()
                                delay(200)
                                dialog = SalesDialog.ShareReceiptDialog
                            }
                        }) {
                            onEvent(SalesEvent.RemovePrintDevice)
                            scope.launch {
                                sheetState.collapse()
                                delay(200)
                                dialog = SalesDialog.ShareReceiptDialog
                            }

                        }
                }


            },
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            sheetBackgroundColor = Color.White,
            sheetPeekHeight = 0.dp
        ) {
            Column(modifier = Modifier.padding(top = 15.dp, start = 15.dp, end = 15.dp)) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_order),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = stringResource(id = R.string.sales),
                        style = MaterialTheme.typography.h3.copy(fontFamily = FontFamily.Cursive),
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                SearchBar(
                    hint = stringResource(id = R.string.search),
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Thin,
                        fontStyle = FontStyle.Italic
                    )
                ) {
                    onEvent(SalesEvent.Search(it))
                }
                Spacer(modifier = Modifier.padding(top = 20.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(
                        count = state.sales.size,
                        key = { state.sales[it].id },
                        itemContent = {
                            Box(modifier = Modifier.animateItemPlacement(tween())) {
                                SaleItem(
                                    sale = state.sales[it],
                                    onShareReceipt = {
                                        scope.launch { sheetState.expand() }
                                        idReceiptToShare = state.sales[it].id
                                    })
                            }
                        })
                }
            }
        }
        PullRefreshIndicator(
            refreshing = state.isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(
                Alignment.TopCenter
            )
        )
    }


}

private enum class SalesDialog {
    ShareReceiptDialog, SelectPrintDeviceDialog
}