//package com.example.spendmend.screens.components
//
//import androidx.compose.foundation.lazy.LazyListState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.snapshotFlow
//import kotlinx.coroutines.flow.distinctUntilChanged
//
//@Composable
//fun HideBottomBarOnScroll(
//    listState: LazyListState
//) {
//
//    val bottomBarState = LocalBottomBarState.current
//
//    LaunchedEffect(listState) {
//
//        var previousIndex = listState.firstVisibleItemIndex
//        var previousOffset = listState.firstVisibleItemScrollOffset
//
//        snapshotFlow {
//            Pair(
//                listState.firstVisibleItemIndex,
//                listState.firstVisibleItemScrollOffset
//            )
//        }
//            .distinctUntilChanged()
//            .collect { (index, offset) ->
//
//                val scrollingDown =
//                    index > previousIndex ||
//                            (index == previousIndex && offset > previousOffset)
//
//                val scrollingUp =
//                    index < previousIndex ||
//                            (index == previousIndex && offset < previousOffset)
//
//                if (scrollingDown) {
//                    bottomBarState.hide()
//                } else if (scrollingUp) {
//                    bottomBarState.show()
//                }
//
//                previousIndex = index
//                previousOffset = offset
//            }
//    }
//}
//
//
