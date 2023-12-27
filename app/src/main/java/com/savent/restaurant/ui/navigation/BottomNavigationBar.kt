package com.savent.restaurant.ui.navigation

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.savent.restaurant.ui.theme.DarkBlue

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val items = listOf(
        BottomNavItem.FoodMenu,
        BottomNavItem.Orders,
        BottomNavItem.Sales
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Card(elevation = 30.dp, shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                AddItem(
                    navItem = item,
                    currentRoute = currentRoute,
                    navController = navController
                )
            }
        }
    }


}

@Composable
fun AddItem(
    navItem: BottomNavItem,
    currentRoute: String?,
    navController: NavController
) {
    val selected = currentRoute?.contains(navItem.route)?:false

    val background = if (selected) DarkBlue else Color.Transparent
    val iconColor = if (selected) Color.White else Color.Gray
    Box(
        modifier = Modifier
            .padding(15.dp)
            .requiredHeight(45.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(background)
            .clickable(onClick = {
                navController.navigate(navItem.route) {
                    // Pop up to the start destination of the graph to
                    // avoid building up a large stack of destinations
                    // on the back stack as users select items
                    navController.graph.startDestinationRoute?.let { route ->
                        popUpTo(route) {
                            saveState = true
                        }
                    }
                    // Avoid multiple copies of the same destination when re-selecting the same item
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
                    restoreState = true
                }
            })
    ) {
        Row(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Icon(
                painter = painterResource(id = navItem.iconResId),
                contentDescription = "icon",
                tint = iconColor,
                modifier = Modifier.fillMaxHeight()
            )

            AnimatedVisibility(visible = selected) {
                Text(
                    text = stringResource(id = navItem.titleResId),
                    fontSize = 15.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
